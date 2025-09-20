plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.listadetarefas"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.listadetarefas"
        minSdk = 24        // Android 7.0 (seu Moto G6 é Android 9, então funciona)
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // -----------------------------
    // Configuração de assinatura
    signingConfigs {
        create("release") {
            storeFile = file("release-keystore.jks")   // Certifique-se que o .jks está dentro da pasta "app"
            storePassword = "0307301819gG$"            // Sua senha do keystore
            keyAlias = "key1"                          // Seu alias
            keyPassword = "0307301819gG$"              // Senha da chave
        }
    }

    buildTypes {
        release {
            // 🚫 Sem minify (ProGuard desligado)
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
        }

        debug {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation("com.google.code.gson:gson:2.10.1")
}
