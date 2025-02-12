import org.jetbrains.kotlin.ir.backend.js.compile

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.8.21"
    id("org.jetbrains.intellij") version "1.13.3"
}

group = "de.cubbossa"
version = "1.1.0"

repositories {
    mavenCentral()
    maven("https://nexus.leonardbausenwein.de/repository/maven-public/")
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2023.3.1")
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
        sinceBuild.set("222")
        untilBuild.set("233.*")
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
