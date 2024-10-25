import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.uniandes.project.abcall"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.uniandes.project.abcall"
        minSdk = 29
        targetSdk = 34
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
    }
    packaging {
        resources {
            excludes.addAll(
                listOf(
                    "META-INF/LICENSE.md",
                    "META-INF/NOTICE.md",
                    "META-INF/DEPENDENCIES",
                    "META-INF/NOTICE",
                    "META-INF/LICENSE",
                    "META-INF/LICENSE-notice.md"
                )
            )
        }
    }
    packaging {
        jniLibs {
            useLegacyPackaging = true
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.annotation)
    implementation(libs.squareup.retrofit)
    implementation(libs.converter.gson)
    implementation("androidx.security:security-crypto:1.1.0-alpha03")
    implementation ("com.airbnb.android:lottie:6.0.0")
    implementation (libs.github.glide)
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.8.0")
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    androidTestImplementation("androidx.arch.core:core-testing:2.1.0")
    androidTestImplementation("io.mockk:mockk-android:1.13.8")
    androidTestImplementation ("androidx.test.ext:junit:1.1.5")

    testImplementation(libs.junit)
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation ("io.kotest:kotest-runner-junit5:5.5.4")
    testImplementation ("junit:junit:4.13.2")
    testImplementation ("io.kotest:kotest-runner-junit5:5.5.4") // Para Kotest con JUnit5
    testImplementation ("io.kotest:kotest-assertions-core:5.5.4") // Aserciones de Kotest
    testImplementation ("org.mockito:mockito-core:4.5.1") // Si estás usando Mockito

}

tasks.withType<Test> {
    useJUnitPlatform()
}