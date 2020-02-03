import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    maven

    kotlin("jvm") version "1.3.41"

    jacoco
}

group = "com.kbapps.ephemeris"
version = "1.0.2"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    // https://github.com/ThreeTen/threetenbp/
    implementation("org.threeten", "threetenbp", "1.4.1", classifier = "no-tzdb")

    // https://mvnrepository.com/artifact/org.hamcrest/hamcrest-all
    testImplementation("org.hamcrest:hamcrest-all:1.3")
    testImplementation("org.junit.jupiter:junit-jupiter:5.5.1")
}

val test by tasks.getting(Test::class) {
    useJUnitPlatform { }
}

tasks.jacocoTestReport {
    reports {
        xml.isEnabled = false
        csv.isEnabled = false
        html.isEnabled = true
        html.destination = file("$buildDir/reports/coverage")
    }
}
tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = "0.95".toBigDecimal()
            }
        }
    }
}


configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}