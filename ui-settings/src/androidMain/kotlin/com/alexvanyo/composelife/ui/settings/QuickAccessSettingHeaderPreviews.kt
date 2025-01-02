/*
 * Copyright 2023 The Android Open Source Project
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

package com.alexvanyo.composelife.ui.settings

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.airbnb.android.showkase.annotation.ShowkaseComposable
import com.alexvanyo.composelife.ui.mobile.ComposeLifeTheme
import com.alexvanyo.composelife.ui.settings.entrypoints.WithPreviewDependencies
import com.alexvanyo.composelife.ui.util.ThemePreviews

@ShowkaseComposable
@ThemePreviews
@Composable
internal fun QuickAccessSettingHeaderIsFavoritePreview(modifier: Modifier = Modifier) {
    WithPreviewDependencies {
        ComposeLifeTheme {
            Surface(modifier) {
                QuickAccessSettingHeader(
                    isFavorite = true,
                    setIsFavorite = {},
                )
            }
        }
    }
}

@ShowkaseComposable
@ThemePreviews
@Composable
internal fun QuickAccessSettingHeaderWithOpenInSettingsPreview(modifier: Modifier = Modifier) {
    WithPreviewDependencies {
        ComposeLifeTheme {
            Surface(modifier) {
                QuickAccessSettingHeader(
                    isFavorite = true,
                    setIsFavorite = {},
                    onOpenInSettingsClicked = {},
                )
            }
        }
    }
}

@ShowkaseComposable
@ThemePreviews
@Composable
internal fun QuickAccessSettingHeaderIsNotFavoritePreview(modifier: Modifier = Modifier) {
    WithPreviewDependencies {
        ComposeLifeTheme {
            Surface(modifier) {
                QuickAccessSettingHeader(
                    isFavorite = false,
                    setIsFavorite = {},
                )
            }
        }
    }
}