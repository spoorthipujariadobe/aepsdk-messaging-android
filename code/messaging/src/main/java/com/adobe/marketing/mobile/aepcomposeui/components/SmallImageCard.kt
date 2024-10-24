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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.adobe.marketing.mobile.aepcomposeui.SmallImageUI
import com.adobe.marketing.mobile.aepcomposeui.interactions.UIEvent
import com.adobe.marketing.mobile.aepcomposeui.observers.AepUIEventObserver
import com.adobe.marketing.mobile.aepcomposeui.style.SmallImageUIStyle
import com.adobe.marketing.mobile.aepcomposeui.utils.UIAction

/**
 * Composable function that renders a small image card UI.
 *
 * @param ui The small image AEP UI to be rendered.
 * @param style The style to be applied to the small image UI.
 * @param observer An optional observer that listens to UI events.
 */
@Composable
fun SmallImageCard(
    ui: SmallImageUI,
    style: SmallImageUIStyle,
    observer: AepUIEventObserver?,
) {

    // TODO - Add id for LaunchedEffect, test if it is working
    LaunchedEffect(key1 = Unit) {
        observer?.onEvent(UIEvent.Display(ui))
    }

    // TODO - Add id for DisposableEffect, test if it is working
    DisposableEffect(key1 = Unit) {
        onDispose {
            observer?.onEvent(UIEvent.Dismiss(ui))
        }
    }

    Card(
        modifier = Modifier
            .clickable {
                observer?.onEvent(UIEvent.Interact(ui, UIAction.Click(null, ui.getTemplate().actionUrl)))
            }
    ) {
        Row {
            // TODO - Add image support
            Column {
                ui.getTemplate().title.let {
                    AepTextComposable(
                        it,
                        style.defaultTitleTextStyle,
                        style.titleAepTextStyle
                    )
                }
                ui.getTemplate().body?.let {
                    AepTextComposable(
                        it,
                        defaultStyle = style.defaultBodyTextStyle,
                        overridingStyle = style.bodyAepTextStyle
                    )
                }
                Row {
                    ui.getTemplate().buttons?.forEachIndexed { index, button ->
                        AepButtonComposable(
                            button,
                            onClick = {
                                observer?.onEvent(UIEvent.Interact(ui, UIAction.Click(button.id, button.actionUrl)))
                            },
                            overridingButtonStyle = style.buttonStyle[index]?.first,
                            defaultButtonTextStyle = style.defaultButtonTextStyle,
                            overridingButtonTextStyle = style.buttonStyle[index]?.second
                        )
                    }
                }
            }
        }
    }
}
