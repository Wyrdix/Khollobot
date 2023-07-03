plugins {
    id("java")
}

group = "com.wyrdix"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    dependencies {
        //Change "implementation" to "compile" in old Gradle versions
        implementation("net.dv8tion:JDA:5.0.0-beta.12") {
            exclude(module = "opus-java")
        }
    }
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}