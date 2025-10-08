/*
  Copyright 2024 Adobe. All rights reserved.
  This file is licensed to you under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0
  Unless required by applicable law or agreed to in writing, software distributed under
  the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR REPRESENTATIONS
  OF ANY KIND, either express or implied. See the License for the specific language
  governing permissions and limitations under the License.
*/

package com.adobe.marketing.mobile.aepcomposeui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.adobe.marketing.mobile.aepcomposeui.CarouselContainerUI
import com.adobe.marketing.mobile.aepcomposeui.InboxContainerUI
import com.adobe.marketing.mobile.aepcomposeui.style.ContainerStyle
import com.adobe.marketing.mobile.aepcomposeui.style.ContentCardsStyle
import com.adobe.marketing.mobile.aepcomposeui.viewmodel.AepContainerState
import com.adobe.marketing.mobile.messaging.ContentCardContainerUIEventObserver
import com.adobe.marketing.mobile.messaging.ContentCardUIEventListener

@Composable
fun AepContainer(
    containerUiState: AepContainerState,
    containerStyle: ContainerStyle = ContainerStyle(),
    cardsStyle: ContentCardsStyle = ContentCardsStyle(),
    cardUIEventListener: ContentCardUIEventListener? = null
) {
    when (containerUiState) {
        is AepContainerState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(30.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is AepContainerState.Success -> {
            val ui = containerUiState.containerUI
            when (ui) {
                is InboxContainerUI -> {
                    InboxContainer(
                        ui = ui,
                        inboxContainerStyle = containerStyle.inboxContainerUIStyle,
                        cardsStyle = cardsStyle,
                        observer = ContentCardContainerUIEventObserver(cardUIEventListener)
                    )
                }
                is CarouselContainerUI -> {
                    CarouselContainer(
                        ui = ui,
                        carouselContainerStyle = containerStyle.carouselContainerUIStyle,
                        cardsStyle = cardsStyle,
                        observer = ContentCardContainerUIEventObserver(cardUIEventListener)
                    )
                }
            }
        }
        is AepContainerState.Error -> {
            // do nothing for error state
        }
    }
}
