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
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.adobe.marketing.mobile.Messaging
import com.adobe.marketing.mobile.aepcomposeui.AepUI
import com.adobe.marketing.mobile.aepcomposeui.SmallImageUI
import com.adobe.marketing.mobile.aepcomposeui.components.DynamicUIFromJson
import com.adobe.marketing.mobile.aepcomposeui.components.SmallImageCard
import com.adobe.marketing.mobile.messaging.ContentCardEventObserver
import com.adobe.marketing.mobile.messaging.ContentCardUIEventListener
import com.adobe.marketing.mobile.messaging.ContentCardUIProvider
import com.adobe.marketing.mobile.messaging.SchemaType
import com.adobe.marketing.mobile.messaging.Surface
import com.adobe.marketing.mobile.messagingsample.databinding.ActivityScrollingBinding
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

class ScrollingFeedActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScrollingBinding
    private lateinit var contentCardViewModel: AepContentCardViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityScrollingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize ViewModel
        contentCardViewModel = ViewModelProvider(this)[AepContentCardViewModel::class.java]

        Messaging.updatePropositionsForSurfaces(mutableListOf(Surface("card/ms"))) { result ->
            if(result) {
                    contentCardViewModel.refreshContent()
                }
            }
        // Read JSON templates
        val smallImageTemplate = resources.openRawResource(R.raw.small_image_template)
            .bufferedReader().use { it.readText() }
        val largeImageTemplate = resources.openRawResource(R.raw.large_image_template)
            .bufferedReader().use { it.readText() }
        val imageOnlyTemplate = resources.openRawResource(R.raw.image_only_template)
            .bufferedReader().use { it.readText() }

        binding.composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                AppTheme {
                    TabScreen(
                        smallImageTemplate = smallImageTemplate,
                        largeImageTemplate = largeImageTemplate,
                        imageOnlyTemplate = imageOnlyTemplate,
                        viewModel = contentCardViewModel
                    )
                }
            }
        }
    }
}

@Composable
private fun TabScreen(
    smallImageTemplate: String,
    largeImageTemplate: String,
    imageOnlyTemplate: String,
    viewModel: AepContentCardViewModel
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Small Image", "Large Image", "Image Only", "Server Content")

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(text = title) }
                )
            }
        }

        when (selectedTabIndex) {
            0 -> TemplateContent(jsonString = smallImageTemplate)
            1 -> TemplateContent(jsonString = largeImageTemplate)
            2 -> TemplateContent(jsonString = imageOnlyTemplate)
            3 -> ServerContent(viewModel = viewModel)
        }
    }
}

@Composable
private fun TemplateContent(jsonString: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        DynamicUIFromJson(jsonString = jsonString)
    }
}

@Composable
private fun ServerContent(viewModel: AepContentCardViewModel) {
    val aepUiList by viewModel.aepUIList.collectAsStateWithLifecycle()

    if (aepUiList.isNotEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            DynamicUIFromJson(jsonString = aepUiList[0].get("content").toString())
        }
    }
}

// create new view model or reuse existing one to hold the aepUIList
class AepContentCardViewModel : ViewModel() {
    // State to hold AepUI list
    private val _aepUIList = MutableStateFlow<List<JSONObject>>(emptyList())
    val aepUIList: StateFlow<List<JSONObject>> = _aepUIList.asStateFlow()

    init {
        // Launch a coroutine to fetch the aepUIList from the ContentCardUIProvider
        // when the ViewModel is created
        viewModelScope.launch {
            getCBECards()
        }
    }

    // Function to refresh the aepUIList from the ContentCardUIProvider
    fun refreshContent() {
        viewModelScope.launch {
            getCBECards()
        }
    }

    private fun getCBECards() {
        val surfaces = mutableListOf<Surface>()
        val surface = Surface("card/ms")
        surfaces.add(surface)
        val cbeCardJsonList = mutableListOf<JSONObject>()
        Messaging.getPropositionsForSurfaces(surfaces) { propositionsMap ->
            if (propositionsMap.isNotEmpty()) {
                val propositionList = propositionsMap[surface]
                if (!propositionList.isNullOrEmpty()) {
                    for(proposition in propositionList) {
                        for(propositionItem in proposition.items) {
                            if (propositionItem.schema == SchemaType.JSON_CONTENT){
                                cbeCardJsonList.add(JSONObject(propositionItem.itemData))
                            }
                        }
                    }
                }
                _aepUIList.value = cbeCardJsonList
            }
        }
    }
}