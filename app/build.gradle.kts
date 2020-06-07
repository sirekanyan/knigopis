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
        minSdkVersion(21)
        targetSdkVersion(29)
        versionCode = 29
        versionName = "0.3.2"
        setProperty("archivesBaseName", "$applicationId-$versionName-$versionCode")
        vectorDrawables.useSupportLibrary = true
        manifestPlaceholders = mapOf(
            "LOGIN_CALLBACK_SCHEME" to "e270636c0efc6cad95130113d3bbafc3",
            "LOGIN_CALLBACK_HOST" to "532b8e7fc54c52b6df5b55181acc241a",
            "LOGIN_CALLBACK_PATH" to "$versionCode"
        )
        manifestPlaceholders.forEach { (key, value) ->
            buildConfigField("String", key, "\"$value\"")
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard")
        }
        getByName("debug") {
            applicationIdSuffix = ".debug"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    // kotlin standard library
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.3.72")

    // androidx libraries
    implementation("androidx.appcompat:appcompat:1.1.0")
    implementation("androidx.constraintlayout:constraintlayout:1.1.3")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.0.0")
    implementation("androidx.browser:browser:1.2.0")

    // rxjava
    implementation("io.reactivex.rxjava2:rxjava:2.2.19")
    implementation("io.reactivex.rxjava2:rxkotlin:2.4.0")
    implementation("io.reactivex.rxjava2:rxandroid:2.1.1")

    // retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:adapter-rxjava2:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // okhttp
    @Suppress("GradleDependency")
    implementation("com.squareup.okhttp3:logging-interceptor:3.14.9")

    // etc
    implementation("com.google.android.material:material:1.1.0")
    implementation("com.github.bumptech.glide:glide:4.11.0")

    // crash reporting
    implementation("ch.acra:acra-http:5.1.3")
}

task("updateReadme") {
    dependsOn("assembleRelease")
    doLast {
        val releaseVariant = android.applicationVariants.first { it.name == "release" }
        val releaseFiles = releaseVariant.outputs.map { it.outputFile }
        val apkFile = releaseFiles.single { it.exists() && it.extension == "apk" }
        val properties = mapOf(
            "apkSize" to "%.2f".format(apkFile.length().toFloat() / 1024 / 1024),
            "appVersion" to android.defaultConfig.versionName.orEmpty(),
            "minSdkVersion" to android.defaultConfig.minSdkVersion?.apiLevel?.toString().orEmpty()
        )
        rootProject.file("README.md").printWriter().use { readme ->
            rootProject.file("readme.md").forEachLine { inputLine ->
                readme.appendln(
                    properties.entries.fold(inputLine) { line, (key, value) ->
                        line.replace("{{$key}}", value)
                    }
                )
            }
        }
    }
}
