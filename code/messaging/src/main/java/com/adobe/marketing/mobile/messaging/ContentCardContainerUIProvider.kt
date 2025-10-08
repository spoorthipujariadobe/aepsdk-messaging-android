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

package com.adobe.marketing.mobile.messaging

import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import com.adobe.marketing.mobile.aepcomposeui.components.CarouselContainer
import com.adobe.marketing.mobile.aepcomposeui.contentprovider.AepContainerUIContentProvider
import com.adobe.marketing.mobile.aepcomposeui.uimodels.AepColor
import com.adobe.marketing.mobile.aepcomposeui.uimodels.AepContainerUITemplate
import com.adobe.marketing.mobile.aepcomposeui.uimodels.AepImage
import com.adobe.marketing.mobile.aepcomposeui.uimodels.AepText
import com.adobe.marketing.mobile.aepcomposeui.uimodels.CarouselContainerUITemplate
import com.adobe.marketing.mobile.aepcomposeui.uimodels.InboxContainerUITemplate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow

class ContentCardContainerUIProvider(val surface: Surface) : AepContainerUIContentProvider {

    override suspend fun getContainerUI(): Flow<AepContainerUITemplate> = flow {
        emit(getContainer())
    }

    override suspend fun refreshContainerUI() {
        getContainer()
    }

    private fun getContainer(): AepContainerUITemplate {
//            CarouselContainerUITemplate(
//                heading = AepText("Message Inbox"),
//                capacity = 5,
//            )
        return InboxContainerUITemplate(
            heading = AepText("Message Inbox"),
            capacity = 15,
            emptyMessage = AepText("No messages right now"),
            unreadIcon = AepImage(
                url = "https://icons.veryicon.com/png/o/leisure/crisp-app-icon-library-v3/notification-5.png",
                darkUrl = "https://icons.veryicon.com/png/o/leisure/crisp-app-icon-library-v3/notification-5.png",
            ),
            unreadBgColor = AepColor(Color.DarkGray, Color.LightGray),
            unreadIconAlignment = Alignment.TopStart
        )
    }
}
