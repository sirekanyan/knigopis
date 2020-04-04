plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
}

androidExtensions {
    isExperimental = true
}

android {
    compileSdkVersion(29)
    buildToolsVersion("29.0.3")
    defaultConfig {
        applicationId = "com.sirekanyan.knigopis"
        minSdkVersion(16)
        targetSdkVersion(29)
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
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.72")

    // androidx libraries
    implementation("androidx.appcompat:appcompat:1.1.0")
    implementation("androidx.constraintlayout:constraintlayout:1.1.3")
    implementation("androidx.browser:browser:1.0.0")

    // rxjava
    implementation("io.reactivex.rxjava2:rxjava:2.2.15")
    implementation("io.reactivex.rxjava2:rxkotlin:2.4.0")
    implementation("io.reactivex.rxjava2:rxandroid:2.1.1")

    // retrofit
    implementation("com.squareup.retrofit2:retrofit:2.6.2")
    implementation("com.squareup.retrofit2:adapter-rxjava2:2.6.2")
    implementation("com.squareup.retrofit2:converter-gson:2.6.2")

    // okhttp
    @Suppress("GradleDependency")
    implementation("com.squareup.okhttp3:logging-interceptor:3.12.6") // use 3.12.x if minSdkVersion < 21

    // etc
    implementation("com.google.android.material:material:1.0.0")
    implementation("com.github.bumptech.glide:glide:4.10.0")
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
