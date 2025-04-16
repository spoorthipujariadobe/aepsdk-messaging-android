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

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adobe.marketing.mobile.aepcomposeui.observers.AepUIEventObserver
import com.adobe.marketing.mobile.aepcomposeui.parsers.JsonUIParser
import com.adobe.marketing.mobile.aepcomposeui.uimodels.JsonUIModel
import com.adobe.marketing.mobile.aepcomposeui.uimodels.StyleModel
import com.adobe.marketing.mobile.messaging.ContentCardImageManager
import android.util.Log
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.text.style.TextAlign
import com.adobe.marketing.mobile.aepcomposeui.style.AepButtonStyle
import com.adobe.marketing.mobile.aepcomposeui.style.AepImageStyle
import com.adobe.marketing.mobile.aepcomposeui.uimodels.AepButton
import com.adobe.marketing.mobile.aepcomposeui.uimodels.AepText
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.ui.layout.ContentScale
import com.adobe.marketing.mobile.aepcomposeui.style.AepIconStyle
import androidx.compose.ui.platform.LocalContext

/**
 * Composable function that renders a dynamic UI component from a JSON string.
 *
 * @param jsonString The JSON string representing the UI to be rendered.
 * @param observer An optional observer that listens to UI events.
 */
@Composable
fun DynamicUIFromJson(
    jsonString: String,
    observer: AepUIEventObserver? = null
) {
    val jsonModel = JsonUIParser.parseJson(jsonString)
    Log.d("DynamicUI", "Parsed JSON model: $jsonModel")
    DynamicUIComponent(jsonModel, Modifier, observer)
}

/**
 * Recursively renders UI components based on a JsonUIModel.
 *
 * @param model The JsonUIModel to render.
 * @param modifier The Modifier to apply to the component.
 * @param observer An optional observer that listens to UI events.
 */
