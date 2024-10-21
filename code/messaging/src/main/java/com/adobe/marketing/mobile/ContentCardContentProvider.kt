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

import com.adobe.marketing.mobile.aepcomposeui.contentprovider.AepUIContentProvider
import com.adobe.marketing.mobile.aepuitemplates.AepUITemplate
import com.adobe.marketing.mobile.aepuitemplates.SmallImageTemplate
import com.adobe.marketing.mobile.aepuitemplates.uimodels.AepButton
import com.adobe.marketing.mobile.aepuitemplates.uimodels.AepColor
import com.adobe.marketing.mobile.aepuitemplates.uimodels.AepDismissButton
import com.adobe.marketing.mobile.aepuitemplates.uimodels.AepFont
import com.adobe.marketing.mobile.aepuitemplates.uimodels.AepImage
import com.adobe.marketing.mobile.aepuitemplates.uimodels.AepText
import com.adobe.marketing.mobile.messaging.MessagingConstants
import com.adobe.marketing.mobile.messaging.Proposition
import com.adobe.marketing.mobile.messaging.Surface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ContentCardContentProvider(val surface: String) : AepUIContentProvider {
    private val _contentFlow = MutableStateFlow<List<AepUITemplate>>(emptyList())
    private val contentFlow: StateFlow<List<AepUITemplate>> = _contentFlow

    override suspend fun getContent(): Flow<List<AepUITemplate>> {
        // use "surface" to make getPropositionsForSurface call
        val surfaceList = listOf(Surface(surface))
        Messaging.getPropositionsForSurfaces(surfaceList) {
            _contentFlow.value = getTemplates(it.values.flatten())
        }
        return contentFlow
    }

    private fun getTemplates(propositionList: List<Proposition>): List<AepUITemplate> {
        val templateList = mutableListOf<AepUITemplate>()
        for (proposition in propositionList) {
            val item = proposition.items[0]
            val contentCardSchemaData = item.contentCardSchemaData
            val contentMap = contentCardSchemaData?.content as HashMap<String, Object>
            val smallImageTemplate = getSmallImageTemplate(item.itemId, contentMap)
            if (smallImageTemplate != null) {
                templateList.add(smallImageTemplate)
            }
        }
        return templateList
    }

    override suspend fun refreshContent() {
        TODO("Not yet implemented")
    }

    private fun getAepText(textSchemaMap: Map<String, Any>): AepText? {
        return (textSchemaMap[MessagingConstants.ContentCard.UIElement.Text.CONTENT] as? String)?.let {
            AepText(
                content = it,
                color = (textSchemaMap[MessagingConstants.ContentCard.UIElement.Text.CLR] as? Map<String, Any>)?.let { colorMap ->
                    getAepColor(colorMap)
                },
                align = textSchemaMap[MessagingConstants.ContentCard.UIElement.Text.ALIGN] as? String,
                font = (textSchemaMap[MessagingConstants.ContentCard.UIElement.Text.FONT] as? Map<String, Any>)?.let { font ->
                    getAepFont(font)
                }
            )
        }
    }

    private fun getAepFont(fontMap: Map<String, Any>): AepFont {
        return AepFont(
            name = fontMap[MessagingConstants.ContentCard.UIElement.Font.NAME] as? String,
            size = (fontMap[MessagingConstants.ContentCard.UIElement.Font.SIZE] as? Number)?.toInt(),
            weight = fontMap[MessagingConstants.ContentCard.UIElement.Font.WEIGHT] as? String,
            style =
            (fontMap[MessagingConstants.ContentCard.UIElement.Font.STYLE] as? List<*>)?.filterIsInstance<String>()
        )
    }

    private fun getAepColor(colorMap: Map<String, Any>): AepColor? {
        return (colorMap[MessagingConstants.ContentCard.UIElement.Color.LIGHT] as? String)?.let {
            AepColor(
                lightColour = it,
                darkColour = (
                    colorMap[MessagingConstants.ContentCard.UIElement.Color.DARK] as? String
                    )
            )
        }
    }

    private fun getAepButton(buttonSchemaMap: Map<String, Any>): AepButton? {
        val buttonText =
            (buttonSchemaMap[MessagingConstants.ContentCard.UIElement.Button.TEXT] as? Map<String, Any>)?.let {
                getAepText(it)
            }
        val buttonActionUrl =
            buttonSchemaMap[MessagingConstants.ContentCard.UIElement.Button.ACTION_URL] as? String
        val buttonInteractionId =
            buttonSchemaMap[MessagingConstants.ContentCard.UIElement.Button.INTERACTION_ID] as? String

        if (buttonText != null && buttonActionUrl != null && buttonInteractionId != null) {
            return AepButton(
                id = buttonInteractionId,
                text = buttonText,
                actionUrl = buttonActionUrl,
                borderWidth = (buttonSchemaMap[MessagingConstants.ContentCard.UIElement.Button.BORDER_WIDTH] as? Number)?.toFloat(),
                borderColor = (buttonSchemaMap[MessagingConstants.ContentCard.UIElement.Button.BORDER_COLOR] as? Map<String, Any>)?.let {
                    getAepColor(
                        it
                    )
                },
                backgroundColour = (buttonSchemaMap[MessagingConstants.ContentCard.UIElement.Button.BACKGROUND_COLOR] as? Map<String, Any>)?.let {
                    getAepColor(
                        it
                    )
                },
                backgroundImage = (buttonSchemaMap[MessagingConstants.ContentCard.UIElement.Button.BACKGROUND_IMAGE] as? Map<String, Any>)?.let {
                    getAepImage(
                        it
                    )
                }
            )
        }
        return null
    }

    private fun getAepImage(imageSchemaMap: Map<String, Any>): AepImage {
        return AepImage(
            url = imageSchemaMap[MessagingConstants.ContentCard.UIElement.Image.URL] as? String,
            darkUrl = imageSchemaMap[MessagingConstants.ContentCard.UIElement.Image.DARK_URL] as? String,
            bundle = imageSchemaMap[MessagingConstants.ContentCard.UIElement.Image.BUNDLE] as? String,
            darkBundle = imageSchemaMap[MessagingConstants.ContentCard.UIElement.Image.DARK_BUNDLE] as? String,
            icon = imageSchemaMap[MessagingConstants.ContentCard.UIElement.Image.ICON] as? String,
            iconSize = (imageSchemaMap[MessagingConstants.ContentCard.UIElement.Image.ICON_SIZE] as? Number)?.toFloat(),
            iconColor = (imageSchemaMap[MessagingConstants.ContentCard.UIElement.Image.ICON_COLOR] as? Map<String, Any>)?.let {
                getAepColor(
                    it
                )
            },
            alt = imageSchemaMap[MessagingConstants.ContentCard.UIElement.Image.ALTERNATE_TEXT] as? String,
            placeholder = imageSchemaMap[MessagingConstants.ContentCard.UIElement.Image.PLACEHOLDER] as? String
        )
    }

    private fun getAepDismissButton(dismissSchemaMap: Map<String, Any>): AepDismissButton {
        return AepDismissButton(
            style = dismissSchemaMap[MessagingConstants.ContentCard.UIElement.DismissButton.STYLE] as? String
        )
    }

    private fun getSmallImageTemplate(
        id: String,
        contentSchemaMap: Map<String, Any>
    ): SmallImageTemplate? {
        val title =
            getAepText(contentSchemaMap[MessagingConstants.ContentCard.SmallImageTemplate.TITLE] as Map<String, Any>)
                ?: return null
        return SmallImageTemplate(
            id = id,
            title = title,
            body = (contentSchemaMap[MessagingConstants.ContentCard.SmallImageTemplate.BODY] as? Map<String, Any>)?.let {
                getAepText(it)
            },
            image = (contentSchemaMap[MessagingConstants.ContentCard.SmallImageTemplate.IMAGE] as? Map<String, Any>)?.let {
                getAepImage(it)
            },
            actionUrl = contentSchemaMap[MessagingConstants.ContentCard.SmallImageTemplate.ACTION_URL] as? String,
            buttons = (contentSchemaMap[MessagingConstants.ContentCard.SmallImageTemplate.BUTTONS] as? List<Map<String, Any>>)?.mapNotNull {
                getAepButton(it)
            },
            dismissBtn = (contentSchemaMap[MessagingConstants.ContentCard.SmallImageTemplate.DISMISS_BUTTON] as? Map<String, Any>)?.let {
                getAepDismissButton(it)
            }
        )
    }
}
