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

package com.adobe.marketing.mobile.aepcomposeui.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.adobe.marketing.mobile.aepcomposeui.AepUIConstants.LOG_TAG
import com.adobe.marketing.mobile.services.HttpMethod
import com.adobe.marketing.mobile.services.Log
import com.adobe.marketing.mobile.services.NetworkRequest
import com.adobe.marketing.mobile.services.ServiceProvider
import java.net.HttpURLConnection
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

internal object UIUtils {

    private const val SELF_TAG = "UIUtils"
    private const val DOWNLOAD_TIMEOUT_SECS = 10

    /**
     * Downloads the image from the given URL.
     *
     * @param url the URL of the image to download.
     * @return the downloaded image as a [Bitmap].
     */
    // TODO: This method is repeated in Messaging, maybe it should be moved to a common place
    fun downloadImage(url: String?): Bitmap? {
        if (url.isNullOrBlank()) {
            Log.warning(
                LOG_TAG,
                SELF_TAG,
                "Failed to download image, the URL is null or empty."
            )
            return null
        }
        var bitmap: Bitmap? = null
        val latch = CountDownLatch(1)
        val networkRequest = NetworkRequest(
            url,
            HttpMethod.GET,
            null,
            null,
            DOWNLOAD_TIMEOUT_SECS,
            DOWNLOAD_TIMEOUT_SECS
        )

        ServiceProvider.getInstance()
            .networkService
            .connectAsync(networkRequest) { connection ->
                try {
                    if (connection == null) {
                        Log.warning(
                            LOG_TAG,
                            SELF_TAG,
                            "Failed to download image from url ($url), received a null connection."
                        )
                        latch.countDown()
                        return@connectAsync
                    }
                    if ((connection.responseCode == HttpURLConnection.HTTP_OK)) {
                        connection.inputStream.use { inputStream ->
                            bitmap = BitmapFactory.decodeStream(inputStream)
                        }
                    } else {
                        Log.debug(
                            LOG_TAG,
                            SELF_TAG,
                            "Failed to download image from url ($url). Response code was: ${connection.responseCode}."
                        )
                    }
                } catch (e: Exception) {
                    Log.warning(
                        LOG_TAG,
                        SELF_TAG,
                        "Exception while processing image download: ${e.localizedMessage}"
                    )
                } finally {
                    connection.close()
                    latch.countDown()
                }
            }
        // Wait for the download to complete or timeout
        try {
            if (!latch.await(DOWNLOAD_TIMEOUT_SECS.toLong(), TimeUnit.SECONDS)) {
                Log.warning(LOG_TAG, SELF_TAG, "Timed out waiting for image download to complete.")
            }
        } catch (e: InterruptedException) {
            Log.warning(
                LOG_TAG,
                SELF_TAG,
                "Interrupted while waiting for image download to complete: ${e.localizedMessage}"
            )
        }
        return bitmap
    }
}
