plugins {
    id("com.github.ben-manes.versions") version "0.39.0"
}

// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:${Versions.gradle}")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.30")
        classpath("com.google.dagger:hilt-android-gradle-plugin:${Versions.hilt}")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

tasks.register("installGitHooks", Exec::class) {
    commandLine("git", "config", "--local", "core.hooksPath", "git-hooks")
}

tasks.getByPath(":sample-learning-app:preBuild").dependsOn(":installGitHooks")
