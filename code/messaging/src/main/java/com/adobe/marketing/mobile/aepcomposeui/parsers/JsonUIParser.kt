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

package com.adobe.marketing.mobile.aepcomposeui.parsers

import com.adobe.marketing.mobile.aepcomposeui.uimodels.JsonUIModel
import com.google.gson.Gson

/**
 * Parser for converting JSON UI model to SmallImageTemplate
 */
object JsonUIParser {
    
    /**
     * Parse a JSON string into a JsonUIModel
     * 
     * @param jsonString The JSON string to parse
     * @return The parsed JsonUIModel
     */
    fun parseJson(jsonString: String): JsonUIModel {
        val gson = Gson()
        return gson.fromJson(jsonString, JsonUIModel::class.java)
    }
} 