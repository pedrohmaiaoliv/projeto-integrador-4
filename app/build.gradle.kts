plugins {
    alias(libs.plugins.android.application)
    // Plugin para Android Application, necessário para construir o APK ou AAB
    alias(libs.plugins.jetbrains.kotlin.android)
    // Plugin do Kotlin para Android, necessário para usar o Kotlin no projeto
    id("com.google.gms.google-services")
    // Plugin do Google Services para integrar serviços como Firebase
}

android {
    namespace = "com.example.google_books_project"
    // Definição do namespace para o seu projeto, importante para a organização do código
    compileSdk = 34
    // Versão do SDK do Android usada para compilar o projeto

    defaultConfig {
        applicationId = "com.example.google_books_project"
        // ID único da aplicação no formato de domínio reverso
        minSdk = 24
        // Versão mínima do SDK do Android necessária para rodar o aplicativo
        targetSdk = 34
        // Versão do SDK do Android que o aplicativo foi testado para funcionar corretamente
        versionCode = 1
        // Número da versão da aplicação (incrementado em cada atualização)
        versionName = "1.0"
        // Nome da versão da aplicação, usado no processo de distribuição

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        // Configuração do runner de testes
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            // Desativa o minify para release (normalmente usado para reduzir o tamanho do APK)
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                // Arquivo de configuração padrão do ProGuard para otimizações
                "proguard-rules.pro"
                // Arquivo de regras personalizadas do ProGuard
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        // Define a versão do Java para compatibilidade com o código-fonte
        targetCompatibility = JavaVersion.VERSION_1_8
        // Define a versão do Java para a compatibilidade com o bytecode
    }

    kotlinOptions {
        jvmTarget = "1.8"
        // Define a versão do JVM para o Kotlin (compatibilidade com Java 8)
    }

    buildFeatures {
        viewBinding = true
        // Habilita o recurso de ViewBinding para uma melhor manipulação das views no código
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    // Biblioteca do AndroidX para uso de extensões do Kotlin
    implementation(libs.androidx.appcompat)
    // Biblioteca de compatibilidade para versões anteriores do Android
    implementation(libs.material)
    // Biblioteca para usar componentes Material Design
    implementation(libs.androidx.activity)
    // Biblioteca para Activity do AndroidX
    implementation(libs.androidx.constraintlayout)
    // Biblioteca para layouts de ConstraintLayout
    implementation(libs.firebase.firestore)
    // Biblioteca para integrar o Firestore, banco de dados no Firebase
    implementation(libs.androidx.navigation.runtime.ktx)
    // Biblioteca de navegação do AndroidX, com extensões Kotlin
    implementation(libs.androidx.navigation.fragment)
    // Biblioteca para navegação com fragments
    implementation(libs.androidx.navigation.fragment.ktx)
    // Biblioteca com extensões Kotlin para navegação com fragments
    implementation(libs.books)
    // Dependência personalizada para livros, provavelmente uma biblioteca relacionada ao seu app
    testImplementation(libs.junit)
    // Biblioteca de testes unitários JUnit
    androidTestImplementation(libs.androidx.junit)
    // Biblioteca de suporte para testes com JUnit no Android
    androidTestImplementation(libs.androidx.espresso.core)
    // Biblioteca para realizar testes de UI com Espresso

    // Outras dependências
    implementation("com.google.android.material:material:1.9.0")
    // Dependência do Material Components para UI
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    // Biblioteca Retrofit para integração de APIs REST
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    // Converte objetos JSON para objetos Kotlin com Gson no Retrofit
    implementation("com.squareup.picasso:picasso:2.71828")
    // Biblioteca para carregamento de imagens em Android com Picasso

    // Firebase BoM (Bill of Materials)
    implementation(platform("com.google.firebase:firebase-bom:33.6.0"))
    // Usado para gerenciar versões das bibliotecas do Firebase de forma consistente

    // Firebase Analytics
    implementation("com.google.firebase:firebase-analytics-ktx")
    // Biblioteca do Firebase para Analytics (métricas de uso do app)

    // Outras bibliotecas Firebase
    implementation("com.google.firebase:firebase-auth-ktx")
    // Biblioteca do Firebase para autenticação de usuários
    implementation("com.google.firebase:firebase-firestore-ktx")
    // Biblioteca do Firebase Firestore para armazenamento de dados
}

apply(plugin = "com.google.gms.google-services")
// Aplica o plugin do Google Services para habilitar a integração com os serviços do Firebase
