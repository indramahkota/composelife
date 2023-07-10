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

package com.alexvanyo.composelife.ui.app.cells

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alexvanyo.composelife.preferences.CurrentShape
import com.alexvanyo.composelife.ui.app.entrypoints.WithPreviewDependencies
import com.alexvanyo.composelife.ui.app.theme.ComposeLifeTheme
import com.alexvanyo.composelife.ui.util.ThemePreviews

@ThemePreviews
@Composable
fun AliveCellPreview() {
    WithPreviewDependencies {
        ComposeLifeTheme {
            InteractableCell(
                modifier = Modifier.size(50.dp),
                drawState = DrawState.Alive,
                shape = CurrentShape.RoundRectangle(
                    sizeFraction = 1f,
                    cornerFraction = 0f,
                ),
                contentDescription = "test cell",
                onValueChange = {},
            )
        }
    }
}

@ThemePreviews
@Composable
fun PendingAliveCellPreview() {
    WithPreviewDependencies {
        ComposeLifeTheme {
            InteractableCell(
                modifier = Modifier.size(50.dp),
                drawState = DrawState.PendingAlive,
                shape = CurrentShape.RoundRectangle(
                    sizeFraction = 1f,
                    cornerFraction = 0f,
                ),
                contentDescription = "test cell",
                onValueChange = {},
            )
        }
    }
}

@ThemePreviews
@Composable
fun DeadCellPreview() {
    WithPreviewDependencies {
        ComposeLifeTheme {
            InteractableCell(
                modifier = Modifier.size(50.dp),
                drawState = DrawState.Dead,
                shape = CurrentShape.RoundRectangle(
                    sizeFraction = 1f,
                    cornerFraction = 0f,
                ),
                contentDescription = "test cell",
                onValueChange = {},
            )
        }
    }
}

@ThemePreviews
@Composable
fun PendingDeadCellPreview() {
    WithPreviewDependencies {
        ComposeLifeTheme {
            InteractableCell(
                modifier = Modifier.size(50.dp),
                drawState = DrawState.PendingDead,
                shape = CurrentShape.RoundRectangle(
                    sizeFraction = 1f,
                    cornerFraction = 0f,
                ),
                contentDescription = "test cell",
                onValueChange = {},
            )
        }
    }
}
