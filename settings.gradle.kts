pluginManagement {
    plugins {
        id("net.neoforged.moddev") version "1.0.14"
        id("net.neoforged.moddev.repositories") version "1.0.14"
        id("com.diffplug.spotless") version "6.25.0"
    }
}

plugins {
    id("net.neoforged.moddev.repositories")
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

run {
    @Suppress("UnstableApiUsage")
    dependencyResolutionManagement {
        repositoriesMode = RepositoriesMode.PREFER_SETTINGS
        rulesMode = RulesMode.PREFER_SETTINGS

        repositories {
            maven {
                name = "ModMaven (K4U-NL)"
                url = uri("https://modmaven.dev/")
                content {
                    includeGroup("mezz.jei")
                }
            }

            maven {
                name = "BlameJared"
                url = uri("https://maven.blamejared.com")
                content {
                    includeGroup("com.hollingsworth.ars_nouveau")
                    includeGroup("com.hollingsworth.nuggets")
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

            maven {
                name = "Illusive Soulworks"
                url = uri("https://maven.theillusivec4.top/")
                content {
                    includeGroup("com.illusivesoulworks.caelus")
                }
            }

            maven {
                name = "Minecraft Forge"
                url = uri("https://maven.minecraftforge.net/")
                content {
                    includeGroup("com.github.glitchfiend")
                }
            }

            maven {
                name = "OctoStudios"
                url = uri("https://maven.octo-studios.com/releases")
                content {
                    includeGroup("top.theillusivec4.curios")
                }
            }
        }

        versionCatalogs {
            create("libs") {
                val mc = "1.21.1"
                version("minecraft", mc)

                val nf = mc.substringAfter('.')
                version("neoforge", "${nf + (if (!nf.contains('.')) ".0" else "")}.113")
                version("parchment", "2024.11.17")

                version("ae2", "19.2.2-beta")
                library("ae2", "org.appliedenergistics", "appliedenergistics2").versionRef("ae2")

                version("ars", "5.4.2.938")
                library("ars", "com.hollingsworth.ars_nouveau", "ars_nouveau-$mc").versionRef("ars")
            }
        }
    }
}
