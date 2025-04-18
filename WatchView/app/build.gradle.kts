plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.watchview"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.watchview"
        minSdk = 26
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation (libs.expandablelayout)

    implementation(libs.androidx.recyclerview)
    implementation (libs.androidx.cardview)

    implementation(libs.glide)

    // WorkManager (para ejecutar la actualización cada 24h)
    implementation(libs.androidx.work.runtime.ktx)

    // Retrofit (para hacer las peticiones a la API)
    implementation(libs.retrofit)
    implementation(libs.retrofitGson)

    // OkHttp (para logs de las peticiones)
    implementation(libs.okHttpLoggingInterceptor)

    annotationProcessor(libs.compiler)

    // Gson
    implementation(libs.gson)

    // Mockito
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.inline)

    // Para pruebas unitarias con JUnit 5
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.8.1")
}