buildscript {
    repositories {
        jcenter()
        google()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:3.4.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.41")
    }
}

allprojects {
    repositories {
        jcenter()
        google()
        maven {
            url = uri("https://jitpack.io")
            content {
                includeGroup("com.github.tbruyelle")
            }
        }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