@Composable
fun DynamicUIComponent(
    model: JsonUIModel,
    modifier: Modifier = Modifier,
    observer: AepUIEventObserver? = null
) {
    // Combine the provided modifier with this component's style
    val combinedModifier = modifier.then(createModifierFromStyle(model.style))

    when (model.type.lowercase()) {
        "view" -> {
            when (model.style?.flexDirection?.lowercase()) {
                "row" -> {
                    Row(
                        modifier =  combinedModifier,
                        verticalAlignment = when (model.style.alignItems) {
                            "center" -> Alignment.CenterVertically
                            "flex-start" -> Alignment.Top
                            "flex-end" -> Alignment.Bottom
                            else -> Alignment.CenterVertically
                        }
                    ) {
                        model.children?.forEach { child ->
                            // Create child modifier with alignment if specified
                            val childModifier = if (child.style?.justifyContent != null) {
                                when (child.style.justifyContent) {
                                    "center" -> Modifier.align(Alignment.CenterVertically)
                                    "flex-start", "top", "start" -> Modifier.align(Alignment.Top)
                                    "flex-end", "bottom", "end" -> Modifier.align(Alignment.Bottom)
                                    else -> Modifier
                                }
                            } else {
                                Modifier
                            }
                            
                            // Add weight if specified
                            val finalChildModifier = if (child.style?.flex != null) {
                                childModifier.then(Modifier.weight(child.style.flex))
                            } else {
                                childModifier
                            }
                            
                            // Pass the combined modifier to the child component
                            DynamicUIComponent(
                                model = child,
                                modifier = finalChildModifier,
                                observer = observer
                            )
                        }
                    }
                }
                "column" -> {
                    Column(
                        modifier = combinedModifier,
                        horizontalAlignment = when (model.style.alignItems) {
                            "center" -> Alignment.CenterHorizontally
                            "flex-start" -> Alignment.Start
                            "flex-end" -> Alignment.End
                            else -> Alignment.Start
                        }
                    ) {
                        model.children?.forEach { child ->
                            // Create child modifier with alignment if specified
                            val childModifier = if (child.style?.justifyContent != null) {
                                when (child.style.justifyContent) {
                                    "center" -> Modifier.align(Alignment.CenterHorizontally)
                                    "flex-start", "start" -> Modifier.align(Alignment.Start)
                                    "flex-end", "end" -> Modifier.align(Alignment.End)
                                    else -> Modifier
                                }
                            } else {
                                Modifier
                            }
                            
                            // Add weight if specified
                            val finalChildModifier = if (child.style?.flex != null) {
                                childModifier.then(Modifier.weight(child.style.flex))
                            } else {
                                childModifier
                            }
                            
                            // Pass the combined modifier to the child component
                            DynamicUIComponent(
                                model = child,
                                modifier = finalChildModifier,
                                observer = observer
                            )
                        }
                    }
                }
                else -> {
                        Box(
                            modifier = combinedModifier,
                            contentAlignment = when (model.style?.alignItems) {
                                "center" -> Alignment.Center
                                "flex-start", "topStart", "topLeft" -> Alignment.TopStart
                                "flex-end", "bottomEnd", "bottomRight" -> Alignment.BottomEnd
                                "top" -> Alignment.TopCenter
                                "bottom" -> Alignment.BottomCenter
                                "start", "left" -> Alignment.CenterStart
                                "end", "right" -> Alignment.CenterEnd
                                "topEnd", "topRight" -> Alignment.TopEnd
                                "bottomStart", "bottomLeft" -> Alignment.BottomStart
                                else -> Alignment.Center
                            }
                        ) {
                            model.children?.forEach { child ->
                                // Create child modifier with alignment if specified
                                val childModifier = if (child.style?.justifyContent != null) {
                                    when (child.style.justifyContent) {
                                        "topStart", "topLeft" -> Modifier.align(Alignment.TopStart)
                                        "top" -> Modifier.align(Alignment.TopCenter)
                                        "topEnd", "topRight" -> Modifier.align(Alignment.TopEnd)
                                        "center" -> Modifier.align(Alignment.Center)
                                        "start", "left" -> Modifier.align(Alignment.CenterStart)
                                        "end", "right" -> Modifier.align(Alignment.CenterEnd)
                                        "bottomStart", "bottomLeft" -> Modifier.align(Alignment.BottomStart)
                                        "bottom" -> Modifier.align(Alignment.BottomCenter)
                                        "bottomEnd", "bottomRight" -> Modifier.align(Alignment.BottomEnd)
                                        else -> Modifier
                                    }
                                } else {
                                    Modifier
                                }

                                // Pass the alignment modifier to the child component
                                DynamicUIComponent(
                                    model = child,
                                    modifier = childModifier,
                                    observer = observer
                                )
                            }
                        }
                    }
            }
        }
        "text" -> {
            Text(
                text = model.content ?: "",
                modifier = combinedModifier,
                fontSize = model.style?.fontSize?.sp ?: 14.sp,
                fontWeight = when (model.style?.fontWeight) {
                    "bold" -> FontWeight.Bold
                    "400" -> FontWeight.Normal
                    "500" -> FontWeight.Medium
                    "600" -> FontWeight.SemiBold
                    "700" -> FontWeight.Bold
                    else -> FontWeight.Normal
                },
                color = model.style?.color?.let {
                    try {
                        Color(android.graphics.Color.parseColor(it))
                    } catch (e: Exception) {
                        MaterialTheme.colorScheme.onSurface
                    }
                } ?: MaterialTheme.colorScheme.onSurface,
                textAlign = when (model.style?.textAlign) {
                    "center" -> TextAlign.Center
                    "right", "end" -> TextAlign.End
                    "left", "start" -> TextAlign.Start
                    else -> null
                }
            )
        }
        "image" -> {
            DynamicImageComponent(
                model = model,
                modifier = combinedModifier,
                observer = observer
            )
        }
        "icon" -> {
            // Parse the drawable ID from the model
            val drawableName = model.content?.toString() ?: ""
            val resources = LocalContext.current.resources
            val drawableId = resources.getIdentifier(drawableName, "drawable", LocalContext.current.packageName)
            
            if (drawableId != 0) {
                // Create icon style with tint if specified
                val iconTint = model.style?.color?.let {
                    try {
                        Color(android.graphics.Color.parseColor(it))
                    } catch (e: Exception) {
                        null
                    }
                }
                
                AepIconComposable(
                    drawableId = drawableId,
                    iconStyle = AepIconStyle(
                        modifier = combinedModifier,
                        tint = iconTint,
                        contentDescription = model.content
                    )
                )
            } else {
                // Render an empty box if drawable ID is invalid
                Box(modifier = combinedModifier)
            }
        }
        "button" -> {
            AepButtonComposable(
                model = AepButton(
                    id = model.id ?: "button",
                    text = AepText(model.content ?: "Button"),
                    actionUrl = model.actionUrl ?: ""
                ),
                onClick = {
                    // Handle button click
                    model.actionUrl?.let {
                        // Trigger action
                    }
                },
                buttonStyle = AepButtonStyle(
                    modifier = combinedModifier
                )
            )
        }
        else -> {
            // Unknown component type, render nothing or a placeholder
            Box(modifier = combinedModifier)
        }
    }
}

/**
 * Creates a Modifier based on the provided StyleModel.
 *
 * @param style The StyleModel to create a Modifier from.
 * @return A Modifier with the specified styles applied.
 */
