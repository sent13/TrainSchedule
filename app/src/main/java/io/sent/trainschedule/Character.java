package io.sent.trainschedule;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

/**
 * Created by sent13 on 16/05/29.
 */
public class Character implements Serializable{

    static final int COMPRESS_QUALITY=100;

    transient Bitmap charaImage;            //リサイズ済みのキャラクター画像
    private byte[] BitmapArray;

    String name,normalText, noTrainText, noCheckedText;

    public Character(Bitmap charaImage,String name, String normalText, String noTrainText, String noCheckedText){
        this.name=name;
        this.charaImage=charaImage;
        this.normalText=normalText;
        this.noTrainText=noTrainText;
        this.noCheckedText=noCheckedText;
        serializeBitmap();
    }

    //Bitmap→byte[]
    final void serializeBitmap() {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        charaImage.compress(Bitmap.CompressFormat.PNG, COMPRESS_QUALITY, bout);
        BitmapArray = bout.toByteArray();
    }

    //byte[]→Bitmap
    final void restorationBitmap() {
        if (BitmapArray == null) {
            return;
        }
        charaImage= BitmapFactory.decodeByteArray(BitmapArray, 0,
                BitmapArray.length);
    }

}
