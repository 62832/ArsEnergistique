plugins {
    eclipse
    idea
    alias(libs.plugins.neogradle)
    alias(libs.plugins.mixin)
    alias(libs.plugins.parchment)
    alias(libs.plugins.spotless)
}

val modId = "arseng"

version = System.getenv("ARSENG_VERSION") ?: "0.0.0"
group = "gripe.90"
base.archivesName.set(modId)

java.toolchain.languageVersion.set(JavaLanguageVersion.of(17))

repositories {
    mavenLocal()
    mavenCentral()

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
        content {
            includeGroup("software.bernie.geckolib")
        }
    }
}

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
    mappings("parchment", "2023.09.03-1.20.1")
    copyIdeResources.set(true)

    runs {
        configureEach {
            workingDirectory(project.file("run"))
            property("forge.logging.markers", "REGISTRIES")
            property("forge.logging.console.level", "debug")

            mods {
                create(modId) {
                    source(sourceSets.main.get())
                }
            }
        }

        create("client")

        create("server") {
            workingDirectory(file("run/server"))
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
    processResources {
        exclude("**/.cache")

        val props = mapOf("version" to version)
        inputs.properties(props)

        filesMatching("META-INF/mods.toml") {
            expand(props)
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
        biome()
        indentWithSpaces(2)
        endWithNewline()
    }
}
