plugins {
    id("java-library")
    id("kotlin")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    // OkHttp
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    // kotlinx coroutine
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.1")
    // test
    testImplementation("junit:junit:4.13.2")
}