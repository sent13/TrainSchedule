package io.sent.trainschedule;

import android.graphics.Bitmap;
import android.net.Uri;

/**
 * Created by sent13 on 16/06/11.
 */
public class CustomData {
    private Uri imageUri;
    private String textData_;

    public void setImageUri(Uri uri) {
        imageUri = uri;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setTextData(String text) {
        textData_ = text;
    }

    public String getTextData() {
        return textData_;
    }
}

