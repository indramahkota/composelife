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

package com.alexvanyo.composelife.buildlogic

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

fun Project.configureAndroid(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
    val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

    commonExtension.apply {
        compileSdk = 35

        lint {
            warningsAsErrors = true
            disable.addAll(listOf("GradleDependency", "OldTargetApi", "AndroidGradlePluginVersion"))
            enable.addAll(listOf("UnsupportedChromeOsHardware"))
        }

        defaultConfig {
            vectorDrawables {
                useSupportLibrary = true
            }
        }

        buildTypes {
            getByName("debug") {
                isPseudoLocalesEnabled = true
            }
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11
            isCoreLibraryDesugaringEnabled = true
        }

        // Workaround for https://github.com/Kotlin/kotlinx.coroutines/blob/master/kotlinx-coroutines-debug/README.md#build-failures-due-to-duplicate-resource-files
        packaging {
            resources.excludes.addAll(
                listOf(
                    "/META-INF/AL2.0",
                    "/META-INF/LGPL2.1",
                    "/META-INF/LICENSE.md",
                    "/META-INF/LICENSE-notice.md",
                    "/META-INF/com.google.dagger_dagger.version",
                ),
            )
        }
    }

    configurations.configureEach {
        resolutionStrategy.eachDependency {
            val version = requested.version.orEmpty()
            // Force guava to always use the android version instead of the jre version
            if (requested.group == "com.google.guava" && requested.name == "guava" && version.endsWith("jre")) {
                useVersion(version.removeSuffix("jre") + "android")
            }
        }
    }

    dependencies {
        add("coreLibraryDesugaring", libs.findLibrary("android.desugarJdkLibs").get())
    }
}
