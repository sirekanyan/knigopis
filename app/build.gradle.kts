import org.jetbrains.kotlin.gradle.internal.AndroidExtensionsExtension

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
}

androidExtensions {
    // fixme: https://youtrack.jetbrains.com/issue/KT-22213
    configure(delegateClosureOf<AndroidExtensionsExtension> {
        isExperimental = true
    })
}

android {
    compileSdkVersion(28)
    buildToolsVersion("28.0.3")
    defaultConfig {
        applicationId = "com.sirekanyan.knigopis"
        minSdkVersion(16)
        targetSdkVersion(28)
        versionCode = 25
        versionName = "0.2.3"
        setProperty("archivesBaseName", "$applicationId-$versionName-$versionCode")
        vectorDrawables.useSupportLibrary = true
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard")
        }
        getByName("debug") {
            applicationIdSuffix = ".debug"
        }
    }
}

dependencies {
    // kotlin standard library
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.11")

    // support libraries
    implementation("com.android.support:appcompat-v7:28.0.0")
    implementation("com.android.support:design:28.0.0")
    implementation("com.android.support:support-vector-drawable:28.0.0")
    implementation("com.android.support.constraint:constraint-layout:1.1.3")

    // rxjava
    implementation("io.reactivex.rxjava2:rxjava:2.2.5")
    implementation("io.reactivex.rxjava2:rxkotlin:2.3.0")
    implementation("io.reactivex.rxjava2:rxandroid:2.1.0")
    implementation("com.github.tbruyelle:rxpermissions:0.10.2")

    // retrofit & okhttp
    implementation("com.squareup.retrofit2:retrofit:2.5.0")
    implementation("com.squareup.retrofit2:adapter-rxjava2:2.5.0")
    implementation("com.squareup.retrofit2:converter-gson:2.5.0")
    implementation("com.squareup.okhttp3:logging-interceptor:3.12.1")

    // etc
    implementation("com.github.bumptech.glide:glide:4.8.0")
    implementation(files("libs/ulogin-sdk-v1.1.aar"))
}

task("updateReadme") {
    dependsOn("assembleRelease")
    doLast {
        val releaseVariant = android.applicationVariants.first { it.name == "release" }
        val releaseFiles = releaseVariant.outputs.map { it.outputFile }
        val apkFile = releaseFiles.single { it.exists() && it.extension == "apk" }
        val apkSize = "%.2f".format(apkFile.length().toFloat() / 1024 / 1024)
        rootProject.file("README.md").printWriter().use { readme ->
            rootProject.file("readme.md").forEachLine { line ->
                readme.appendln(line.replace("{{apkSize}}", apkSize))
            }
        }
    }
}
