pluginManagement {
    repositories {
        gradlePluginPortal()
        maven { url = uri("https://maven.minecraftforge.net/") }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            plugin("forge", "net.minecraftforge.gradle").version("6.0.+")
            plugin("mixin", "org.spongepowered.mixin").version("0.7.+")
            plugin("spotless", "com.diffplug.spotless").version("6.20.0")

            val minecraftVersion = "1.20.1"
            version("minecraft", minecraftVersion)

            val forgeVersion = "47.1.3"
            version("loader", forgeVersion.substringBefore('.'))
            library("forge", "net.minecraftforge", "forge").version("$minecraftVersion-$forgeVersion")
            library("mixin", "org.spongepowered", "mixin").version("0.8.5")

            version("ae2", "15.0.9-beta")
            library("ae2", "appeng", "appliedenergistics2-forge").versionRef("ae2")

            version("ars", "4.2.5.61")
            library("ars", "com.hollingsworth.ars_nouveau", "ars_nouveau-$minecraftVersion").versionRef("ars")

            library("geckolib", "software.bernie.geckolib", "geckolib-forge-$minecraftVersion").version("4.2.1")
            library("mixin-extras", "com.github.llamalad7.mixinextras", "mixinextras-forge").version("0.2.0-beta.9")
            library("curios", "top.theillusivec4.curios", "curios-forge").version("5.2.0-beta.3+$minecraftVersion")
            library("patchouli", "vazkii.patchouli", "Patchouli").version("$minecraftVersion-81-FORGE")

            library("jei", "mezz.jei", "jei-$minecraftVersion-forge").version("15.2.0.25")
            library("jade", "curse.maven", "jade-324717").version("4681833")
        }
    }
}