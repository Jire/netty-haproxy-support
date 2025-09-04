import org.jreleaser.model.Active
import org.jreleaser.model.Signing

plugins {
    alias(libs.plugins.kotlin.jvm)
    `maven-publish`
    signing
    id("org.jreleaser")
}

group = "org.jire"
version = "1.3.1"
description = "Support for the HAProxy protocol, to resolve \"real\" IP addresses behind a proxy"

val isSnapshot = version.toString().endsWith("SNAPSHOT")

repositories {
    mavenCentral()
}

dependencies {
    api(platform(libs.netty.bom))

    api(libs.netty.handler)
    api(libs.netty.codec.haproxy)

    implementation(libs.kotlin.inline.logger)
}

kotlin {
    jvmToolchain(11)
    explicitApi()
    compilerOptions {
        freeCompilerArgs = listOf("-Xjvm-default=all")
    }
}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    repositories {
        maven {
            name = if (isSnapshot) "staging" else "release"
            setUrl(layout.buildDirectory.dir(if (isSnapshot) "staging-deploy" else "release-deploy"))
        }
    }
    publications {
        create<MavenPublication>("netty-haproxy-support") {
            from(components["java"])

            pom {
                name = rootProject.name
                description = rootProject.description
                url = "https://github.com/Jire/netty-haproxy-support"
                packaging = "jar"
                licenses {
                    license {
                        name = "MIT License"
                        url = "https://github.com/Jire/netty-haproxy-support/blob/main/LICENSE.txt"
                    }
                }
                developers {
                    developer {
                        id = "Jire"
                        name = "Thomas Nappo"
                        email = "thomasgnappo@gmail.com"
                    }
                }
                scm {
                    connection = "scm:git:git://github.com/Jire/netty-haproxy-support.git"
                    developerConnection = "scm:git:ssh://git@github.com/Jire/netty-haproxy-support.git"
                    url = "https://github.com/Jire/netty-haproxy-support"
                }
            }
        }
    }
}

signing {
    useGpgCmd()
    sign(publishing.publications)
}

tasks.javadoc {
    if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
}

jreleaser {
    project {
        author("Jire")
        license = "MIT"
        links {
            homepage = "https://github.com/Jire/netty-haproxy-support"
        }
        inceptionYear = "2025"
        description = "Support for the HAProxy protocol, to resolve \"real\" IP addresses behind a proxy"
    }
    signing {
        active = Active.ALWAYS
        mode = Signing.Mode.FILE
        armored = true
    }
    deploy {
        maven {
            mavenCentral {
                register("release-deploy") {
                    enabled = !isSnapshot
                    active = Active.RELEASE
                    url = "https://central.sonatype.com/api/v1/publisher"
                    applyMavenCentralRules = true
                    stagingRepository("build/release-deploy")
                }
            }
            nexus2 {
                register("snapshot-deploy") {
                    enabled = isSnapshot
                    active = Active.SNAPSHOT
                    snapshotUrl = "https://central.sonatype.com/repository/maven-snapshots/"
                    snapshotSupported = true
                    applyMavenCentralRules = true
                    stagingRepository("build/staging-deploy")
                }
            }
        }
    }
    release {
        github {
            enabled = false
        }
    }
}

tasks.withType<GenerateModuleMetadata>().configureEach {
    enabled = false
}
