plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.sarmad.adsdk.demo"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.sarmad.adsdk.demo"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }


    flavorDimensions.add("Default")

    productFlavors {

        create("appDev") {

            resValue("string", "open_app_ad", "ca-app-pub-3940256099942544/9257395921")
            resValue("string", "banner_ad", "ca-app-pub-3940256099942544/9214589741")
            resValue("string", "rectangle_banner_ad", "ca-app-pub-3940256099942544/6300978111")
            resValue("string", "collapsible_banner_ad", "ca-app-pub-3940256099942544/2014213617")
            resValue("string", "interstitial_ad", "ca-app-pub-3940256099942544/1033173712")
            resValue("string", "native_ad", "ca-app-pub-3940256099942544/2247696110")
            resValue("string", "rewarded_ad", "ca-app-pub-3940256099942544/5224354917")

        }

        create("appProd") {
        }

    }

    buildFeatures {
        viewBinding = true
    }
    buildTypes {

        getByName("debug") {
            isMinifyEnabled = false
            isShrinkResources = false
        }

        getByName("release") {

            isMinifyEnabled = true
            isShrinkResources = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

        }

    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.google.ads)
    implementation(project(":admobify-adsdk"))

//    implementation("com.github.SarmadPervaizz:Admobify:1.6")

    implementation (libs.sdp.android)
    implementation (libs.ssp.android)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    implementation (libs.billing.ktx)

}