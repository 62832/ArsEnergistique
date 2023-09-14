plugins {
    eclipse
    idea
    alias(libs.plugins.forge)
    alias(libs.plugins.mixin)
    alias(libs.plugins.spotless)
}

val modId = "arseng"

version = (System.getenv("ARSENG_VERSION") ?: "v0.0.0").substring(1)
group = "gripe.90"
base.archivesName.set(modId)

java.toolchain.languageVersion.set(JavaLanguageVersion.of(17))

sourceSets {
    main {
        resources.srcDir("src/generated/resources")
    }

    create("data") {
        val main = main.get()
        compileClasspath += main.compileClasspath + main.output
        runtimeClasspath += main.runtimeClasspath + main.output
    }
}

minecraft {
    mappings("official", libs.versions.minecraft.get())

    copyIdeResources.set(true)

    runs {
        configureEach {
            workingDirectory(project.file("run"))
            property("forge.logging.markers", "REGISTRIES")
            property("forge.logging.console.level", "info")

            mods {
                create(modId) {
                    source(sourceSets.main.get())
                }
            }
        }

        create("client")

        create("server") {
            args("--nogui")
        }

        create("data") {
            args(
                "--mod", modId,
                "--all",
                "--output", file("src/generated/resources/"),
                "--existing", file("src/main/resources/"))

            mods {
                getByName(modId) {
                    source(sourceSets.getByName("data"))
                }
            }
        }
    }
}

repositories {
    maven {
        name = "ModMaven (K4U-NL)"
        url = uri("https://modmaven.dev/")
        content {
            includeGroup("appeng")
            includeGroup("top.theillusivec4.curios")
            includeGroup("mezz.jei")
        }
    }

    maven {
        name = "BlameJared"
        url = uri("https://maven.blamejared.com")
        content {
            includeGroup("com.hollingsworth.ars_nouveau")
            includeGroup("vazkii.patchouli")
        }
    }

    maven {
        name = "CurseMaven"
        url = uri("https://cursemaven.com")
        content {
            includeGroup("curse.maven")
        }
    }

    maven {
        name = "GeckoLib"
        url = uri("https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/")
    }

    maven {
        name = "JitPack"
        url = uri("https://jitpack.io")
    }
}

dependencies {
    minecraft(libs.forge)
    annotationProcessor(variantOf(libs.mixin) { classifier("processor") })

    implementation(fg.deobf(libs.ae2.get()))
    implementation(fg.deobf(libs.ars.get()))

    implementation(fg.deobf(libs.geckolib.get()))
    runtimeOnly(fg.deobf(libs.mixin.extras.get()))
    runtimeOnly(fg.deobf(libs.curios.get()))
    runtimeOnly(fg.deobf(libs.patchouli.get()))

    runtimeOnly(fg.deobf(libs.jei.get()))
    runtimeOnly(fg.deobf(libs.jade.get()))
}

mixin {
    add(sourceSets.main.get(), "$modId.refmap.json")
    config("$modId.mixins.json")
}

tasks {
    register("releaseInfo") {
        doLast {
            val output = System.getenv("GITHUB_OUTPUT")

            if (!output.isNullOrEmpty()) {
                val outputFile = File(output)
                outputFile.appendText("MOD_VERSION=$version\n")
                outputFile.appendText("MINECRAFT_VERSION=${libs.versions.minecraft.get()}\n")
            }
        }
    }

    processResources {
        exclude("**/.cache")

        val replaceProperties = mapOf(
            "version" to project.version,
            "fmlVersion" to "[${libs.versions.loader.get()},)",
            "ae2Version" to "(,${libs.versions.ae2.get().substringBefore('.').toInt() + 1})",
            "arsVersion" to "[${libs.versions.ars.get().substringBeforeLast('.')},)"
        )

        inputs.properties(replaceProperties)

        filesMatching("META-INF/mods.toml") {
            expand(replaceProperties)
        }
    }

    jar {
        finalizedBy("reobfJar")
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
}

spotless {
    kotlinGradle {
        target("*.kts")
        diktat()
    }

    java {
        target("src/**/java/**/*.java")
        palantirJavaFormat()
        endWithNewline()
        indentWithSpaces(4)
        removeUnusedImports()
        toggleOffOn()
        trimTrailingWhitespace()
        importOrderFile(rootProject.file("codeformat/$modId.importorder"))

        // courtesy of diffplug/spotless#240
        // https://github.com/diffplug/spotless/issues/240#issuecomment-385206606
        custom("noWildcardImports") {
            if (it.contains("*;\n")) {
                throw Error("No wildcard imports allowed")
            }

            it
        }

        bumpThisNumberIfACustomStepChanges(1)
    }

    json {
        target("src/*/resources/**/*.json")
        targetExclude("src/generated/resources/**")
        prettier().config(mapOf("parser" to "json"))
    }
}
