apply plugin: 'com.android.application'
apply plugin: 'kotlin-android'
apply plugin: 'kotlin-android-extensions'

androidExtensions {
    experimental = true
}

android {
    compileSdkVersion 28
    buildToolsVersion '28.0.3'
    defaultConfig {
        applicationId 'com.sirekanyan.knigopis'
        minSdkVersion 16
        targetSdkVersion 28
        versionCode 23
        versionName '0.2.1'
        archivesBaseName = "$applicationId-$versionName-$versionCode"
        vectorDrawables.useSupportLibrary = true
    }
    buildTypes {
        release {
            minifyEnabled true
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard'
        }
        debug {
            applicationIdSuffix '.debug'
        }
    }
    afterEvaluate {
        assembleRelease.doLast {
            applicationVariants.find { it.name == 'release' }.outputs.each { output ->
                if (output.outputFile.exists()) {
                    def apkSize = (output.outputFile.size().toFloat() / 1024 / 1024).round(2)
                    rootProject.file('README.md').text = rootProject.file('readme.md').text
                            .replaceAll('::versionName::', output.apkData.versionName.toString())
                            .replaceAll('::versionCode::', output.apkData.versionCode.toString())
                            .replaceAll('::apkSize::', apkSize.toString())
                }
            }
        }
    }
}

dependencies {
    // kotlin standard library
    implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlin_version"

    // support libraries
    implementation 'com.android.support:appcompat-v7:28.0.0'
    implementation 'com.android.support:design:28.0.0'
    implementation 'com.android.support:support-vector-drawable:28.0.0'
    implementation 'com.android.support.constraint:constraint-layout:1.1.3'

    // rxjava
    implementation 'io.reactivex.rxjava2:rxjava:2.1.11'
    implementation 'io.reactivex.rxjava2:rxkotlin:2.2.0'
    implementation 'io.reactivex.rxjava2:rxandroid:2.0.2'
    implementation 'com.tbruyelle.rxpermissions2:rxpermissions:0.9.4@aar'

    // retrofit & okhttp
    implementation 'com.squareup.retrofit2:retrofit:2.3.0'
    implementation 'com.squareup.retrofit2:adapter-rxjava2:2.3.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.3.0'
    implementation 'com.squareup.okhttp3:logging-interceptor:3.9.1'

    // etc
    implementation 'com.github.bumptech.glide:glide:4.7.1'
    implementation(name: 'ulogin-sdk-v1.1', ext: 'aar')
}

clean {
    delete 'release'
}