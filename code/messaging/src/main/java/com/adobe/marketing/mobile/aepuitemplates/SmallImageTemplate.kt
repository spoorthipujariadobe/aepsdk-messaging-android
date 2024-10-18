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

package com.adobe.marketing.mobile.aepuitemplates

import com.adobe.marketing.mobile.aepcomposeui.aepui.components.AEPButton
import com.adobe.marketing.mobile.aepcomposeui.aepui.components.AEPDismissButton
import com.adobe.marketing.mobile.aepcomposeui.aepui.components.AEPImage
import com.adobe.marketing.mobile.aepcomposeui.aepui.components.AEPText
import com.adobe.marketing.mobile.aepuitemplates.uimodels.AepButton
import com.adobe.marketing.mobile.aepuitemplates.uimodels.AepDismissButton
import com.adobe.marketing.mobile.aepuitemplates.uimodels.AepImage
import com.adobe.marketing.mobile.aepuitemplates.uimodels.AepText
import com.adobe.marketing.mobile.aepuitemplates.utils.AepUITemplateType
import com.adobe.marketing.mobile.messaging.UiTemplateConstructionFailedException
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Class representing a small image template, which implements the [AepUITemplate] interface.
 *
 * @property title The title text and display settings.
 * @property body The body text and display settings.
 * @property image The details of the image to be displayed.
 * @property actionUrl If provided, interacting with this card will result in the opening of the actionUrl.
 * @property buttons The details for the small image template buttons.
 * @property dismissBtn The details for the small image template dismiss button.
 */
@Serializable
data class SmallImageTemplate(
    val id: String,
    val title: AepText,
    val body: AepText? = null,
    val image: AepImage? = null,
    val actionUrl: String? = null,
    val buttons: List<AepButton>? = null,
    val dismissBtn: AepDismissButton? = null
) : AepUITemplate {

    companion object {
        @JvmStatic
        fun fromJsonString(jsonString: String): SmallImageTemplate {
            val json = Json { ignoreUnknownKeys = true }
            val smallImageTemplate: SmallImageTemplate?
            try {
                smallImageTemplate = json.decodeFromString<SmallImageTemplate>(jsonString)
            } catch (e: Exception) {
                throw UiTemplateConstructionFailedException("Failed to create a small image template from the provided JSON string: ${e.localizedMessage}")
            }
            return smallImageTemplate
        }
    }

    /**
     * Returns the type of this template, which is [AepUITemplateType.SMALL_IMAGE].
     *
     * @return A string representing the type of the template.
     */
    override fun getType() = AepUITemplateType.SMALL_IMAGE
}
