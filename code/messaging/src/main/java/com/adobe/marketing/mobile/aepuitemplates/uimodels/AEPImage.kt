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

package com.adobe.marketing.mobile.aepuitemplates.uimodels

import com.adobe.marketing.mobile.aepuitemplates.utils.Constants

/**
 * Data class representing an image element in the UI.
 *
 * @property url The URL of the image.
 * @property darkUrl The URL of the image for dark mode.
 * @property bundle The resource bundle for the image.
 * @property darkBundle The resource bundle for the image in dark mode.
 * @property icon The icon name or identifier.
 * @property iconSize The size of the icon.
 *
 * @param imageMap A map containing key-value pairs to initialize the AEPImage properties.
 */
data class AEPImage(
    val url: String? = null,
    val darkUrl: String? = null,
    val bundle: String? = null,
    val darkBundle: String? = null,
    val icon: String? = null,
    val iconSize: Int? = null
) {
    constructor(imageMap: Map<String, Any>) : this(
        url = imageMap[Constants.CardTemplate.UIElement.Image.URL] as? String,
        darkUrl = imageMap[Constants.CardTemplate.UIElement.Image.DARK_URL] as? String,
        bundle = imageMap[Constants.CardTemplate.UIElement.Image.BUNDLE] as? String,
        darkBundle = imageMap[Constants.CardTemplate.UIElement.Image.DARK_BUNDLE] as? String,
        icon = imageMap[Constants.CardTemplate.UIElement.Image.ICON] as? String,
        iconSize = (imageMap[Constants.CardTemplate.UIElement.Image.ICON_SIZE] as? Number)?.toInt()
    )
}