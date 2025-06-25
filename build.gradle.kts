import org.jreleaser.model.Active

plugins {
    `java-library`
    `maven-publish`
    id("org.jreleaser") version "1.18.0"
}

group = "io.github.archipelagomw"
version = "0.1.20-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(libs.junit.jupiter)

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // Gson is indirectly exposed via slotdata helpers
    api(libs.gson)
    implementation(libs.java.websocket)
    implementation(libs.httpclient)
    implementation(libs.httpcore)
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    withSourcesJar()
    withJavadocJar()
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.release = 8
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}

tasks.named<Javadoc>("javadoc") {
    options {
        (this as CoreJavadocOptions).addBooleanOption("Xdoclint:none", true)
    }
}

publishing {
    publications {
        create<MavenPublication>("javaClient") {
            from(components["java"])
            repositories {
                // For the time being
                mavenLocal()
                maven {
                    url = uri(layout.buildDirectory.dir("staging-deploy"))
                }
            }
            pom {
                name = "Archipelago Java Library"
                description = "Java library to connect to an Archipelago Server"
                url = "https://github.com/ArchipelagoMW/Java-Client"
                scm {
                    connection = "scm:git://github.com/ArchipelagoMW/Java-Client"
                    developerConnection = "scm:git:https://github.com/ArchipelagoMW/Java-Client.git"
                    url = "https://github.com/ArchipelagoMW/Java-Client"
                }
                inceptionYear = "2021"
                licenses {
                    license {
                        name = "MIT License"
                        url = "https://github.com/ArchipelagoMW/Java-Client/blob/main/LICENSE"
                        distribution = "repo"
                    }
                }
                developers {
                    developer {
                        name = "PlatanoBailando"
                        email = "cjmang@gmail.com"
                    }
                    developer {
                        name = "digiholic"
                    }
                }
                contributors {
                    contributor {
                        name = "Kono Tyran"
                        roles.add("Author")
                    }
                    contributor {
                        name = "mattman107"
                    }
                    contributor {
                        name = "charlesfire"
                    }
                }
            }
        }
    }
}

jreleaser {
    signing {
        active = Active.ALWAYS
        armored = true
    }
    release {
        github {
            enabled = true
            repoOwner = "ArchipelagoMW"
            overwrite = false
            skipRelease = true
        }
    }
    deploy {
        maven {
            mavenCentral {
                register("release-deploy") {
                    // Releases intended to be turned on via environment variable
                    applyMavenCentralRules = true
                    url = "https://central.sonatype.com/api/v1/publisher"
                    stagingRepository("build/staging-deploy")
                }
            }
            nexus2 {
                register("snapshot-deploy") {
                    active = Active.SNAPSHOT
                    applyMavenCentralRules = true
                    snapshotSupported = true
                    closeRepository = true
                    releaseRepository = true
                    url = "https://central.sonatype.com/api/v1/publisher"
                    snapshotUrl = "https://central.sonatype.com/repository/maven-snapshots/"
                    stagingRepository("build/staging-deploy")
                }
            }
        }
    }
}
