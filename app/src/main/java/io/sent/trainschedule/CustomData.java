package io.sent.trainschedule;

import android.graphics.Bitmap;
import android.net.Uri;

/**
 * Created by sent13 on 16/06/11.
 */
public class CustomData {
    private Bitmap bitmap;
    private String textData_;

    public void setImageBitmap(Bitmap bitmap) {
        this.bitmap=bitmap;
    }

    public Bitmap getImageBitmap() {
        return bitmap;
    }

    public void setTextData(String text) {
        textData_ = text;
    }

    public String getTextData() {
        return textData_;
    }
}

