
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id ("com.github.ben-manes.versions") version "0.46.0"
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
    implementation("com.airbnb.android:lottie:6.0.0")
    implementation(libs.github.glide)
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.8.0")
    implementation("io.jsonwebtoken:jjwt:0.12.6")
    implementation("com.github.clans:fab:1.6.4")
    implementation(libs.androidx.junit.ktx)
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")



    androidTestImplementation("org.hamcrest:hamcrest-core:1.3") // Cambiar a la versión 1.3
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
    androidTestImplementation("io.mockk:mockk-android:1.13.13")

    androidTestImplementation("com.github.tomakehurst:wiremock:3.0.1") {
        exclude(group = "org.apache.httpcomponents", module= "httpclient")
        exclude(group = "asm", module= "asm")
        exclude(group = "org.json", module= "json")
        exclude(group = "org.hamcrest", module = "hamcrest-core")
        exclude(group = "org.hamcrest", module = "hamcrest")
    }
    androidTestImplementation ("androidx.test.espresso:espresso-intents:3.6.1")
    androidTestImplementation("androidx.test:core-ktx:1.6.1") {
        exclude(group = "org.hamcrest", module = "hamcrest-core")
    }
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.0") {
        // Excluir las versiones de hamcrest-core aquí
        exclude(group = "org.hamcrest", module = "hamcrest-core")
        exclude(group = "org.hamcrest", module = "hamcrest")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

configurations.all {
    exclude ("org.hamcrest', module: 'hamcrest-core")
}