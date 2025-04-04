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

package com.alexvanyo.composelife.ui.mobile

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import com.alexvanyo.composelife.preferences.DarkThemeConfig
import com.alexvanyo.composelife.preferences.darkThemeConfigState
import com.alexvanyo.composelife.preferences.di.ComposeLifePreferencesProvider
import com.alexvanyo.composelife.resourcestate.ResourceState

/**
 * A composition local tracking whether or not the [ComposeLifeTheme] has already been applied.
 */
@Suppress("ComposeCompositionLocalUsage")
private val LocalAppliedComposeLifeTheme = compositionLocalOf { false }

/**
 * Applies the [ComposeLifeTheme] with the dark theme based given by [shouldUseDarkTheme], which is based off of the
 * preferences from [ComposeLifePreferencesProvider].
 *
 * If [ComposeLifeTheme] is applied multiple times, only the outer one takes effect.
 */
context(_: ComposeLifePreferencesProvider)
@Composable
fun ComposeLifeTheme(
    content: @Composable () -> Unit,
) = ComposeLifeTheme(
    darkTheme = shouldUseDarkTheme(),
    content = content,
)

/**
 * Applies the [ComposeLifeTheme] with the given [darkTheme].
 *
 * If [ComposeLifeTheme] is applied multiple times, only the outer one takes effect.
 */
@Composable
fun ComposeLifeTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit,
) {
    if (LocalAppliedComposeLifeTheme.current) {
        // Render the content directly if the theme has already been applied
        content()
    } else {
        // Otherwise, mark that we've applied the theme, and wrap the content
        CompositionLocalProvider(LocalAppliedComposeLifeTheme provides true) {
            MaterialTheme(
                colorScheme = ComposeLifeTheme.colorScheme(darkTheme),
                typography = Typography(),
                content = content,
            )
        }
    }
}

@Immutable
object ComposeLifeTheme {

    @Composable
    fun colorScheme(darkTheme: Boolean) =
        if (darkTheme) {
            darkColorScheme
        } else {
            lightColorScheme
        }

    val aliveCellColor
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme.onSurface

    val pendingAliveCellColor
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)

    val deadCellColor
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme.surface

    val pendingDeadCellColor
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
}

@get:Composable
@get:ReadOnlyComposable
internal expect val ComposeLifeTheme.lightColorScheme: ColorScheme

@get:Composable
@get:ReadOnlyComposable
internal expect val ComposeLifeTheme.darkColorScheme: ColorScheme

context(preferencesProvider: ComposeLifePreferencesProvider)
@Composable
fun shouldUseDarkTheme(): Boolean =
    when (
        val darkThemeConfigState = preferencesProvider.composeLifePreferences.darkThemeConfigState
    ) {
        ResourceState.Loading,
        is ResourceState.Failure,
        -> isSystemInDarkTheme()
        is ResourceState.Success -> when (darkThemeConfigState.value) {
            DarkThemeConfig.FollowSystem -> isSystemInDarkTheme()
            DarkThemeConfig.Dark -> true
            DarkThemeConfig.Light -> false
        }
    }
