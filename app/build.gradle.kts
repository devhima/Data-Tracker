
plugins {
    id("com.android.application")
    
}

android {

    namespace = "com.devhima.datatracker"
    compileSdk = 33
    
    
    signingConfigs {
        getByName("debug") {
            keyAlias = "devhima"
            keyPassword = "devhima"
            storeFile = file("/storage/emulated/0/AndroidIDEProjects/Data/devhima.keystore")
            storePassword = "devhima"
        }
        create("release") {
            keyAlias = "devhima"
            keyPassword = "devhima"
            storeFile = file("/storage/emulated/0/AndroidIDEProjects/Data/devhima.keystore")
            storePassword = "devhima"
        }
    }
    
    defaultConfig {
        applicationId = "com.devhima.datatracker"
        minSdk = 21
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
        
        vectorDrawables { 
            useSupportLibrary = true
        }
        
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildTypes {
        /*release {
            //isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }*/
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
            isDebuggable = false
        }
        getByName("debug") {
            signingConfig = signingConfigs.getByName("debug")
            isDebuggable = true
        }
    }

    buildFeatures {
        viewBinding = true
        
    }
    
}

dependencies {


    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
   implementation ("androidx.preference:preference:1.2.0")
}
