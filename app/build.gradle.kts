import java.util.*

val kotlinVersion: String by rootProject.extra
val composeVersion: String by rootProject.extra

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
}

android {
    compileSdk = 33
    namespace = "io.github.takusan23.chocodroid"

    defaultConfig {
        applicationId = "io.github.takusan23.chocodroid"
        minSdk = 21
        targetSdk = 33
        versionCode = 1
        versionName = "1.0.0 beta01"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        javaCompileOptions {
            annotationProcessorOptions {
                arguments += "room.schemaLocation" to "$projectDir/room_db_schema"
            }
        }

        // ヒルド日時を Context#getString で参照可能にする
        resValue("string", "build_date", System.currentTimeMillis().toString())
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
        // useIR = true
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.3.2"
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    // APK作成と署名の設定
    signingConfigs {
        // 予め GitHubActions の環境変数に入れておく
        // ローカル環境で行う場合 (Windows cmd.exe)
        // set ENV_SIGN_KEYSTORE_BASE64 = Base64エンコードした署名ファイル
        // set ENV_SIGN_KEY_ALIAS = KeyAlias
        // set ENV_SIGN_KEY_PASSWORD = KeyPassword
        // set ENV_SIGN_STORE_PASSWORD = StorePassword
        create("release_signing_config") {
            // 存在しない場合はとりあえずスルーする
            if (System.getenv("ENV_SIGN_KEYSTORE_BASE64") != null) {
                // GitHubActionsの環境変数に入れておいた署名ファイルがBase64でエンコードされているので戻す
                System.getenv("ENV_SIGN_KEYSTORE_BASE64").let { base64 ->
                    val decoder = Base64.getDecoder()
                    // ルートフォルダに作成する
                    File("keystore.jks").also { file ->
                        file.createNewFile()
                        file.writeBytes(decoder.decode(base64))
                    }
                }
                // どうやら appフォルダ の中を見に行ってるみたいなのでプロジェクトのルートフォルダを指定する
                storeFile = File(rootProject.projectDir, "keystore.jks")
                keyAlias = System.getenv("ENV_SIGN_KEY_ALIAS")
                keyPassword = System.getenv("ENV_SIGN_KEY_PASSWORD")
                storePassword = System.getenv("ENV_SIGN_STORE_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            // 署名の設定を適用する
            signingConfig = signingConfigs.getByName("release_signing_config")
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

}

dependencies {
    // Gradle の Version Catalog 機能を使い、ライブラリのバージョンを一元管理しています。
    // libs.version.toml を見てください。

    // API叩くやつ
    implementation(project(":internet"))
    // 分割ダウンロード
    implementation(project(":downloadpocket"))

    // Android
    implementation(libs.androidx.core)
    implementation(libs.androidx.appcompat)
    implementation(libs.android.material)
    implementation(libs.androidx.lifecycle)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.datastore)
    implementation(libs.androidx.media)
    implementation(libs.androidx.lifecycle.service)

    // Jetpack Compose
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.ui.tooling.preview)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.compose.material3)

    // ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose) // collectAsStateWithLifecycle を使いたい

    // ExoPlayer。生放送用のHlsと、一部の動画はDashで配信されている
    implementation(libs.exoplayer.core)
    implementation(libs.exoplayer.hls)
    implementation(libs.exoplayer.dash)

    // Coil
    implementation(libs.coil)
    implementation(libs.coil.compose)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    annotationProcessor(libs.androidx.room.compiler)
    kapt("androidx.room:room-compiler:${libs.versions.androidxRoom.get()}")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
}