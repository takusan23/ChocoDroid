buildscript {
    val kotlinVersion: String by extra("1.7.0")
    val composeVersion: String by extra("1.2.0-rc03")
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application").version("7.1.3").apply(false)
    id("com.android.library").version("7.1.3").apply(false)
    id("org.jetbrains.kotlin.android").version("1.7.0").apply(false)
}

tasks.register("clean") {
    doFirst {
        delete(rootProject.buildDir)
    }
}
