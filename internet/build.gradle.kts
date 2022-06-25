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
    // HTML Parser
    implementation("org.jsoup:jsoup:1.14.3")
    // OkHttp
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    // kotlinx serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.0")
    // kotlinx coroutine
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.1")
    // test
    testImplementation("junit:junit:4.13.2")
}
