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
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import com.adobe.marketing.mobile.ContentCardCallback
import com.adobe.marketing.mobile.ContentCardStyle
import com.adobe.marketing.mobile.ContentCards
import com.adobe.marketing.mobile.ContentCardsViewModel
import com.adobe.marketing.mobile.Messaging
import com.adobe.marketing.mobile.aepcomposeui.aepui.AepUI
import com.adobe.marketing.mobile.aepcomposeui.aepui.SmallImageUI
import com.adobe.marketing.mobile.aepcomposeui.aepui.style.AepButtonStyle
import com.adobe.marketing.mobile.aepcomposeui.aepui.style.AepTextStyle
import com.adobe.marketing.mobile.messaging.Proposition
import com.adobe.marketing.mobile.messaging.SchemaType
import com.adobe.marketing.mobile.messaging.Surface
import com.adobe.marketing.mobile.messagingsample.databinding.ActivityScrollingBinding

class ScrollingFeedActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScrollingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityScrollingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // retrieve any cached feed propositions
        var propositions = mutableListOf<Proposition>()
        val surfaces = mutableListOf<Surface>()

        // staging environment - CJM Stage, AJO Web (VA7)
        // surface for content card -
        // mobileapp://com.adobe.marketing.mobile.messagingsample/card/ms
        val surface = Surface("card/ms")

//        val surface = Surface("feeds/apifeed")
        surfaces.add(surface)
        Messaging.updatePropositionsForSurfaces(surfaces)

        binding.composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppTheme {
                    ContentCards(
                        ContentCardsViewModel("card/ms"),
/*                        contentCardStyle = ContentCardStyle().apply {
                            smallImageAepUiStyle.titleAepTextStyle =
                                AepTextStyle(textStyle = MaterialTheme.typography.h1)
                            smallImageAepUiStyle.buttonAepButtonStyle[1] =
                                AepButtonStyle(
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.Green
                                    )
                                )
                        },
                        container = { content ->
                            // Default to a Column if no container is provided
                            LazyRow(modifier = Modifier.fillMaxWidth().padding(10.dp)) {
                                item {
                                    content()
                                }
                            }
                        }*/
                    )
                }
            }
        }
/*        Messaging.getPropositionsForSurfaces(surfaces) {
            println("getPropositionsForSurfaces callback contained ${it.entries.size} entry/entries for surface ${surface.uri}")
            for (entry in it.entries) {
                for (proposition in entry.value) {
                    if (isContentCard(proposition)) {
                        propositions.add(proposition)
                    }
                }
            }

            // show feed items
            val feedInboxRecyclerView = findViewById<RecyclerView>(R.id.feedInboxView)
            val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            val feedCardAdapter = FeedCardAdapter(propositions)
            runOnUiThread {
                feedInboxRecyclerView.layoutManager = linearLayoutManager
                feedInboxRecyclerView.adapter = feedCardAdapter
            }
        }*/
    }

    private fun isContentCard(proposition: Proposition): Boolean {
        return proposition.items[0].schema == SchemaType.CONTENT_CARD
    }
}