@Composable
private fun createModifierFromStyle(style: StyleModel?): Modifier {
    if (style == null) return Modifier
    
    return Modifier
        // Apply size constraints
        .then(
            if (style.width != null) {
                Modifier.width(width = style.width.dp)
            } else if (style.fillWidth == true) {
                Modifier.fillMaxWidth()
            } else {
                // For row that don't have a specific width, use IntrinsicSize.Min
                // This will make the row only as wide as its content requires
                if (style.flexDirection?.lowercase() == "row") {
                    Modifier.width(IntrinsicSize.Min)
                } else {
                    Modifier.wrapContentWidth()
                }
            }
        )
        .then(
            if (style.height != null) {
                Modifier.height(height = style.height.dp)
            } else if (style.fillHeight == true) {
                Modifier.fillMaxHeight()
            } else {
                // For columns that don't have a specific height, use IntrinsicSize.Min
                // This will make the column only as tall as its content requires
                if (style.flexDirection?.lowercase() == "column") {
                    Modifier.height(IntrinsicSize.Min)
                } else {
                    Modifier.wrapContentHeight()
                }
            }
        )
        // Apply background color
        .then(
            if (style.backgroundColor != null) {
                try {
                    val color = Color(android.graphics.Color.parseColor(style.backgroundColor))
                    Modifier.background(color)
                } catch (e: Exception) {
                    Modifier
                }
            } else {
                Modifier
            }
        )
        // Apply border radius
        .then(
            if (style.borderRadius != null) {
                Modifier.clip(RoundedCornerShape(style.borderRadius.dp))
            } else {
                Modifier
            }
        )
        // Apply border
        .then(
            if (style.borderWidth != null && style.borderWidth > 0) {
                val borderColor = style.borderColor?.let {
                    try {
                        Color(android.graphics.Color.parseColor(it))
                    } catch (e: Exception) {
                        Color.Black
                    }
                } ?: Color.Black

                Modifier.border(
                    width = style.borderWidth.dp,
                    color = borderColor,
                    shape = if (style.borderRadius != null) {
                        RoundedCornerShape(style.borderRadius.dp)
                    } else {
                        RoundedCornerShape(0.dp)
                    }
                )
            } else {
                Modifier
            }
        )
        // Apply padding
        .then(
            if (style.padding != null) {
                Modifier.padding(all = style.padding.dp)
            } else {
                Modifier
            }
        )
        .then(
            if (style.paddingVertical != null) {
                Modifier.padding(vertical = style.paddingVertical.dp)
            } else {
                Modifier
            }
        )
        .then(
            if (style.paddingHorizontal != null) {
                Modifier.padding(horizontal = style.paddingHorizontal.dp)
            } else {
                Modifier
            }
        )
        // Apply margins
        .then(
            if (style.marginLeft != null) {
                Modifier.padding(start = style.marginLeft.dp)
            } else {
                Modifier
            }
        )
        .then(
            if (style.marginRight != null) {
                Modifier.padding(end = style.marginRight.dp)
            } else {
                Modifier
            }
        )
        .then(
            if (style.marginTop != null) {
                Modifier.padding(top = style.marginTop.dp)
            } else {
                Modifier
            }
        )
        .then(
            if (style.marginBottom != null) {
                Modifier.padding(bottom = style.marginBottom.dp)
            } else {
                Modifier
            }
        )
}

/**
 * Renders an image component with loading state.
 *
 * @param model The JsonUIModel representing an image.
 * @param modifier The Modifier to apply to the image.
 * @param observer An optional observer that listens to UI events.
 */
@Composable
private fun DynamicImageComponent(
    model: JsonUIModel,
    modifier: Modifier = Modifier,
    observer: AepUIEventObserver? = null
) {
    var isLoading by remember { mutableStateOf(true) }
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(model.url) {
        if (model.url.isNullOrBlank()) {
            isLoading = false
        } else {
            ContentCardImageManager.getContentCardImageBitmap(model.url) {
                it.onSuccess { bitmap ->
                    imageBitmap = bitmap
                    isLoading = false
                }
                it.onFailure {
                    isLoading = false
                }
            }
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        imageBitmap?.let {
            AepImageComposable(
                content = BitmapPainter(it.asImageBitmap()),
                imageStyle = AepImageStyle(
                    modifier = modifier
                        .then(
                            if (model.style?.aspectRatio != null) {
                                val widthRatio: Float =
                                    model.style.aspectRatio.substringBefore("/").toFloat()
                                val heightRatio: Float =
                                    model.style.aspectRatio.substringAfter("/").toFloat()
                                Modifier.aspectRatio(widthRatio / heightRatio)
                            } else {
                                Modifier
                            }
                        ),
                    contentScale = when (model.style?.contentScale?.lowercase()) {
                        "crop" -> ContentScale.Crop
                        "fit" -> ContentScale.Fit
                        "fillBounds" -> ContentScale.FillBounds
                        "fillHeight" -> ContentScale.FillHeight
                        "fillWidth" -> ContentScale.FillWidth
                        "inside" -> ContentScale.Inside
                        "none" -> ContentScale.None
                        else -> ContentScale.Crop
                    },
                    alignment = Alignment.Center
                )
            )
        }

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                strokeWidth = 4.dp
            )
        }
    }
}