import groovy.json.JsonOutput
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.parcelize)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

val localConfig =
    Properties().apply {
        rootProject
            .file("local.properties")
            .takeIf { it.exists() }
            ?.inputStream()
            ?.use { load(it) }
    }

fun configValue(name: String): String = providers.environmentVariable(name).getOrElse(localConfig.getProperty(name, ""))

android {
    namespace = "com.example.yelpclone"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.example.yelpclone"
        minSdk = 26
        targetSdk = 37
        versionCode = 2
        versionName = "2.0"
        testInstrumentationRunner = "com.example.yelpclone.GuideTestRunner"
        buildConfigField("String", "YELP_API_KEY", JsonOutput.toJson(configValue("YELP_API_KEY")))
        buildConfigField("boolean", "MAPS_CONFIGURED", configValue("MAPS_API_KEY").isNotBlank().toString())
        manifestPlaceholders["MAPS_API_KEY"] = configValue("MAPS_API_KEY")
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
    }
}

dependencies {
    implementation(libs.androidx.core)
    implementation(libs.androidx.splashscreen)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.material)
    implementation(libs.maps)
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.gson)
    implementation(libs.okhttp.logging)
    implementation(libs.glide)
    implementation(libs.coroutines.android)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    testImplementation(libs.junit)
    testImplementation(libs.coroutines.test)
    androidTestImplementation(libs.androidx.test.junit)
    androidTestImplementation(libs.androidx.test.espresso)
    androidTestImplementation(libs.hilt.testing)
    kspAndroidTest(libs.hilt.compiler)
}
