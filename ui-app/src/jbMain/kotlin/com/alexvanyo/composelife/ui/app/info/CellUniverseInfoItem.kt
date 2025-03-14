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

package com.alexvanyo.composelife.ui.app.info

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.triStateToggleable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp
import com.alexvanyo.composelife.ui.util.AnimatedContent
import com.alexvanyo.composelife.ui.util.AnimatedVisibility
import com.alexvanyo.composelife.ui.util.TargetState
import com.alexvanyo.composelife.ui.util.or

@Suppress("LongMethod")
@Composable
fun ColumnScope.InfoItem(
    cellUniverseInfoItemContent: CellUniverseInfoItemContent,
    editingTargetState: TargetState<Boolean, *>,
    modifier: Modifier = Modifier,
) {
    // Animate the appearance and disappearance of this item.
    AnimatedVisibility(
        targetState = editingTargetState or cellUniverseInfoItemContent.isChecked,
        contentAlignment = Alignment.Center,
        modifier = modifier,
    ) {
        Box(
            modifier = if (editingTargetState.current) {
                Modifier.triStateToggleable(
                    state = ToggleableState(cellUniverseInfoItemContent.isChecked),
                    onClick = {
                        cellUniverseInfoItemContent.isChecked = !cellUniverseInfoItemContent.isChecked
                    },
                    enabled = true,
                    role = Role.Checkbox,
                )
            } else {
                Modifier
            }
                .semantics(mergeDescendants = true) {}
                .padding(horizontal = 8.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = cellUniverseInfoItemContent.text(editingTargetState.current),
                    modifier = Modifier.weight(1f),
                )

                // The AnimatedContent allowing the text to grow.
                // The spacer represents the room needed for the checkbox, and its presence will ensure the text does
                // not overlap the checkbox.
                // The animated spacer's absence will allow the text to expand as needed within the row.
                AnimatedContent(
                    targetState = editingTargetState,
                    contentAlignment = Alignment.Center,
                ) { targetIsEditing ->
                    if (targetIsEditing) {
                        Spacer(modifier = Modifier.size(48.dp))
                    }
                }
            }

            // The AnimatedContent for the checkbox.
            // This content remains a constant width, allowing for more graceful animations without horizontal
            // movement
            AnimatedContent(
                targetState = editingTargetState,
                contentAlignment = Alignment.Center,
                modifier = Modifier.align(Alignment.CenterEnd),
            ) { targetIsEditing ->
                if (targetIsEditing) {
                    Checkbox(
                        checked = cellUniverseInfoItemContent.isChecked,
                        onCheckedChange = null,
                        modifier = Modifier.size(48.dp),
                    )
                } else {
                    Spacer(modifier = Modifier.width(48.dp))
                }
            }
        }
    }
}
