package io.sent.trainschedule;

import android.graphics.Bitmap;
import android.net.Uri;

import java.io.Serializable;

/**
 * Created by sent13 on 16/05/29.
 */
public class Character implements Serializable{

    Uri imageUri;            //キャラクター画像が保存されているパス
    String name,normalText, noTrainText, noCheckedText;

    public Character(Uri imageUri,String name, String normalText, String noTrainText, String noCheckedText){
        this.name=name;
        this.imageUri=imageUri;
        this.normalText=normalText;
        this.noTrainText=noTrainText;
        this.noCheckedText=noCheckedText;
    }

}
