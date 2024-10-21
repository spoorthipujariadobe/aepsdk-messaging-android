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

import android.util.Log
import com.adobe.marketing.mobile.aepcomposeui.aepui.SmallImageUI
import com.adobe.marketing.mobile.aepcomposeui.interactions.UIEvent
import com.adobe.marketing.mobile.aepcomposeui.observers.AepUIEventObserver
import com.adobe.marketing.mobile.aepuitemplates.AepUITemplate

class MessagingUiEventObserver(private val callback: ContentCardCallback?) : AepUIEventObserver {
    override fun onEvent(event: UIEvent<*, *>) {
        when (event) {
            is UIEvent.Display -> {
            }
            is UIEvent.Interact -> {
                if (callback?.onCardClick(event.aepUi.getTemplate()) != true) {
                    handleClickEvent(event)
                }
            }
            is UIEvent.Dismiss -> {
            }
        }
    }

    private fun handleClickEvent(event: UIEvent.Interact<*, *>) {
        val aepUi = event.aepUi
        when (aepUi) {
            is SmallImageUI -> {
                Log.d("AepUiEventObserverImpl", "SmallImageAepUi Click")
            }
        }
    }
}

interface ContentCardCallback {
    fun onCardClick(template: AepUITemplate): Boolean
    fun onCardDismiss(template: AepUITemplate)
}
