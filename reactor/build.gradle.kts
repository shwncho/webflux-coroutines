plugins {
    kotlin("jvm") version "2.0.0"
}

group = "com.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {

    implementation("ch.qos.logback:logback-classic:1.4.8")
    implementation("io.github.microutils:kotlin-logging:3.0.5")
    implementation("io.projectreactor:reactor-core:3.5.7")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}