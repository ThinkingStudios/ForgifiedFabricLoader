plugins {
    java
    `maven-publish`
    //id("org.cadixdev.licenser") version "0.6.1"
    id("net.neoforged.gradleutils") version "5.1.0"
    id("com.gradleup.shadow") version "9.+"
    // Used for mapping tools only, provides TSRG writer on top of mappings-io
    id("org.relativitymc.neo-loom") version "1.15-SNAPSHOT"
}

val versionMc: String by rootProject
val versionForge: String by rootProject
val versionLoaderUpstream: String by rootProject

gradleutils.version {
    branches {
        suffixBranch()
        suffixExemptedBranch(versionMc)
        //suffixExemptedBranch("1.21.x")
    }
}

group = "org.sinytra"
version = "0.1.0+$versionLoaderUpstream+$versionMc"
println("Version: $version")

//license {
//    header("HEADER")
//    exclude("net/fabricmc/loader/impl/lib/gson/**")
//    exclude("**/*.properties")
//}

val shade: Configuration by configurations.creating

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
    withSourcesJar()
}

sourceSets {
    main {
        java {
            srcDir("src/main/legacyJava")
        }
    }
}

configurations {
    implementation {
        extendsFrom(shade)
    }
}

repositories {
    mavenCentral()
    maven {
        name = "FabricMC"
        url = uri("https://maven.fabricmc.net")
    }
    maven {
        name = "Mojank"
        url = uri("https://libraries.minecraft.net/")
    }
    maven {
        name = "NeoForged"
        url = uri("https://maven.neoforged.net/releases")
    }
}

dependencies {
    minecraft(group = "com.mojang", name = "minecraft", version = versionMc)
    neoForge(group = "net.neoforged", name = "neoforge", version = versionForge)

    shade(api(include("net.neoforged:srgutils:1.0.11")!!)!!)
    shade(include("org.ow2.sat4j:org.ow2.sat4j.core:2.3.6")!!)
    shade(include("org.ow2.sat4j:org.ow2.sat4j.pb:2.3.6")!!)

    testCompileOnly("org.jetbrains:annotations:23.0.0")
    // Unit testing for mod metadata
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}


tasks {
    setOf(jar, shadowJar).forEach { provider ->
        provider.configure {
            manifest.attributes(
                "FMLModType" to "LIBRARY",
                "Automatic-Module-Name" to "net.fabricmc.loader",
                "Implementation-Version" to archiveVersion.get()
            )
        }
    }

    shadowJar {
        configurations = listOf(shade)
        relocate("net.minecraftforge.srgutils", "reloc.net.minecraftforge.srgutils")
        relocate("org.sat4j", "reloc.org.sat4j")
        archiveClassifier.set("full")
    }

    assemble {
        dependsOn(shadowJar)
    }

    test {
        useJUnitPlatform()
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }

    repositories {
        maven {
            name = "KTTMaven"
            url = uri("https://maven.kessokuteatime.work/snapshots/")
            credentials {
                username = System.getenv("KTT_MAVEN_USERNAME")
                password = System.getenv("KTT_MAVEN_TOKEN")
            }
        }
    }
}
