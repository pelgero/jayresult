import com.vanniktech.maven.publish.SonatypeHost

plugins {
    java
    signing
    id("com.vanniktech.maven.publish") version "0.28.0"
}

group = "io.badgod"
version = "0.1.0"


repositories {
    mavenCentral()
}

dependencies {
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
    testImplementation("org.hamcrest:hamcrest-library:2.2")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
    testLogging.events("FAILED")
    testLogging.showExceptions = true
}

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()


    coordinates(
        project.group.toString(),
        rootProject.name,
        project.version.toString()
    )

    pom {
        name.set("JayResult")

        description.set("Implementation of the Result monad in Java (based on Rust language standard implementation)")
        inceptionYear.set("2024")
        url.set("https://github.com/pelgero/jayresult")
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                id.set("pelgero")
                name.set("Robert Pelger")
                url.set("https://github.com/pelgero/")
            }
        }
        scm {
            url.set("https://github.com/pelgero/jayresult.git")
            connection.set("scm:git:git://github.com/pelgero/jayresult.git")
            developerConnection.set("scm:git:ssh://git@github.com/pelgero/jayresult.git")
        }
    }
}


