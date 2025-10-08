/*
  Copyright 2025 Adobe. All rights reserved.
  This file is licensed to you under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0
  Unless required by applicable law or agreed to in writing, software distributed under
  the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR REPRESENTATIONS
  OF ANY KIND, either express or implied. See the License for the specific language
  governing permissions and limitations under the License.
*/

package com.adobe.marketing.mobile.aepcomposeui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.adobe.marketing.mobile.aepcomposeui.AepContainerUI
import com.adobe.marketing.mobile.aepcomposeui.CarouselContainerUI
import com.adobe.marketing.mobile.aepcomposeui.InboxContainerUI
import com.adobe.marketing.mobile.aepcomposeui.contentprovider.AepContainerUIContentProvider
import com.adobe.marketing.mobile.aepcomposeui.contentprovider.AepUIContentProvider
import com.adobe.marketing.mobile.aepcomposeui.state.CarouselContainerUIState
import com.adobe.marketing.mobile.aepcomposeui.state.InboxContainerUIState
import com.adobe.marketing.mobile.aepcomposeui.uimodels.CarouselContainerUITemplate
import com.adobe.marketing.mobile.aepcomposeui.uimodels.InboxContainerUITemplate
import com.adobe.marketing.mobile.messaging.ContentCardUIProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.jvm.java

sealed interface AepContainerState {
    object Loading : AepContainerState
    data class Success(val containerUI: AepContainerUI<*, *>) : AepContainerState
    data class Error(val error: Throwable) : AepContainerState
}

class AepContainerRepository(
    private val aepUIProvider: AepUIContentProvider,
    private val aepContainerUIProvider: AepContainerUIContentProvider
): ViewModel(){
    private val _containerUiState = MutableStateFlow<AepContainerState>(AepContainerState.Loading)
    val containerUiState: StateFlow<AepContainerState> = _containerUiState.asStateFlow()

    init {
        viewModelScope.launch {
            aepUIProvider.getContentCardFlow().collect { contentCardResult ->
                aepContainerUIProvider.getContainerUI().collect { containerResult ->
                    val state = when (containerResult) {
                        is InboxContainerUITemplate -> {
                            AepContainerState.Success(
                                InboxContainerUI(
                                    containerResult,
                                    InboxContainerUIState(
                                        aepUIList = contentCardResult
                                    )
                                )
                            )
                        }

                        is CarouselContainerUITemplate -> {
                            AepContainerState.Success(
                                CarouselContainerUI(
                                    containerResult,
                                    state = CarouselContainerUIState(
                                        contentCardResult
                                    )
                                )
                            )
                        }
                    }
                    _containerUiState.update { state }
                }
            }
        }
    }
}


class AepContainerViewModelFactory(
    private val aepUIProvider: AepUIContentProvider,
    private val aepContainerUIProvider: AepContainerUIContentProvider
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AepContainerRepository::class.java) -> {
                AepContainerRepository(aepUIProvider, aepContainerUIProvider ) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}