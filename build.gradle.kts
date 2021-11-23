// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {

    val kotlinVersion: String by extra("1.5.31")
    val composeVersion: String by extra("1.1.0-beta03")

    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.0.3")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${kotlinVersion}")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

tasks.register("clean") {
    doFirst {
        delete(rootProject.buildDir)
    }
}
