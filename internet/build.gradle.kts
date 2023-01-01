plugins {
    id("java-library")
    id("kotlin")
    kotlin("plugin.serialization") version "1.6.20"
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    // Gradle の Version Catalog 機能を使い、ライブラリのバージョンを一元管理しています。
    // libs.version.toml を見てください。

    // HTML Parser
    implementation(libs.jsoup)
    // OkHttp
    implementation(libs.okhttp)
    // kotlinx serialization
    implementation(libs.kotlinx.serialization.json)
    // kotlinx coroutine
    implementation(libs.kotlinx.coroutine)
    // test
    testImplementation(libs.junit)
}
