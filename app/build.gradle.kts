plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.ksp)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.androidx.room)
    id("kotlin-parcelize")
}

android {
    namespace = "djabari.dev.workquotes"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "djabari.dev.workquotes"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    testOptions {
        unitTests.isReturnDefaultValues = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    room {
        schemaDirectory("$projectDir/schemas")
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.kotlinx.coroutines.android)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.androidx.material.icons.extended)

    // ktor
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.client.content.enconding)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)

    // kotlin serialization
    implementation(libs.kotlinx.serialization.json)

    // kotlin datetime
    implementation(libs.kotlinx.datetime)

    // paging
    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.compose)

    // compose navigation
    implementation(libs.androidx.navigation.compose)

    // kotlin inject
    implementation(libs.kotlinInject.runtime)
    ksp(libs.kotlinInject.compiler)

    // room
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.paging)
    annotationProcessor(libs.androidx.room.compiler)
    androidTestImplementation(libs.androidx.room.testing)

    // work manager
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.work.testing)
    implementation(libs.androidx.work.multiprocess)

    // datastore
    implementation(libs.datastore)
    implementation(libs.datastore.preferences)
}