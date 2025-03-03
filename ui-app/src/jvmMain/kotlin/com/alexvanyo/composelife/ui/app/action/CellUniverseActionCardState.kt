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

package com.alexvanyo.composelife.ui.app.action

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.alexvanyo.composelife.navigation.BackstackEntry
import com.alexvanyo.composelife.navigation.BackstackMap
import com.alexvanyo.composelife.navigation.BackstackState
import com.alexvanyo.composelife.navigation.canNavigateBack
import com.alexvanyo.composelife.navigation.popBackstack
import com.alexvanyo.composelife.navigation.rememberMutableBackstackNavigationController
import com.alexvanyo.composelife.navigation.withExpectedActor
import com.alexvanyo.composelife.ui.settings.InlineSettingsPaneInjectEntryPoint
import com.alexvanyo.composelife.ui.settings.InlineSettingsPaneLocalEntryPoint
import com.alexvanyo.composelife.ui.util.RepeatablePredictiveBackHandler
import com.alexvanyo.composelife.ui.util.RepeatablePredictiveBackState
import com.alexvanyo.composelife.ui.util.TargetState
import com.alexvanyo.composelife.ui.util.rememberRepeatablePredictiveBackStateHolder
import kotlin.uuid.Uuid

interface CellUniverseActionCardInjectEntryPoint :
    InlineEditPaneInjectEntryPoint,
    InlineSettingsPaneInjectEntryPoint

interface CellUniverseActionCardLocalEntryPoint :
    InlineEditPaneLocalEntryPoint,
    InlineSettingsPaneLocalEntryPoint

/**
 * The persistable state describing the [CellUniverseActionCard].
 */
interface CellUniverseActionCardState {

    /**
     * Sets if the card is expanded.
     */
    fun setIsExpanded(isExpanded: Boolean)

    /**
     * The target state for whether the card is expanded.
     */
    val expandedTargetState: TargetState<Boolean, *>

    /**
     * The inline navigation state of the card.
     */
    val inlineNavigationState: BackstackState<InlineActionCardNavigation>

    /**
     * The [RepeatablePredictiveBackState] for the inline navigation of the [CellUniverseActionCard].
     */
    val inlineRepeatablePredictiveBackState: RepeatablePredictiveBackState

    /**
     * `true` if the card can navigate back.
     */
    val canNavigateBack: Boolean

    fun onSpeedClicked(actorBackstackEntryId: Uuid? = null)

    fun onEditClicked(actorBackstackEntryId: Uuid? = null)

    fun onSettingsClicked(actorBackstackEntryId: Uuid? = null)

    fun inlineOnBackPressed(inlineActorBackstackEntryId: Uuid? = null)
}

/**
 * Remembers the a default implementation of [CellUniverseActionCardState].
 */
