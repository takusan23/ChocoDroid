plugins {
    id("java-library")
    id("kotlin")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    // Gradle の Version Catalog 機能を使い、ライブラリのバージョンを一元管理しています。
    // libs.version.toml を見てください。

    // OkHttp
    implementation(libs.okhttp)
    // kotlinx coroutine
    implementation(libs.kotlinx.coroutine)
    // test
    testImplementation(libs.junit)
}