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
    id("com.alexvanyo.composelife.kotlin.multiplatform")
    id("com.alexvanyo.composelife.android.library")
    id("com.alexvanyo.composelife.android.library.jacoco")
    id("com.alexvanyo.composelife.android.library.testing")
    id("com.alexvanyo.composelife.detekt")
    alias(libs.plugins.sqldelight)
    kotlin("kapt")
}

android {
    namespace = "com.alexvanyo.composelife.database"
    defaultConfig {
        minSdk = 21
    }
    lint {
        lintConfig = file("lint.xml")
    }
    configureGradleManagedDevices(FormFactor.All, this)
}

kotlin {
    android()
    jvm()

    sourceSets {
        val commonMain by getting {
            configurations["kapt"].dependencies.add(libs.dagger.hilt.compiler.get())
            dependencies {
                api(projects.dispatchers)
                api(projects.updatable)

                api(libs.kotlinx.coroutines.core)
                implementation(libs.dagger.hilt.core)
                implementation(libs.sqldelight.coroutinesExtensions)
                implementation(libs.sqldelight.primitiveAdapters)
            }
        }
        val androidMain by getting {
            dependencies {
                api(libs.kotlinx.coroutines.android)
                implementation(libs.dagger.hilt.android)
                implementation(libs.sqldelight.androidDriver)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(projects.databaseTest)
                implementation(projects.dispatchersTest)

                implementation(libs.kotlinx.coroutines.test)
                implementation(libs.turbine)
            }
        }
        val androidSharedTest by getting {
            dependencies {
                implementation(libs.androidx.test.core)
                implementation(libs.androidx.test.junit)
                implementation(libs.androidx.test.runner)
            }
        }
        val androidUnitTest by getting {
            configurations["kaptTest"].dependencies.add(libs.dagger.hilt.compiler.get())
        }
        val androidInstrumentedTest by getting {
            configurations["kaptAndroidTest"].dependencies.add(libs.dagger.hilt.compiler.get())
        }
    }
}

sqldelight {
    databases {
        create("ComposeLifeDatabase") {
            packageName.set("com.alexvanyo.composelife.database")
            schemaOutputDirectory.set(file("src/commonMain/sqldelight/databases"))
            verifyMigrations.set(true)
        }
    }
}
