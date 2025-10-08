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
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.adobe.marketing.mobile.aepcomposeui.ContainerEvent
import com.adobe.marketing.mobile.aepcomposeui.InboxContainerUI
import com.adobe.marketing.mobile.aepcomposeui.observers.AepContainerUIEventObserver
import com.adobe.marketing.mobile.aepcomposeui.style.AepCardStyle
import com.adobe.marketing.mobile.aepcomposeui.style.ContentCardsStyle
import com.adobe.marketing.mobile.aepcomposeui.style.ImageOnlyUIStyle
import com.adobe.marketing.mobile.aepcomposeui.style.InboxContainerUIStyle
import com.adobe.marketing.mobile.aepcomposeui.style.LargeImageUIStyle
import com.adobe.marketing.mobile.aepcomposeui.style.SmallImageUIStyle

@Composable
internal fun InboxContainer(
    ui: InboxContainerUI,
    inboxContainerStyle: InboxContainerUIStyle,
    cardsStyle: ContentCardsStyle,
    observer: AepContainerUIEventObserver?
) {
    LaunchedEffect(ui) {
        observer?.onContainerEvent(ContainerEvent.Refreshed(ui))
    }
    val inboxContainerSettings = ui.getAepContainerTemplate()
    val uiList = ui.getAepContainerState().aepUIList.take(inboxContainerSettings.capacity)

    val unreadCardColor = if (isSystemInDarkTheme()) {
        inboxContainerStyle.unreadBgColor?.darkColor
            ?: inboxContainerSettings.unreadBgColor?.darkColor
    } else {
        inboxContainerStyle.unreadBgColor?.lightColor
            ?: inboxContainerSettings.unreadBgColor?.lightColor
    }

    Column {
        // Wrap AepText in an invisible Surface to provide Material Theme context
        Surface(
            color = Color.Transparent
        ) {
            AepText(
                model = inboxContainerSettings.heading,
                textStyle = inboxContainerStyle.headingStyle
            )
        }

        // Create unread style variants once if unread color is specified
        val unreadCardsStyle = createUnreadCardsStyle(cardsStyle, unreadCardColor)

        val lazyColumnStyle = inboxContainerStyle.lazyColumnStyle
        val modifier = lazyColumnStyle.modifier ?: Modifier
        val contentPadding = lazyColumnStyle.contentPadding ?: PaddingValues(0.dp)
        val reverseLayout = lazyColumnStyle.reverseLayout ?: false
        val flingBehavior = lazyColumnStyle.flingBehavior ?: ScrollableDefaults.flingBehavior()
        val userScrollEnabled = lazyColumnStyle.userScrollEnabled ?: true

        if (uiList.isEmpty() &&
            (inboxContainerSettings.emptyMessage != null || inboxContainerSettings.emptyImage != null)
        ) {
            // Wrap AepText in an invisible Surface to provide Material Theme context
            Surface(
                color = Color.Transparent,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    inboxContainerSettings.emptyMessage?.let {
                        AepText(
                            model = it,
                            textStyle = inboxContainerStyle.emptyMessageStyle
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    inboxContainerSettings.emptyImage?.let {
                        AepAsyncImage(
                            image = it,
                            imageStyle = inboxContainerStyle.emptyImageStyle
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = modifier,
                contentPadding = contentPadding,
                reverseLayout = reverseLayout,
                flingBehavior = flingBehavior,
                userScrollEnabled = userScrollEnabled,
                verticalArrangement = lazyColumnStyle.verticalArrangement
                    ?: getDefaultVerticalArrangement(reverseLayout),
                horizontalAlignment = lazyColumnStyle.horizontalAlignment ?: Alignment.Start
            ) {
                renderListItems(
                    uiList = uiList,
                    cardsStyle = cardsStyle,
                    unreadCardsStyle = unreadCardsStyle,
                    unreadIcon = if (inboxContainerSettings.unreadIcon != null) Triple(
                        inboxContainerSettings.unreadIcon,
                        inboxContainerStyle.unreadIconStyle,
                        inboxContainerStyle.unreadIconAlignment
                            ?: inboxContainerSettings.unreadIconAlignment ?: Alignment.TopStart
                    )
                    else null,
                    observer = observer?.aepUIEventObserver
                )
            }
        }
    }
}

private fun getDefaultVerticalArrangement(reverseLayout: Boolean): Arrangement.Vertical =
    if (reverseLayout) Arrangement.Bottom else Arrangement.Top

/**
 * Creates unread card styles with the specified unread color, or returns the original styles if no unread color is provided.
 */
@Composable
private fun createUnreadCardsStyle(cardsStyle: ContentCardsStyle, unreadCardColor: Color?): ContentCardsStyle {
    if (unreadCardColor == null) {
        return cardsStyle
    }

    val unreadSmallImageStyle = createUnreadSmallImageStyle(cardsStyle.smallImageUIStyle, unreadCardColor)
    val unreadLargeImageStyle = createUnreadLargeImageStyle(cardsStyle.largeImageUIStyle, unreadCardColor)
    val unreadImageOnlyStyle = createUnreadImageOnlyStyle(cardsStyle.imageOnlyUIStyle, unreadCardColor)

    return ContentCardsStyle(
        smallImageUIStyle = unreadSmallImageStyle,
        largeImageUIStyle = unreadLargeImageStyle,
        imageOnlyUIStyle = unreadImageOnlyStyle
    )
}

@Composable
private fun createUnreadSmallImageStyle(originalStyle: SmallImageUIStyle, unreadCardColor: Color): SmallImageUIStyle {
    val unreadCardStyle = AepCardStyle(
        modifier = originalStyle.cardStyle.modifier,
        enabled = originalStyle.cardStyle.enabled,
        shape = originalStyle.cardStyle.shape,
        colors = CardDefaults.cardColors(unreadCardColor),
        elevation = originalStyle.cardStyle.elevation,
        border = originalStyle.cardStyle.border
    )
    return SmallImageUIStyle.Builder()
        .cardStyle(unreadCardStyle)
        .imageStyle(originalStyle.imageStyle)
        .rootRowStyle(originalStyle.rootRowStyle)
        .textColumnStyle(originalStyle.textColumnStyle)
        .titleAepTextStyle(originalStyle.titleTextStyle)
        .bodyAepTextStyle(originalStyle.bodyTextStyle)
        .buttonRowStyle(originalStyle.buttonRowStyle)
        .buttonStyle(originalStyle.buttonStyle.map { it }.toTypedArray())
        .dismissButtonStyle(originalStyle.dismissButtonStyle)
        .dismissButtonAlignment(originalStyle.dismissButtonAlignment)
        .build()
}

@Composable
private fun createUnreadLargeImageStyle(originalStyle: LargeImageUIStyle, unreadCardColor: Color): LargeImageUIStyle {
    val unreadCardStyle = AepCardStyle(
        modifier = originalStyle.cardStyle.modifier,
        enabled = originalStyle.cardStyle.enabled,
        shape = originalStyle.cardStyle.shape,
        colors = CardDefaults.cardColors(unreadCardColor),
        elevation = originalStyle.cardStyle.elevation,
        border = originalStyle.cardStyle.border
    )
    return LargeImageUIStyle.Builder()
        .cardStyle(unreadCardStyle)
        .imageStyle(originalStyle.imageStyle)
        .rootColumnStyle(originalStyle.rootColumnStyle)
        .textColumnStyle(originalStyle.textColumnStyle)
        .titleAepTextStyle(originalStyle.titleTextStyle)
        .bodyAepTextStyle(originalStyle.bodyTextStyle)
        .buttonRowStyle(originalStyle.buttonRowStyle)
        .buttonStyle(originalStyle.buttonStyle.map { it }.toTypedArray())
        .dismissButtonStyle(originalStyle.dismissButtonStyle)
        .dismissButtonAlignment(originalStyle.dismissButtonAlignment)
        .build()
}

@Composable
private fun createUnreadImageOnlyStyle(originalStyle: ImageOnlyUIStyle, unreadCardColor: Color): ImageOnlyUIStyle {
    val unreadCardStyle = AepCardStyle(
        modifier = originalStyle.cardStyle.modifier,
        enabled = originalStyle.cardStyle.enabled,
        shape = originalStyle.cardStyle.shape,
        colors = CardDefaults.cardColors(unreadCardColor),
        elevation = originalStyle.cardStyle.elevation,
        border = originalStyle.cardStyle.border
    )
    return ImageOnlyUIStyle.Builder()
        .cardStyle(unreadCardStyle)
        .imageStyle(originalStyle.imageStyle)
        .dismissButtonStyle(originalStyle.dismissButtonStyle)
        .dismissButtonAlignment(originalStyle.dismissButtonAlignment)
        .build()
}
