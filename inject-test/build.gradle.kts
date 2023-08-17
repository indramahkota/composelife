/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

plugins {
    id("com.alexvanyo.composelife.kotlin.multiplatform")
    id("com.alexvanyo.composelife.android.library")
    id("com.alexvanyo.composelife.detekt")
}

android {
    namespace = "com.alexvanyo.composelife.injecttest"
    defaultConfig {
        minSdk = 21
    }
}

kotlin {
    androidTarget()
    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(projects.databaseTest)
                implementation(projects.injectScopes)
                api(projects.kmpAndroidRunner)
                api(projects.preferencesTest)

                implementation(kotlin("test-junit"))
                api(libs.kotlinx.coroutines.test)
            }
        }
        val androidMain by getting {
            dependencies {
                api(libs.androidx.test.runner)
                api(libs.androidx.compose.uiTestJunit4)
                implementation(libs.leakCanary.android)
                api(libs.leakCanary.instrumentation)
            }
        }
    }
}