plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("maven-publish")
}

android {

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }

    namespace = "com.sarmad.admobify.adsdk"
    compileSdk = 34

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }


    buildFeatures {
        viewBinding = true
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
//    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.google.ads)
    implementation(libs.lifecycle)
    implementation(libs.androidx.lifecycle.process)

    implementation (libs.billing.ktx)

    implementation(libs.analytics)
    implementation(libs.remote.config)

}


afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                groupId = "com.github.SarmadPervaizz"
                artifactId = "admobify"
                version = "1.4-beta1"

                // Include sources and javadoc jars
                artifact(tasks.named("sourcesJar").get())
            }
        }
    }
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    // Collect source directories as files
    val javaSources = android.sourceSets["main"].java.srcDirs
    val kotlinSources = android.sourceSets["main"].kotlin.srcDirs()

    // Include both Java and Kotlin sources
    from(javaSources)
    from(kotlinSources)
}

tasks.named("sourcesJar").configure {
    dependsOn(tasks.named("compileReleaseKotlin"))
}
