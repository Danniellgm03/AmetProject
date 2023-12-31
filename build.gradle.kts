plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains:annotations:24.0.0")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    testCompileOnly("org.projectlombok:lombok:1.18.30")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.30")
    implementation("org.xerial:sqlite-jdbc:3.43.0.0")
    implementation("org.mybatis:mybatis:3.5.13")

    implementation("com.google.code.gson:gson:2.10.1")
}

tasks.jar{
    manifest {
        attributes["Main-Class"] = "org.amet.Main"
    }
}

tasks.test {
    useJUnitPlatform()
}