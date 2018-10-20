buildscript {
    repositories {
        jcenter()
        google()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:3.2.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.2.71")
    }
}

allprojects {
    repositories {
        jcenter()
        mavenCentral()
        google()
        flatDir {
            dirs("libs")
        }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
