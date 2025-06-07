plugins {
    `java-library`
    `maven-publish`
}

group = "io.github.ArchipelagoMW"
version = "0.1.20-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(libs.junit.jupiter)

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation(libs.java.websocket)
    implementation(libs.gson)
    implementation(libs.httpclient)
    implementation(libs.httpcore)
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

val sourcesJar: TaskProvider<Jar> by tasks.registering(Jar::class) {
    ->
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

val javadocJar: TaskProvider<Jar> by tasks.registering(Jar::class) {
    ->
    dependsOn.add(tasks.javadoc)
    archiveClassifier.set("javadoc")
    from(tasks.javadoc)
}

tasks {
    artifacts {
        archives(sourcesJar)
        archives(javadocJar)
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
            repositories {
                // For the time being
                mavenLocal()
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
            artifacts {
                artifact(tasks.jar)
                artifact(sourcesJar)
                artifact(javadocJar)
            }
        }
    }
}