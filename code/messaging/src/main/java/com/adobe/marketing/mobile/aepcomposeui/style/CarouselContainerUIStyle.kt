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

package com.adobe.marketing.mobile.aepcomposeui.style

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.adobe.marketing.mobile.aepcomposeui.AepUIConstants

class CarouselContainerUIStyle private constructor(
    val headingStyle: AepTextStyle,
    val lazyRowStyle: AepLazyRowStyle,
) {
    companion object {
        private val defaultHeadingStyle = AepTextStyle(
            textStyle = TextStyle(
                fontSize = AepUIConstants.DefaultStyle.TITLE_TEXT_SIZE.sp,
                fontWeight = AepUIConstants.DefaultStyle.TITLE_FONT_WEIGHT
            )
        )
        private val defaultListStyle = AepLazyRowStyle()
    }

    class Builder {
        private var headingStyle: AepTextStyle? = null
        private var lazyRowStyle: AepLazyRowStyle? = null

        fun headingStyle(headingStyle: AepTextStyle) = apply { this.headingStyle = headingStyle }

        fun lazyRowStyle(listStyle: AepLazyRowStyle) = apply { this.lazyRowStyle = listStyle }

        fun build() = CarouselContainerUIStyle(
            headingStyle = AepTextStyle.merge(defaultHeadingStyle, headingStyle),
            lazyRowStyle = AepLazyRowStyle.merge(defaultListStyle, lazyRowStyle)
        )
    }
}
