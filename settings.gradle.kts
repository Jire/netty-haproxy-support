plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"

    kotlin("jvm") version "1.9.23" apply false

    id("com.gradle.plugin-publish") version "1.3.1" apply false
    id("org.jreleaser") version "1.19.0" apply false
}

rootProject.name = "netty-haproxy-support"
