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

import com.adobe.marketing.mobile.aepcomposeui.AepContainerUI
import com.adobe.marketing.mobile.aepcomposeui.AepUI
import com.adobe.marketing.mobile.aepcomposeui.CarouselContainerUI
import com.adobe.marketing.mobile.aepcomposeui.ContainerEvent
import com.adobe.marketing.mobile.aepcomposeui.InboxContainerUI
import com.adobe.marketing.mobile.aepcomposeui.observers.AepContainerUIEventObserver
import com.adobe.marketing.mobile.aepcomposeui.observers.AepUIEventObserver

class ContentCardContainerUIEventObserver(private val callback: ContentCardUIEventListener?) : AepContainerUIEventObserver {
    private var containerUI: AepContainerUI<*, *>? = null
    override val aepUIEventObserver: AepUIEventObserver
        get() = ContentCardEventObserver(this)

    override fun onContainerEvent(event: ContainerEvent<*, *>) {
        containerUI = event.aepContainerUI
    }

    override fun onDisplay(aepUI: AepUI<*, *>) {
        callback?.onDisplay(aepUI)
    }

    override fun onDismiss(aepUI: AepUI<*, *>) {
        containerUI?.let {
            val newList = it.getAepContainerState().aepUIList.toMutableList()
            newList.remove(aepUI)
            when (it) {
                is InboxContainerUI -> {
                    val currentState = it.getAepContainerState()
                    it.updateAepContainerState(currentState.copy(aepUIList = newList))
                }

                is CarouselContainerUI -> {
                    val currentState = it.getAepContainerState()
                    it.updateAepContainerState(currentState.copy(aepUIList = newList))
                }
            }
        }
        callback?.onDismiss(aepUI)
    }

    override fun onInteract(
        aepUI: AepUI<*, *>,
        interactionId: String?,
        actionUrl: String?
    ): Boolean {
        return callback?.onInteract(aepUI, interactionId, actionUrl) ?: false
    }
}
