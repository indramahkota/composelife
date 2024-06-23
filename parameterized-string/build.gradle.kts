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

import com.alexvanyo.composelife.buildlogic.FormFactor
import com.alexvanyo.composelife.buildlogic.configureGradleManagedDevices

plugins {
    alias(libs.plugins.convention.kotlinMultiplatform)
    alias(libs.plugins.convention.androidLibrary)
    alias(libs.plugins.convention.androidLibraryCompose)
    alias(libs.plugins.convention.androidLibraryJacoco)
    alias(libs.plugins.convention.androidLibraryTesting)
    alias(libs.plugins.convention.detekt)
    alias(libs.plugins.convention.kotlinMultiplatformCompose)
    alias(libs.plugins.gradleDependenciesSorter)
}

android {
    namespace = "com.alexvanyo.composelife.parameterizedstring"
    defaultConfig {
        minSdk = 21
    }
    configureGradleManagedDevices(FormFactor.All, this)
}

kotlin {
    jvm("desktop")
    androidTarget()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(libs.jetbrains.compose.runtime)
                api(libs.jetbrains.compose.uiText)

                implementation(libs.kotlinx.coroutines.core)
            }
        }
        val androidMain by getting {
            dependencies {
                api(libs.androidx.compose.foundation)

                implementation(libs.androidx.core)
                implementation(libs.androidx.tracing)
                implementation(libs.kotlinx.coroutines.android)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlinx.coroutines.test)
            }
        }
        val androidSharedTest by getting {
            dependencies {
                implementation(libs.androidx.compose.uiTestJunit4)
                implementation(libs.androidx.test.core)
                implementation(libs.androidx.test.espresso)
                implementation(projects.testActivity)
            }
        }
    }
}
