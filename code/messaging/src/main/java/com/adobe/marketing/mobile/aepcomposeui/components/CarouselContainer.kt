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

import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.OutlinedTextFieldDefaults.contentPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.adobe.marketing.mobile.aepcomposeui.CarouselContainerUI
import com.adobe.marketing.mobile.aepcomposeui.ContainerEvent
import com.adobe.marketing.mobile.aepcomposeui.observers.AepContainerUIEventObserver
import com.adobe.marketing.mobile.aepcomposeui.style.CarouselContainerUIStyle
import com.adobe.marketing.mobile.aepcomposeui.style.ContentCardsStyle

@Composable
internal fun CarouselContainer(
    ui: CarouselContainerUI,
    carouselContainerStyle: CarouselContainerUIStyle,
    cardsStyle: ContentCardsStyle,
    observer: AepContainerUIEventObserver?
) {
    LaunchedEffect(ui) {
        observer?.onContainerEvent(ContainerEvent.Refreshed(ui))
    }
    val carouselContainerSettings = ui.getAepContainerTemplate()
    val uiList = ui.getAepContainerState().aepUIList.take(carouselContainerSettings.capacity)

    if (!uiList.isEmpty()) {
    Column {
        // Wrap AepText in an invisible Surface to provide Material Theme context
        Surface(
            color = Color.Transparent
        ) {
            AepText(
                model = carouselContainerSettings.heading,
                textStyle = carouselContainerStyle.headingStyle
            )
        }

        val lazyRowStyle = carouselContainerStyle.lazyRowStyle
        val modifier = lazyRowStyle.modifier ?: Modifier
        val contentPadding = lazyRowStyle.contentPadding ?: PaddingValues(0.dp)
        val reverseLayout = lazyRowStyle.reverseLayout ?: false
        val flingBehavior = lazyRowStyle.flingBehavior ?: ScrollableDefaults.flingBehavior()
        val userScrollEnabled = lazyRowStyle.userScrollEnabled ?: true

            LazyRow(
                modifier = modifier,
                contentPadding = contentPadding,
                reverseLayout = reverseLayout,
                flingBehavior = flingBehavior,
                userScrollEnabled = userScrollEnabled,
                horizontalArrangement = lazyRowStyle.horizontalArrangement
                    ?: getDefaultHorizontalArrangement(reverseLayout),
                verticalAlignment = lazyRowStyle.verticalAlignment ?: Alignment.Top
            ) {
                renderListItems(
                    uiList = uiList,
                    cardsStyle = cardsStyle,
                    observer = observer?.aepUIEventObserver
                )
            }
        }
    }
}

private fun getDefaultHorizontalArrangement(reverseLayout: Boolean): Arrangement.Horizontal =
    if (reverseLayout) Arrangement.End else Arrangement.Start