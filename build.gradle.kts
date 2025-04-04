import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.*

plugins {
    kotlin("jvm") version "1.9.22"

    `maven-publish`

    jacoco
}

group = "com.kbapps.ephemeris"
version = "2.2.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

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

tasks {
    val sourcesJar by creating(Jar::class) {
        archiveClassifier.set("sources")
        from(sourceSets.main.get().allSource)
    }

    val javadocJar by creating(Jar::class) {
        dependsOn.add(javadoc)
        archiveClassifier.set("javadoc")
        from(javadoc)
    }

    artifacts {
        archives(sourcesJar)
        archives(javadocJar)
        archives(jar)
    }
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

publishing {
    val props = Properties()

    file(
        System.getenv("repoConfig")
            ?: "${System.getProperty("user.home")}${File.separator}repository.properties"
    ).let {
        if (it.exists()) {
            props.load(it.inputStream())
        }
    }

    publications {
        create<MavenPublication>("maven") {
            groupId = "com.github.kb0"
            artifactId = "kotlin-ephemeris"
            version = project.version.toString()

            from(components["java"])
        }
    }

    repositories {
        if (props.getProperty("publish.store") != null) {
            maven {
                url = uri(props.getProperty("publish.store"))
                credentials(HttpHeaderCredentials::class) {
                    name = "Deploy-Token"
                    value = props.getProperty("publish.token")
                }
                authentication {
                    create<HttpHeaderAuthentication>("header")
                }
            }
        }

        if (System.getenv("GITHUB_TOKEN") != null) {
            maven {
                url = uri("https://maven.pkg.github.com/kb0/kotlin-ephemeris")
                credentials {
                    username = System.getenv("GITHUB_ACTOR")
                    password = System.getenv("GITHUB_TOKEN")
                }
            }
        }
    }
}
