/*
  Copyright 2023 Adobe. All rights reserved.
  This file is licensed to you under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0
  Unless required by applicable law or agreed to in writing, software distributed under
  the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR REPRESENTATIONS
  OF ANY KIND, either express or implied. See the License for the specific language
  governing permissions and limitations under the License.
*/

package com.adobe.marketing.mobile.messagingsample

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.adobe.marketing.mobile.Messaging
import com.adobe.marketing.mobile.aepcomposeui.AepUI
import com.adobe.marketing.mobile.aepcomposeui.AepUIConstants
import com.adobe.marketing.mobile.aepcomposeui.style.AepColumnStyle
import com.adobe.marketing.mobile.aepcomposeui.style.AepImageStyle
import com.adobe.marketing.mobile.aepcomposeui.style.AepLazyColumnStyle
import com.adobe.marketing.mobile.aepcomposeui.style.AepRowStyle
import com.adobe.marketing.mobile.aepcomposeui.style.AepTextStyle
import com.adobe.marketing.mobile.aepcomposeui.style.ContainerStyle
import com.adobe.marketing.mobile.aepcomposeui.style.ContentCardsStyle
import com.adobe.marketing.mobile.aepcomposeui.style.ImageOnlyUIStyle
import com.adobe.marketing.mobile.aepcomposeui.style.InboxContainerUIStyle
import com.adobe.marketing.mobile.aepcomposeui.style.LargeImageUIStyle
import com.adobe.marketing.mobile.aepcomposeui.style.SmallImageUIStyle
import com.adobe.marketing.mobile.aepcomposeui.viewmodel.AepContainerRepository
import com.adobe.marketing.mobile.aepcomposeui.viewmodel.AepContainerState
import com.adobe.marketing.mobile.aepcomposeui.components.AepContainer
import com.adobe.marketing.mobile.aepcomposeui.style.AepCardStyle
import com.adobe.marketing.mobile.aepcomposeui.style.AepLazyRowStyle
import com.adobe.marketing.mobile.aepcomposeui.style.CarouselContainerUIStyle
import com.adobe.marketing.mobile.messaging.ContentCardContainer
import com.adobe.marketing.mobile.messaging.ContentCardContainerUIProvider
import com.adobe.marketing.mobile.messaging.ContentCardUIEventListener
import com.adobe.marketing.mobile.messaging.ContentCardUIProvider
import com.adobe.marketing.mobile.messaging.Surface
import com.adobe.marketing.mobile.messagingsample.databinding.ActivityScrollingBinding
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.jvm.java

class ScrollingFeedActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScrollingBinding
    private lateinit var contentCardCallback: ContentCardCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityScrollingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // staging environment - CJM Stage, AJO Web (VA7)
        // surface for content card -
        // mobileapp://com.adobe.marketing.mobile.messagingsample/card/ms
        val surfaces = mutableListOf<Surface>()
        val surface = Surface("card/ms")
        surfaces.add(surface)

        // val viewModel = ViewModelProvider(this)[ExistingViewModel::class.java]
        contentCardCallback = ContentCardCallback()

        // Set a click listener for refresh button which calls the API for fetch content cards from Edge
//        val refreshButton: ImageButton = findViewById(R.id.refreshButton)
//        refreshButton.setOnClickListener {
//            Messaging.updatePropositionsForSurfaces(surfaces) { success ->
//                if (success) {
//                    Log.d(AepUIConstants.LOG_TAG, "Propositions updated successfully")
//                    // Now trigger the ContentCardContainer to refresh its content
//                    viewModel.refreshAepContainerUiState()
//                } else {
//                    Log.e(AepUIConstants.LOG_TAG, "Failed to update propositions")
//                }
//            }
//        }

        // Displaying content cards in a Column
        // create a custom style for the small image card in column
        val smallImageCardStyleColumn = SmallImageUIStyle.Builder()
            .rootRowStyle(
                AepRowStyle(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
                )
            )
            .build()

        val largeImageCardStyleColumn = LargeImageUIStyle.Builder()
            .imageStyle(
                AepImageStyle(
                    modifier = Modifier.fillMaxWidth().height(150.dp),
                    contentScale = ContentScale.FillWidth
                )
            )
            .textColumnStyle(AepColumnStyle(modifier = Modifier.padding(8.dp)))
            .build()

        val imageOnlyCardStyleColumn = ImageOnlyUIStyle.Builder()
            .imageStyle(
                AepImageStyle(
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.FillWidth
                )
            )
            .build()

        binding.composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppTheme {
//                    val containerUiState =
//                        viewModel.aepContainerState.collectAsStateWithLifecycle().value

                    val headingStyle = AepTextStyle(
                        modifier = Modifier.fillMaxWidth().padding(10.dp),
                        textStyle = TextStyle(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center
                        )
                    )

                    val inboxContainerStyle = InboxContainerUIStyle.Builder()
                        .headingStyle(headingStyle)
                        .lazyColumnStyle(
                            AepLazyColumnStyle(
                                modifier = Modifier.background(Color.Gray),
                                contentPadding = PaddingValues(10.dp)
                            )
                        )
                        .build()

                    val carouselContainerStyle = CarouselContainerUIStyle.Builder()
                        .headingStyle(headingStyle)
                        .lazyRowStyle(AepLazyRowStyle(
                            modifier = Modifier
                                .background(Color.Gray)
                                .height(220.dp),
                            contentPadding = PaddingValues(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ))
                        .build()

                    ContentCardContainer(
                        surface = surface,
                        containerStyle = ContainerStyle(
                            inboxContainerUIStyle = inboxContainerStyle,
                            carouselContainerUIStyle = carouselContainerStyle
                        ),
                        cardsStyle = ContentCardsStyle(
                            smallImageUIStyle = smallImageCardStyleColumn,
                            largeImageUIStyle = largeImageCardStyleColumn,
                            imageOnlyUIStyle = imageOnlyCardStyleColumn,
                        ),
                        cardUIEventListener = ContentCardCallback()
                    )
                }
            }
        }
    }
}

class ContentCardCallback: ContentCardUIEventListener {
    override fun onDisplay(aepUI: AepUI<*, *>) {
        Log.d("ContentCardCallback", "onDisplay")
    }

    override fun onDismiss(aepUI: AepUI<*, *>) {
        Log.d("ContentCardCallback", "onDismiss")
    }

    override fun onInteract(
        aepUI: AepUI<*, *>,
        interactionId: String?,
        actionUrl: String?
    ): Boolean {
        Log.d("ContentCardCallback", "onInteract $interactionId $actionUrl")
        // If the url is handled here, return true
        return false
    }
}
// create new view model or reuse existing one to hold the aepUIList
//class ExistingViewModel: ViewModel() {
//    private val contentCardUIProvider = ContentCardUIProvider(Surface("card/ms"))
//    private val containerUIProvider = ContentCardContainerUIProvider(Surface("card/ms"))
//    private val aepContainerRepository = AepContainerRepository(
//        contentCardUIProvider,
//        containerUIProvider
//    )
//
//    private val _aepContainerUiState = MutableStateFlow(aepContainerRepository.containerUiState.value)
//    val aepContainerState: StateFlow<AepContainerState> = _aepContainerUiState.asStateFlow()
//
//    init {
//        refreshAepContainerUiState()
//
//        viewModelScope.launch {
//            aepContainerRepository.containerUiState.collect { state ->
//                _aepContainerUiState.update { state }
//            }
//        }
//    }
//
//    fun refreshAepContainerUiState() {
//        viewModelScope.launch {
//            aepContainerRepository.refreshContainer()
//        }
//    }
//}