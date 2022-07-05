plugins {
    id("java-library")
    id("kotlin")
}

dependencies {
    compileOnly(kotlin("stdlib-jdk8", Versions.kotlin))
    compileOnly(kotlin("reflect", Versions.kotlin))
    compileOnly("com.android.tools.lint:lint-api:30.2.1")
    compileOnly("com.android.tools.lint:lint-checks:30.2.1")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.jar {
    @Suppress("UnstableApiUsage")
    manifest {
        attributes("Lint-Registry-v2" to "com.eidu.rules.Registry")
    }
}
