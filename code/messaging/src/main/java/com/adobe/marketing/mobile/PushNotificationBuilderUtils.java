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

package com.adobe.marketing.mobile;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.adobe.marketing.mobile.messaging.internal.MessagingConstants;
import com.adobe.marketing.mobile.services.Log;
import com.adobe.marketing.mobile.util.StringUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

class PushNotificationBuilderUtils {
    private static final String SELF_TAG = "PushNotificationMediaDownloader";
    private final Context context;

    PushNotificationBuilderUtils(Context context) {
        this.context = context;
    }

    Bitmap download(String url) {
        Bitmap bitmap = null;
        try {
            URL imageUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();
            bitmap = BitmapFactory.decodeStream(connection.getInputStream());
            connection.disconnect();
        } catch(IOException e) {
            Log.warning(MessagingConstants.LOG_TAG, SELF_TAG, "Failed to download push notification large image from url: %s", url);
        }
        return bitmap;
    }

    int getDefaultAppIcon() {
        final String packageName = context.getPackageName();
        try {
            return context.getPackageManager().getApplicationInfo(packageName, 0).icon;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return -1;
    }

    /**
     * Returns the Uri for the sound file with the given name.
     * The sound file must be in the res/raw directory.
     * The sound file should be in format of .mp3, .wav, or .ogg
     *
     * @param soundName the name of the sound file
     * @return the Uri for the sound file with the given name
     */
    Uri getSoundUriForResourceName(@NonNull String soundName) {
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getPackageName()
                + "/raw/" + soundName);
    }

    /**
     * Returns the resource id for the icon with the given name.
     * The icon file must be in the res/drawable directory.
     * If the icon file is not found, 0 is returned.
     *
     * @param iconName the name of the icon file
     * @return the resource id for the icon with the given name
     */
    int getSmallIconWithResourceName(String iconName) {
        if (StringUtils.isNullOrEmpty(iconName)) {
            return 0;
        }
        return context.getResources().getIdentifier(iconName, "drawable", context.getPackageName());
    }
}
