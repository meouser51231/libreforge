import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.1.0")
    }
}

plugins {
    java
    id("java-library")
    id("com.gradleup.shadow") version "8.3.5"
    id("maven-publish")
}

allprojects {
    apply(plugin = "kotlin")
    apply(plugin = "java")
    apply(plugin = "maven-publish")
    apply(plugin = "com.gradleup.shadow")

    repositories {
        mavenLocal() // TODO: REMOVE
        mavenCentral()
        maven("https://repo.auxilor.io/repository/maven-public/")
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://repo.codemc.io/repository/maven-public/")
        maven("https://maven.enginehub.org/repo/")
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
        maven("https://maven.citizensnpcs.co/repo")
    }

    dependencies {
        compileOnly("org.jetbrains:annotations:23.0.0")
        compileOnly(kotlin("stdlib", version = "2.1.0"))
        compileOnly(fileTree("lib") { include("*.jar") })
    }

    // แก้ไขส่วน tasks
    tasks {
        // ปรับปรุงการ process resources
        processResources {
            filesMatching(listOf("**/*.yml")) {  // เปลี่ยนจาก **plugin.yml เป็น **/*.yml
                expand(
                    "projectVersion" to rootProject.version,
                    "version" to project.version
                )
            }
            // เพิ่มการ copy resources ทั้งหมด
            from(sourceSets.main.get().resources) {
                include("**/*")
            }
        }

        // กำหนดค่า shadowJar ให้ชัดเจน
        shadowJar {
            from(sourceSets.main.get().output)
            archiveClassifier.set("")  // ลบ classifier เพื่อให้ไฟล์ไม่มี -all ต่อท้าย
            
            // รวม resources
            from(project.sourceSets.main.get().resources) {
                include("**/*.yml")
            }
        }

        withType<JavaCompile> {
            options.encoding = "UTF-8"
            options.release.set(17)
        }

        withType<KotlinCompile> {
            compilerOptions {
                jvmTarget.set(JvmTarget.JVM_17)
            }
        }

        compileJava {
            dependsOn(clean)
        }

        build {
            dependsOn(shadowJar)
            dependsOn(publishToMavenLocal)
        }
    }

    // กำหนด sourceSets ให้ชัดเจน
    sourceSets {
        main {
            resources {
                srcDirs("src/main/resources")
            }
        }
    }

    java {
        withSourcesJar()
        toolchain {
            languageVersion = JavaLanguageVersion.of(21)
        }
    }

    publishing {
        repositories {
            maven {
                name = "auxilor"
                url = uri("https://repo.auxilor.io/repository/maven-releases/")
                credentials {
                    username = System.getenv("MAVEN_USERNAME")
                    password = System.getenv("MAVEN_PASSWORD")
                }
            }
        }
    }
}

group = "com.willfp"
version = findProperty("version")!!
