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
@file:Suppress("MatchingDeclarationName")

package com.alexvanyo.composelife.ui.app.action.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alexvanyo.composelife.parameterizedstring.ParameterizedString
import com.alexvanyo.composelife.parameterizedstring.parameterizedStringResolver
import com.alexvanyo.composelife.parameterizedstring.parameterizedStringResource
import com.alexvanyo.composelife.preferences.CurrentShape
import com.alexvanyo.composelife.preferences.CurrentShapeType
import com.alexvanyo.composelife.preferences.di.ComposeLifePreferencesProvider
import com.alexvanyo.composelife.preferences.di.LoadedComposeLifePreferencesProvider
import com.alexvanyo.composelife.ui.app.component.DropdownOption
import com.alexvanyo.composelife.ui.app.component.EditableSlider
import com.alexvanyo.composelife.ui.app.component.IdentitySliderBijection
import com.alexvanyo.composelife.ui.app.component.TextFieldDropdown
import com.alexvanyo.composelife.ui.app.resources.CornerFractionLabel
import com.alexvanyo.composelife.ui.app.resources.CornerFractionLabelAndValue
import com.alexvanyo.composelife.ui.app.resources.CornerFractionValue
import com.alexvanyo.composelife.ui.app.resources.RoundRectangle
import com.alexvanyo.composelife.ui.app.resources.Shape
import com.alexvanyo.composelife.ui.app.resources.SizeFractionLabel
import com.alexvanyo.composelife.ui.app.resources.SizeFractionLabelAndValue
import com.alexvanyo.composelife.ui.app.resources.SizeFractionValue
import com.alexvanyo.composelife.ui.app.resources.Strings
import com.livefront.sealedenum.GenSealedEnum
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch

interface CellShapeConfigUiInjectEntryPoint :
    ComposeLifePreferencesProvider

interface CellShapeConfigUiLocalEntryPoint :
    LoadedComposeLifePreferencesProvider

context(CellShapeConfigUiInjectEntryPoint, CellShapeConfigUiLocalEntryPoint)
@Composable
fun CellShapeConfigUi(
    modifier: Modifier = Modifier,
) {
    CellShapeConfigUi(
        currentShape = preferences.currentShape,
        setCurrentShapeType = composeLifePreferences::setCurrentShapeType,
        setRoundRectangleConfig = composeLifePreferences::setRoundRectangleConfig,
        modifier = modifier,
    )
}

@Suppress("LongMethod")
@Composable
fun CellShapeConfigUi(
    currentShape: CurrentShape,
    setCurrentShapeType: suspend (CurrentShapeType) -> Unit,
    setRoundRectangleConfig: suspend ((CurrentShape.RoundRectangle) -> CurrentShape.RoundRectangle) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val coroutineScope = rememberCoroutineScope()

        TextFieldDropdown(
            label = parameterizedStringResource(Strings.Shape),
            currentValue = when (currentShape) {
                is CurrentShape.RoundRectangle -> ShapeDropdownOption.RoundRectangle
            },
            allValues = ShapeDropdownOption.values.toImmutableList(),
            setValue = { option ->
                coroutineScope.launch {
                    setCurrentShapeType(
                        when (option) {
                            ShapeDropdownOption.RoundRectangle -> CurrentShapeType.RoundRectangle
                        },
                    )
                }
            },
        )

        Spacer(modifier = Modifier.height(16.dp))

        @Suppress("USELESS_IS_CHECK")
        when (currentShape) {
            is CurrentShape.RoundRectangle -> {
                var sizeFraction by remember { mutableFloatStateOf(currentShape.sizeFraction) }
                var cornerFraction by remember { mutableFloatStateOf(currentShape.cornerFraction) }

                LaunchedEffect(sizeFraction, cornerFraction) {
                    setRoundRectangleConfig { roundRectangle ->
                        roundRectangle.copy(
                            sizeFraction = sizeFraction,
                            cornerFraction = cornerFraction,
                        )
                    }
                }

                val resolver = parameterizedStringResolver()

                EditableSlider(
                    labelAndValueText = { parameterizedStringResource(Strings.SizeFractionLabelAndValue(it)) },
                    valueText = { resolver(Strings.SizeFractionValue(it)) },
                    labelText = parameterizedStringResource(Strings.SizeFractionLabel),
                    textToValue = { it.toFloatOrNull() },
                    value = sizeFraction,
                    onValueChange = { sizeFraction = it },
                    valueRange = 0.1f..1f,
                    sliderBijection = Float.IdentitySliderBijection,
                )

                EditableSlider(
                    labelAndValueText = { parameterizedStringResource(Strings.CornerFractionLabelAndValue(it)) },
                    valueText = { resolver(Strings.CornerFractionValue(it)) },
                    labelText = parameterizedStringResource(Strings.CornerFractionLabel),
                    textToValue = { it.toFloatOrNull() },
                    value = cornerFraction,
                    onValueChange = { cornerFraction = it },
                    valueRange = 0f..0.5f,
                    sliderBijection = Float.IdentitySliderBijection,
                )
            }
        }
    }
}

sealed interface ShapeDropdownOption : DropdownOption {
    data object RoundRectangle : ShapeDropdownOption {
        override val displayText: ParameterizedString = Strings.RoundRectangle
    }

    @GenSealedEnum
    companion object
}