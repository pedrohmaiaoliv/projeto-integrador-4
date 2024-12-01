plugins {
    alias(libs.plugins.android.application) // Plugin para criar um aplicativo Android.
    alias(libs.plugins.jetbrains.kotlin.android) // Plugin para suporte ao Kotlin no Android.
    id("com.google.gms.google-services") // Plugin necessário para usar serviços do Firebase (Google Services).
}

android {
    namespace = "com.example.google_books_project" // Define o namespace do aplicativo.
    compileSdk = 34 // Versão da SDK usada para compilar o projeto.

    defaultConfig {
        applicationId = "com.example.google_books_project" // Identificador único do aplicativo.
        minSdk = 24 // Versão mínima da SDK suportada pelo aplicativo.
        targetSdk = 34 // Versão alvo da SDK em que o aplicativo foi testado.
        versionCode = 1 // Código de versão do aplicativo (incrementado para cada lançamento).
        versionName = "1.0" // Nome da versão exibido aos usuários.

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner" // Runner para testes instrumentados.
    }

    buildTypes {
        release {
            isMinifyEnabled = false // Minificação desativada para builds de produção.
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), // Arquivo padrão do ProGuard.
                "proguard-rules.pro" // Regras personalizadas do ProGuard.
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8 // Compatibilidade com o Java 8.
        targetCompatibility = JavaVersion.VERSION_1_8 // Alvo da compatibilidade do Java.
    }
    kotlinOptions {
        jvmTarget = "1.8" // Configuração do alvo do Kotlin para o Java 8.
    }
    buildFeatures {
        viewBinding = true // Habilita o ViewBinding para facilitar a manipulação de layouts.
    }
}

dependencies {
    // Dependências do AndroidX e Material Design
    implementation(libs.androidx.core.ktx) // Extensões para Kotlin no Android.
    implementation(libs.androidx.appcompat) // Suporte para compatibilidade com versões anteriores.
    implementation(libs.material) // Componentes de Material Design.
    implementation(libs.androidx.activity) // Suporte para atividades.
    implementation(libs.androidx.constraintlayout) // Layout com constraints.

    // Dependências do Firebase
    implementation(libs.firebase.firestore) // Biblioteca do Firestore.
    implementation(platform("com.google.firebase:firebase-bom:33.6.0")) // Firebase BoM (gerencia versões compatíveis).
    implementation("com.google.firebase:firebase-analytics-ktx") // Firebase Analytics.
    implementation("com.google.firebase:firebase-auth-ktx") // Firebase Authentication.
    implementation("com.google.firebase:firebase-firestore-ktx") // Firebase Firestore.

    // Dependências de navegação
    implementation(libs.androidx.navigation.runtime.ktx) // Navegação em tempo de execução.
    implementation(libs.androidx.navigation.fragment) // Navegação em fragmentos.
    implementation(libs.androidx.navigation.fragment.ktx) // Extensões Kotlin para navegação em fragmentos.

    // Dependências para Retrofit e Picasso
    implementation("com.squareup.retrofit2:retrofit:2.9.0") // Biblioteca para chamadas de rede.
    implementation("com.squareup.retrofit2:converter-gson:2.9.0") // Conversor de JSON para objetos.
    implementation("com.squareup.picasso:picasso:2.71828") // Biblioteca para carregamento de imagens.

    // Testes
    testImplementation(libs.junit) // Biblioteca para testes unitários.
    androidTestImplementation(libs.androidx.junit) // Extensão do JUnit para Android.
    androidTestImplementation(libs.androidx.espresso.core) // Biblioteca para testes de interface.

    // Material Design (dependência específica)
    implementation("com.google.android.material:material:1.9.0") // Componentes do Material Design.
}

apply(plugin = "com.google.gms.google-services") // Aplica o plugin do Google Services necessário para Firebase.
