import org.jetbrains.kotlin.ir.backend.js.compile

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.8.21"
    id("org.jetbrains.intellij") version "1.17.2"
}

group = "de.cubbossa"
version = "1.1.0"

repositories {
    mavenCentral()
    maven("https://nexus.leonardbausenwein.de/repository/maven-public/")
}

dependencies {
    implementation("net.kyori:adventure-api:4.16.0")
    implementation("net.kyori:adventure-text-minimessage:4.16.0")
    implementation("net.kyori:adventure-text-serializer-plain:4.16.0")
    implementation("net.kyori:adventure-text-serializer-legacy:4.16.0")
    implementation("net.kyori:adventure-text-serializer-gson:4.16.0")
    implementation("de.cubbossa:TinyTranslations-common:4.4.3")
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2023.3.4")
    type.set("IC") // Target IDE Platform

    plugins.set(listOf("com.intellij.java", "org.intellij.intelliLang", "org.jetbrains.plugins.yaml", "com.intellij.properties"))
}

sourceSets["main"].java.srcDirs("src/main/gen", "src/main/kotlin")

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    patchPluginXml {
        sinceBuild.set("233")
        untilBuild.set("241.*")
    }

    signPlugin {
        certificateChainFile.set(file("C:/Users/leona/chain.crt"))
        privateKeyFile.set(file("C:/Users/leona/private_encrypted.pem"))
        password.set(providers.environmentVariable("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}
