plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)

    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.autoaccounting.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.autoaccounting.app"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }
}
dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:ui"))
    implementation(project(":domain"))
    implementation(project(":data"))
    implementation(project(":feature:home"))
    implementation(project(":feature:addtransaction"))
    implementation(project(":feature:category"))
    implementation(project(":feature:statistics"))
    implementation(project(":feature:settings"))
    implementation(project(":feature:sms"))
    implementation(project(":feature:bills"))
    implementation(project(":feature:profile"))

    implementation(libs.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.material3)
    implementation(libs.compose.ui)
    implementation(libs.compose.material.icons)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.navigation.compose)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.coroutines.android)
    implementation(libs.work.runtime)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    debugImplementation(libs.compose.ui.tooling)
}
