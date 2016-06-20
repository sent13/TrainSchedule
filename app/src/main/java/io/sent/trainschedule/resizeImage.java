package io.sent.trainschedule;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by sent13 on 16/06/13.
 */
public class resizeImage {
    public static Bitmap resize(Activity activity,Uri uri){
        Bitmap rBitmap=null;
        try {
            InputStream inputStream = activity.getContentResolver().openInputStream(uri);

            // 画像サイズ情報を取得する
            BitmapFactory.Options imageOptions = new BitmapFactory.Options();
            imageOptions.inJustDecodeBounds = true;

            BitmapFactory.decodeStream(inputStream, null, imageOptions);
            Log.v("image", "Original Image Size: " + imageOptions.outWidth + " x " + imageOptions.outHeight);

            inputStream.close();

            // もし、画像が大きかったら縮小して読み込む
            //  今回はimageSizeMaxの大きさに合わせる
            int imageSizeMax = 200;
            inputStream = activity.getContentResolver().openInputStream(uri);
            float imageScaleWidth = (float) imageOptions.outWidth / imageSizeMax;
            float imageScaleHeight = (float) imageOptions.outHeight / imageSizeMax;

            // もしも、縮小できるサイズならば、縮小して読み込む
            if (imageScaleWidth > 2 && imageScaleHeight > 2) {
                BitmapFactory.Options imageOptions2 = new BitmapFactory.Options();

                // 縦横、小さい方に縮小するスケールを合わせる
                int imageScale = (int) Math.floor((imageScaleWidth > imageScaleHeight ? imageScaleHeight : imageScaleWidth));

                // inSampleSizeには2のべき上が入るべきなので、imageScaleに最も近く、かつそれ以下の2のべき上の数を探す
                for (int i = 2; i <= imageScale; i *= 2) {
                    imageOptions2.inSampleSize = i;
                }

                rBitmap = BitmapFactory.decodeStream(inputStream, null, imageOptions2);
                Log.v("image", "Sample Size: 1/" + imageOptions2.inSampleSize);
            } else {
                rBitmap = BitmapFactory.decodeStream(inputStream);
            }

            inputStream.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        return rBitmap;
    }
}
