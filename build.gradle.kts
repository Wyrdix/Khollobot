plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "com.wyrdix"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    //Change "implementation" to "compile" in old Gradle versions
    implementation("net.dv8tion:JDA:5.0.0-beta.12") {
        exclude(module = "opus-java")
    }
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.sun.mail:javax.mail:1.6.2")

}

tasks.compileJava {
    options.encoding = "UTF-8"
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "com.wyrdix.khollobot.KholloBot"
    }
}