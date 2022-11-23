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
    namespace ="io.github.takusan23.chocodroid"

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
        /**
         * 予め GitHubActions の環境変数に入れておく
         *
         * ローカル環境で行う場合 (Windows cmd.exe)
         *
         * set ENV_SIGN_KEYSTORE_BASE64 = Base64エンコードした署名ファイル
         * set ENV_SIGN_KEY_ALIAS = KeyAlias
         * set ENV_SIGN_KEY_PASSWORD = KeyPassword
         * set ENV_SIGN_STORE_PASSWORD = StorePassword
         * */
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
    // API叩くやつ
    implementation(project(":internet"))
    // 分割ダウンロード
    implementation(project(":downloadpocket"))

    // Android
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.5.1")
    implementation("com.google.android.material:material:1.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")
    implementation("androidx.activity:activity-compose:1.6.1")
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("androidx.media:media:1.6.0")

    // Jetpack Compose
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.material:material:$composeVersion")
    implementation("androidx.compose.ui:ui-tooling-preview:$composeVersion")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:$composeVersion")
    debugImplementation("androidx.compose.ui:ui-tooling:$composeVersion")
    implementation("androidx.navigation:navigation-compose:2.4.0-rc01")
    implementation("com.google.accompanist:accompanist-swiperefresh:0.20.0")
    // Compose + Material You
    implementation("androidx.compose.material3:material3:1.1.0-alpha02")
    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.5.1")

    // ExoPlayer。生放送用のHlsと、一部の動画はDashで配信されている
    implementation("com.google.android.exoplayer:exoplayer-core:2.18.1")
    implementation("com.google.android.exoplayer:exoplayer-hls:2.18.1")
    implementation("com.google.android.exoplayer:exoplayer-dash:2.18.1")

    // Coil
    implementation("io.coil-kt:coil:2.2.2")
    implementation("io.coil-kt:coil-compose:2.2.2")

    // Room
    val roomVersion = "2.5.0-alpha02"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    annotationProcessor("androidx.room:room-compiler:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}