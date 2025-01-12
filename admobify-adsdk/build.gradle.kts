plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("maven-publish")
}

android {

    namespace = "com.sarmad.admobify.adsdk"
    compileSdk = 34

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }


    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    buildTypes {

        debug {
        }

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
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    implementation(libs.google.ads)
    implementation(libs.lifecycle)
    implementation(libs.androidx.lifecycle.process)

    implementation (libs.billing.ktx)

    implementation(libs.analytics)
    implementation(libs.remote.config)

}

//TODO hide views in validate case banner native

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                groupId = "com.github.SarmadPervaizz"
                artifactId = "admobify"
                version = "1.7"
            }
        }
    }
}

