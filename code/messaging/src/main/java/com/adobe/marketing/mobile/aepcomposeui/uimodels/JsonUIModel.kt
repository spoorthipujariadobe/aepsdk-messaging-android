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

package com.adobe.marketing.mobile.aepcomposeui.uimodels

/**
 * Data classes representing the JSON structure for UI components
 */
data class JsonUIModel(
    val type: String,
    val style: StyleModel? = null,
    val children: List<JsonUIModel>? = null,
    val content: String? = null,
    val url: String? = null,
    val id: String? = null,
    val actionUrl: String? = null
)

data class StyleModel(
    val flexDirection: String? = null,
    val padding: Int? = null,
    val paddingVertical: Int? = null,
    val paddingHorizontal: Int? = null,
    val marginLeft: Int? = null,
    val marginRight: Int? = null,
    val marginTop: Int? = null,
    val marginBottom: Int? = null,
    val width: Int? = null,
    val fillWidth: Boolean? = null,
    val height: Int? = null,
    val fillHeight: Boolean? = null,
    val borderRadius: Int? = null,
    val fontSize: Int? = null,
    val fontWeight: String? = null,
    val backgroundColor: String? = null,
    val color: String? = null,
    val textAlign: String? = null,
    val justifyContent: String? = null,
    val alignItems: String? = null,
    val flex: Float? = null,
    val borderWidth: Int? = null,
    val borderColor: String? = null,
    val contentScale: String? = null,
    val aspectRatio: String? = null
) 