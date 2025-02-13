plugins {
    eclipse
    idea
    id("net.neoforged.moddev")
    id("com.diffplug.spotless")
}

val modId = "arseng"

base.archivesName = modId
version = System.getenv("ARSENG_VERSION") ?: "0.0.0"
group = "gripe.90"

java.toolchain.languageVersion = JavaLanguageVersion.of(21)

dependencies {
    implementation(libs.ae2)
    implementation(libs.ars)
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

neoForge {
    version = libs.versions.neoforge.get()

    parchment {
        minecraftVersion = libs.versions.minecraft.get()
        mappingsVersion = libs.versions.parchment.get()
    }

    mods {
        create(modId) {
            sourceSet(sourceSets.main.get())
            sourceSet(sourceSets.getByName("data"))
        }
    }

    runs {
        configureEach {
            gameDirectory = file("run")
        }

        create("client") {
            client()
        }

        create("server") {
            server()
            gameDirectory = file("run/server")
        }

        create("data") {
            data()
            programArguments.addAll(
                "--mod", modId,
                "--all",
                "--output", file("src/generated/resources/").absolutePath,
                "--existing", file("src/main/resources/").absolutePath,
                "--existing-mod", "ae2",
                "--existing-mod", "ars_nouveau"
            )
            sourceSet = sourceSets.getByName("data")
        }
    }
}

tasks {
    processResources {
        exclude("**/.cache")

        val props = mapOf("version" to version)
        inputs.properties(props)

        filesMatching("META-INF/neoforge.mods.toml") {
            expand(props)
        }
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
