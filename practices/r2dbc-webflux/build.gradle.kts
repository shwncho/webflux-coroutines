plugins {
    java
    id("org.springframework.boot") version "3.4.4"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("org.springframework.boot:spring-boot-starter-mustache")
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    implementation("org.springframework.security:spring-security-core")

    // reactor tool
    implementation("io.projectreactor:reactor-tools")

    // r2dbc
    implementation("io.asyncer:r2dbc-mysql:0.9.2")

    // rxjava
    implementation("io.reactivex.rxjava3:rxjava")

    // lombok
    annotationProcessor("org.projectlombok:lombok")
    compileOnly("org.projectlombok:lombok")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
