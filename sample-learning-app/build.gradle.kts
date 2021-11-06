import utils.getAppVersion
import utils.toVersionCode

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("org.jetbrains.kotlin.kapt")
    id("dagger.hilt.android.plugin")
    id("org.jlleitschuh.gradle.ktlint") version Versions.ktlintGradle
}

val version = getAppVersion()

android {
    compileSdk = Apps.compileSdk
    buildToolsVersion = Apps.buildToolsVersion

    defaultConfig {
        applicationId = Apps.sampleApplicationId
        minSdk = Apps.minSdk
        targetSdk = Apps.targetSdk
        versionCode = version.toVersionCode()
        versionName = version

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create(BuildType.RELEASE) {
            storeFile = file("dummy-keystore.jks")
            storePassword = "release"
            keyAlias = "release"
            keyPassword = "release"
        }
    }

    buildTypes {
        getByName(BuildType.RELEASE) {
            signingConfig = signingConfigs.getByName(BuildType.RELEASE)

            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), file("proguard-rules.pro"))
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf(
            "-Xopt-in=kotlin.RequiresOptIn"
        )
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Versions.compose
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.3.1")
    implementation("com.google.android.material:material:1.4.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.4.0")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")

    // Compose
    implementation("androidx.compose.ui:ui:${Versions.compose}")
    implementation("androidx.compose.material:material:${Versions.compose}")
    implementation("androidx.compose.ui:ui-tooling-preview:${Versions.compose}")
    implementation("androidx.compose.runtime:runtime:${Versions.compose}")
    implementation("androidx.compose.runtime:runtime-livedata:${Versions.compose}")
    implementation("androidx.navigation:navigation-compose:${Versions.navigation}")
    implementation("androidx.compose.material:material-icons-extended:${Versions.compose}")
    implementation("androidx.activity:activity-compose:1.4.0")

    // Hilt/Dagger DI
    implementation("com.google.dagger:hilt-android:${Versions.hilt}")
    implementation("androidx.lifecycle:lifecycle-process:2.4.0")
    kapt("com.google.dagger:hilt-android-compiler:${Versions.hilt}")

    // EIDU dependencies
    implementation("com.eidu:integration-library:1.3.0")

    // Test
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:${Versions.compose}")
    debugImplementation("androidx.compose.ui:ui-tooling:${Versions.compose}")
}
