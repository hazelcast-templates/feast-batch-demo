plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.hazelcast"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.hazelcast:hazelcast:5.4.0")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.17.2")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "com.example.Main"
    }
}
