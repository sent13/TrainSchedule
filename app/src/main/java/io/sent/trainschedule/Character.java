package io.sent.trainschedule;

import android.graphics.Bitmap;

/**
 * Created by sent13 on 16/05/29.
 */
public class Character {

    Bitmap bitmap;
    String maeoki, atama, gobi, owari;

    public Character(Bitmap bitmap,String maeoki, String atama, String gobi, String owari){
        this.bitmap=bitmap;
        this.maeoki=maeoki;
        this.atama=atama;
        this.gobi=gobi;
        this.owari=owari;
    }

}
