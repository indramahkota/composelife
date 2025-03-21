/*
 * Copyright 2025 The Android Open Source Project
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

package com.alexvanyo.composelife.network.di

import android.net.TrafficStats
import com.alexvanyo.composelife.network.EngineFactoryWithConfigBlock
import io.ktor.client.engine.okhttp.OkHttp
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo

@ContributesTo(AppScope::class)
interface EngineFactoryComponent {

    @Provides
    fun providesEngineFactoryWithConfigBlock(): EngineFactoryWithConfigBlock<*> =
        EngineFactoryWithConfigBlock(OkHttp) {
            engine {
                addInterceptor { chain ->
                    val trafficStatsTag = 0xF00D
                    try {
                        TrafficStats.setThreadStatsTag(trafficStatsTag)
                        chain.proceed(chain.request())
                    } finally {
                        TrafficStats.clearThreadStatsTag()
                    }
                }
            }
        }
}
