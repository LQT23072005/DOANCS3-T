

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services") // Firebase

    //vừa thêm đoạn 47.22
    id("kotlin-kapt")
}

android {
    namespace = "com.example.doancs3"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.doancs3"
        minSdk = 24
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.database)
    implementation(libs.firebase.storage)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.1.0")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)


    //vừa thêm đoạn 20:45
    implementation ("com.tbuonomo:dotsindicator:5.1.0")


    implementation("androidx.recyclerview:recyclerview:1.3.2")



    //vừa thêm đoạn 47.22
    implementation("com.github.bumptech.glide:glide:4.15.1")
    kapt("com.github.bumptech.glide:compiler:4.15.1")


    //vừa thêm đoạn 1.52
    implementation ("com.google.code.gson:gson:2.10.1")

    implementation("com.google.firebase:firebase-auth") //tích hợp đăng ký đăng nhập


    implementation ("com.cloudinary:cloudinary-android:2.3.1") //up lên cloudinary

}




//
//plugins {
//    id("com.android.application")
//    id("org.jetbrains.kotlin.android")
//    id("com.google.gms.google-services") // Firebase
//
//    //vừa thêm đoạn 47.22
//    id("kotlin-kapt")
//}
//
//android {
//    namespace = "com.example.doancs3"
//    compileSdk = 35
//
//    defaultConfig {
//        applicationId = "com.example.doancs3"
//        minSdk = 24
//        targetSdk = 35
//        versionCode = 1
//        versionName = "1.0"
//        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
//    }
//
//    buildTypes {
//        release {
//            isMinifyEnabled = false
//            proguardFiles(
//                getDefaultProguardFile("proguard-android-optimize.txt"),
//                "proguard-rules.pro"
//            )
//        }
//    }
//
//    compileOptions {
//        sourceCompatibility = JavaVersion.VERSION_17
//        targetCompatibility = JavaVersion.VERSION_17
//    }
//
//    kotlinOptions {
//        jvmTarget = "17"
//    }
//
//    buildFeatures {
//        viewBinding = true
//
//    }
//}
//
//dependencies {
//    implementation("com.github.bumptech.glide:glide:4.16.0") // Dùng phiên bản mới nhất
//    kapt("com.github.bumptech.glide:compiler:4.16.0") // Đảm bảo `kapt` dùng chung phiên bản
//    implementation(platform(libs.firebase.bom))
//    implementation(libs.firebase.auth)
//    implementation(libs.firebase.firestore)
//    implementation(libs.firebase.database)
//    implementation(libs.firebase.storage)
//
//    implementation(libs.androidx.core.ktx)
//    implementation(libs.androidx.appcompat)
//    implementation(libs.material)
//    implementation(libs.androidx.activity)
//    implementation(libs.androidx.constraintlayout)
//    implementation("com.google.code.gson:gson:2.10.1")
//
//    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.1.0")
//    testImplementation(libs.junit)
//    androidTestImplementation(libs.androidx.junit)
//    androidTestImplementation(libs.androidx.espresso.core)
//
//
//    //vừa thêm đoạn 20:45
//    implementation ("com.tbuonomo:dotsindicator:5.1.0")
//
//
//
//    //vừa thêm đoạn 47.22
//
//    implementation("com.github.bumptech.glide:glide:4.12.0")
//    implementation("androidx.recyclerview:recyclerview:1.3.2")
//
//   implementation("com.google.firebase:firebase-auth") //tích hợp đăng ký đăng nhập

//implementation ("com.cloudinary:cloudinary-android:2.3.1") //up lên cloudinary
//}