@Suppress("LongMethod", "CyclomaticComplexMethod")
@Composable
fun rememberCellUniverseActionCardState(
    enableBackHandler: Boolean,
    setIsExpanded: (Boolean) -> Unit,
    expandedTargetState: TargetState<Boolean, *>,
): CellUniverseActionCardState {
    var currentInlineBackstack: InlineActionCardBackstack by rememberSaveable(
        stateSaver = InlineActionCardBackstack.Saver,
    ) {
        mutableStateOf(InlineActionCardBackstack.Speed)
    }

    val speedNavController = rememberMutableBackstackNavigationController(
        initialBackstackEntries = listOf(
            BackstackEntry(
                value = InlineActionCardNavigation.Speed,
                previous = null,
            ),
        ),
        backstackValueSaverFactory = InlineActionCardNavigation.SaverFactory,
    )

    val editNavController = rememberMutableBackstackNavigationController(
        initialBackstackEntries = listOf(
            BackstackEntry(
                value = InlineActionCardNavigation.Edit,
                previous = null,
            ),
        ),
        backstackValueSaverFactory = InlineActionCardNavigation.SaverFactory,
    )

    val settingsNavController = rememberMutableBackstackNavigationController(
        initialBackstackEntries = listOf(
            BackstackEntry(
                value = InlineActionCardNavigation.Settings,
                previous = null,
            ),
        ),
        backstackValueSaverFactory = InlineActionCardNavigation.SaverFactory,
    )

    val currentInlineNavController by remember {
        derivedStateOf {
            when (currentInlineBackstack) {
                InlineActionCardBackstack.Speed -> speedNavController
                InlineActionCardBackstack.Edit -> editNavController
                InlineActionCardBackstack.Settings -> settingsNavController
            }
        }
    }

    val canNavigateBack by remember {
        derivedStateOf {
            currentInlineNavController.canNavigateBack || when (currentInlineBackstack) {
                InlineActionCardBackstack.Speed -> false
                InlineActionCardBackstack.Edit,
                InlineActionCardBackstack.Settings,
                -> true
            }
        }
    }

    val inlineNavigationState = remember {
        object : BackstackState<InlineActionCardNavigation> {
            override val entryMap: BackstackMap<InlineActionCardNavigation>
                get() = speedNavController.entryMap +
                    editNavController.entryMap +
                    settingsNavController.entryMap

            override val currentEntryId: Uuid
                get() = currentInlineNavController.currentEntryId

            override val previousEntryId: Uuid?
                get() = if (currentInlineNavController.canNavigateBack) {
                    currentInlineNavController.previousEntryId
                } else {
                    when (currentInlineBackstack) {
                        InlineActionCardBackstack.Speed -> null
                        InlineActionCardBackstack.Edit,
                        InlineActionCardBackstack.Settings,
                        -> speedNavController.currentEntryId
                    }
                }
        }
    }

    val onBackPressed = { inlineActorBackstackEntryId: Uuid? ->
        currentInlineNavController.withExpectedActor(inlineActorBackstackEntryId) {
            if (currentInlineNavController.canNavigateBack) {
                currentInlineNavController.popBackstack()
            } else {
                currentInlineBackstack = InlineActionCardBackstack.Speed
            }
        }
    }

    val inlinePredictiveBackStateHolder = rememberRepeatablePredictiveBackStateHolder()
    RepeatablePredictiveBackHandler(
        repeatablePredictiveBackStateHolder = inlinePredictiveBackStateHolder,
        enabled = enableBackHandler && expandedTargetState.current && canNavigateBack,
    ) {
        onBackPressed(inlineNavigationState.currentEntryId)
    }

    return object : CellUniverseActionCardState {

        override fun setIsExpanded(isExpanded: Boolean) {
            setIsExpanded(isExpanded)
        }

        override val expandedTargetState: TargetState<Boolean, *>
            get() = expandedTargetState

        override val inlineNavigationState get() = inlineNavigationState

        override val inlineRepeatablePredictiveBackState get() = inlinePredictiveBackStateHolder.value

        override val canNavigateBack: Boolean get() = canNavigateBack

        override fun onSpeedClicked(actorBackstackEntryId: Uuid?) {
            currentInlineNavController.withExpectedActor(actorBackstackEntryId) {
                currentInlineBackstack = InlineActionCardBackstack.Speed
            }
        }

        override fun onEditClicked(actorBackstackEntryId: Uuid?) {
            currentInlineNavController.withExpectedActor(actorBackstackEntryId) {
                currentInlineBackstack = InlineActionCardBackstack.Edit
            }
        }

        override fun onSettingsClicked(actorBackstackEntryId: Uuid?) {
            currentInlineNavController.withExpectedActor(actorBackstackEntryId) {
                currentInlineBackstack = InlineActionCardBackstack.Settings
            }
        }

        override fun inlineOnBackPressed(inlineActorBackstackEntryId: Uuid?) {
            inlineOnBackPressed(inlineActorBackstackEntryId)
        }
    }
}
