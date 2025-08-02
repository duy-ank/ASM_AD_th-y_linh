plugins {
<<<<<<< HEAD
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.giaodien"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.giaodien"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
=======
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    alias(libs.plugins.ksp) // ✅ Dùng trực tiếp ID plugin
}

android {
    namespace = "com.example.expensemanagement"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.expensemanagement"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
>>>>>>> 61caeb2 (Initial commit)
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
<<<<<<< HEAD
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
=======

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
>>>>>>> 61caeb2 (Initial commit)
    }
}

dependencies {
<<<<<<< HEAD

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation ("androidx.appcompat:appcompat:1.6.1")
    implementation ("com.google.android.material:material:1.9.0")
    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation ("androidx.viewpager2:viewpager2:1.0.0")
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")

}
=======
    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1") // ✅ Bây giờ sẽ không còn lỗi

    // ViewModel + LiveData
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")

    // Material Design
    implementation("com.google.android.material:material:1.11.0")

    // DateTime Picker
    implementation("com.wdullaer:materialdatetimepicker:4.2.3")
}
>>>>>>> 61caeb2 (Initial commit)
