plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("com.squareup.sqldelight")
    kotlin("plugin.serialization")
}

kotlin {
    android {
        compilations {
            compilations.all {
                kotlinOptions {
                    // Setup the Kotlin compiler options for the 'main' compilation:
                    jvmTarget = "1.8"
                }
            }
        }
    }

    val onPhone = System.getenv("SDK_NAME")?.startsWith("iphoneos") ?: false
    if (onPhone) {
        iosArm64("ios")
    } else {
        iosX64("ios")
    }
    val sql_delight_version = "1.4.4"
    val coroutinesVersion = "1.3.9-native-mt"
    val serializationVersion = "1.0.0-RC"
    val ktorVersion = "1.4.0"
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("com.squareup.sqldelight:runtime:$sql_delight_version")
                implementation("com.squareup.sqldelight:coroutines-extensions:$sql_delight_version")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion") {
                    version {
                        strictly(coroutinesVersion)
                    }
                }
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:$serializationVersion")
                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("io.ktor:ktor-client-serialization:$ktorVersion")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation("com.squareup.sqldelight:android-driver:$sql_delight_version")
            }
        }
        val androidTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("junit:junit:4.13.1")
            }
        }
        val iosMain by getting {
            dependencies {
                implementation("com.squareup.sqldelight:native-driver:$sql_delight_version")
            }
        }
        val iosTest by getting
    }
}

android {
    compileSdkVersion(29)
    defaultConfig {
        minSdkVersion(17)
        targetSdkVersion(29)
    }
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

}

sqldelight {
    database("AppDatabase") {
        packageName = "com.jide.surveyapp"
    }
}