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
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.adobe.marketing.mobile.aepcomposeui.style.AepIconStyle
import com.adobe.marketing.mobile.messaging.R

/**
 * A composable function that displays an icon element with customizable properties.
 *
 * @param iconName The name of icon to be displayed.
 * @param style The [AepIconStyle] to be applied to the icon element.
 * @param onClick Method that is called when this icon is clicked
 */
@Composable
internal fun AepIconComposable(
    iconName: String,
    style: AepIconStyle = AepIconStyle(),
    onClick: () -> Unit = {}
) {
    val drawable = getIconDrawable(iconName)
    drawable?.let {
        Icon(
            painter = painterResource(id = drawable),
            contentDescription = style.contentDescription ?: "",
            modifier = (style.modifier ?: Modifier).clickable { onClick() }
        )
    }
}

private fun getIconDrawable(iconName: String): Int? {
    return when (iconName) {
        "dismiss_simple" -> R.drawable.dismiss_simple
        "dismiss_filled" -> R.drawable.dismiss_filled
        else -> return null
    }
}
