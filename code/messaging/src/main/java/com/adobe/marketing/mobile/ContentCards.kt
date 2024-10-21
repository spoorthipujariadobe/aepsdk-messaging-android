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

package com.adobe.marketing.mobile

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adobe.marketing.mobile.aepcomposeui.aepui.AepComposableViewModelFactory
import com.adobe.marketing.mobile.aepcomposeui.aepui.AepList
import com.adobe.marketing.mobile.aepcomposeui.aepui.AepListViewModel
import com.adobe.marketing.mobile.aepcomposeui.aepui.style.AepUIStyle
import com.adobe.marketing.mobile.aepcomposeui.aepui.style.SmallImageUIStyle

/**
 * Public composable exposing the ContentCards UI, utilizing the AEP List component
 */
@Composable
fun ContentCards(
    viewModel: AepListViewModel,
    contentCardStyle: ContentCardStyle = ContentCardStyle(),
    container: @Composable (@Composable () -> Unit) -> Unit = { content ->
        // Default to a Column if no container is provided
        LazyColumn {
            item {
                content()
            }
        }
    },
    contentCardCallback: ContentCardCallback? = null
) {
    AepList(
        viewModel = viewModel,
        aepUiEventObserver = MessagingUiEventObserver(contentCardCallback),
        aepUiStyle = AepUIStyle(
            smallImageUiStyle = contentCardStyle.smallImageAepUiStyle,
        ),
        container = container,
    )
}

// Wrapper class that only accepts style for UIs supported by ContentCards
class ContentCardStyle(
    val smallImageAepUiStyle: SmallImageUIStyle = SmallImageUIStyle(),
)

@Composable
fun ContentCardsViewModel(surface: String): AepListViewModel {
    return viewModel(
        factory = AepComposableViewModelFactory(ContentCardContentProvider(surface)),
        key = surface
    )
}
