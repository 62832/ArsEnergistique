pluginManagement {
    repositories {
        maven { url = uri("https://maven.neoforged.net/") }
        maven { url = uri("https://maven.parchmentmc.org") }
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            plugin("neogradle", "net.neoforged.gradle").version("6.0.21")
            plugin("mixin", "org.spongepowered.mixin").version("0.7.+")
            plugin("parchment", "org.parchmentmc.librarian.forgegradle").version("1.+")
            plugin("spotless", "com.diffplug.spotless").version("6.22.0")

            val minecraftVersion = "1.20.1"

            library("forge", "net.neoforged", "forge").version("$minecraftVersion-47.1.54")
            library("mixin", "org.spongepowered", "mixin").version("0.8.5")

            version("ae2", "15.2.1")
            library("ae2", "appeng", "appliedenergistics2-forge").versionRef("ae2")
            library("aecapfix", "curse.maven", "aecapfix-914685").version("5017517")

            version("ars", "4.6.0.99")
            library("ars", "com.hollingsworth.ars_nouveau", "ars_nouveau-$minecraftVersion").versionRef("ars")

            library("geckolib", "software.bernie.geckolib", "geckolib-forge-$minecraftVersion").version("4.2.1")
            library("mixin-extras", "io.github.llamalad7", "mixinextras-forge").version("0.3.6")
            library("curios", "top.theillusivec4.curios", "curios-forge").version("5.2.0-beta.3+$minecraftVersion")
            library("patchouli", "vazkii.patchouli", "Patchouli").version("$minecraftVersion-81-FORGE")

            library("jei", "mezz.jei", "jei-$minecraftVersion-forge").version("15.2.0.25")
            library("jade", "curse.maven", "jade-324717").version("4681833")
        }
    }
}
