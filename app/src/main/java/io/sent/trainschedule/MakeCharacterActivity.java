package io.sent.trainschedule;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;

public class MakeCharacterActivity extends AppCompatActivity {

    private static final int REQUEST_CHOOSER=1000;

    EditText charaNameEdit;
    ImageView charaImage;
    Button selectCharaImageBtn;
    EditText charaSerifNormalEdit;      //通常時のセリフを入力するエディタ
    EditText charaSerifNoTrainEdit;     //次の時間の電車がない場合のセリフを入力するエディタ
    EditText charaSerifNoCheckedEdit;       //チェックが入っていない時のセリフを入力するエディタ
    Button makeCharaButton;
    Button cancelButton;

    private Uri m_uri;
    private Uri resultUri;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.make_character);
        Toolbar toolbar = (Toolbar) findViewById(R.id.make_character_toolbar);
        setSupportActionBar(toolbar);
        findViews();
        initViews();
    }

    //アクションバーを作成
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void findViews(){
        charaNameEdit=(EditText)findViewById(R.id.chara_name_edit);
        charaImage=(ImageView)findViewById(R.id.chara_image);
        selectCharaImageBtn=(Button)findViewById(R.id.select_chara_imagebtn);
        charaSerifNormalEdit=(EditText)findViewById(R.id.chara_serif_normal);
        charaSerifNoTrainEdit=(EditText)findViewById(R.id.chara_serif_no_train);
        charaSerifNoCheckedEdit=(EditText)findViewById(R.id.chara_serif_no_checked);
        makeCharaButton=(Button)findViewById(R.id.make_chara_btn);
        cancelButton=(Button)findViewById(R.id.make_chara_cancel_btn);
    }

    private void initViews(){
        charaImage.setImageResource(R.drawable.unknown);
        charaNameEdit.setMaxLines(1);
        charaSerifNormalEdit.setMaxLines(3);
        charaSerifNoTrainEdit.setMaxLines(2);
        charaSerifNoCheckedEdit.setMaxLines(2);

        resultUri=Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                getResources().getResourcePackageName(R.drawable.unknown) + '/' +
                getResources().getResourceTypeName(R.drawable.unknown) + '/' +
                getResources().getResourceEntryName(R.drawable.unknown) );

        //キャラの画像をギャラリーから選ぶ
        selectCharaImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectGallery();
            }
        });

        //未入力がなければキャラクターを作成して前のアクティビティに戻る
        makeCharaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkCharaMake()) {
                    Intent intent = new Intent();
                    intent.putExtra("imageUri", resultUri);
                    intent.putExtra("name", charaNameEdit.getText().toString().trim());
                    intent.putExtra("normal", charaSerifNormalEdit.getText().toString().trim());
                    intent.putExtra("noTrain", charaSerifNoTrainEdit.getText().toString().trim());
                    intent.putExtra("noChecked", charaSerifNoCheckedEdit.getText().toString().trim());
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });

        //キャラクターを作成せず前のアクティビティに戻る
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    private void selectGallery(){
        String photoName = System.currentTimeMillis() + ".jpg";
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, photoName);
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        m_uri = getContentResolver()
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, m_uri);


        Intent intentGallery;
        if(Build.VERSION.SDK_INT <19){
            intentGallery=new Intent(Intent.ACTION_GET_CONTENT);
            intentGallery.setType("image/*");
        }else{
            intentGallery=new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intentGallery.addCategory(Intent.CATEGORY_OPENABLE);
            intentGallery.setType("image/jpeg");
        }
        Intent intent = Intent.createChooser(intentCamera, "画像の選択");
        intent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{intentGallery});
        startActivityForResult(intentGallery, REQUEST_CHOOSER);
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==REQUEST_CHOOSER){
            if(resultCode!=RESULT_OK){
                toast("画像の取得をキャンセルしました");
                return;
            }
            resultUri=(data != null ? data.getData():m_uri);
            if(resultUri == null) {
                // 取得失敗
                return;
            }

            bitmap=resizeImage.resize(this,resultUri);
            charaImage.setImageBitmap(bitmap);

            // ギャラリーへスキャンを促す
            MediaScannerConnection.scanFile(
                    this,
                    new String[]{resultUri.getPath()},
                    new String[]{"image/jpeg"},
                    null
            );


        }
    }


    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    //キャラクターを作成できる状態か判定しできないならエラーを表示
    private boolean checkCharaMake(){
        boolean flag=true;
        String msg="";

        String name,normal,noTrain,noCheckd;
        name=charaNameEdit.getText().toString().trim();
        normal=charaSerifNormalEdit.getText().toString().trim();
        noTrain=charaSerifNoTrainEdit.getText().toString().trim();
        noCheckd=charaSerifNoCheckedEdit.getText().toString().trim();

        if(name.equals("")){
            msg=msg.concat("キャラクターの名前が未入力です");
            flag=false;
        }
        if(normal.equals("")){
            if(!msg.equals("")) msg=msg.concat("\n");
            msg=msg.concat("通常の案内が未入力です");
            flag=false;
        }
        if(noTrain.equals("")){
            if(!msg.equals("")) msg=msg.concat("\n");
            msg=msg.concat("電車がない場合が未入力です");
            flag=false;
        }
        if(noCheckd.equals("")){
            if(!msg.equals("")) msg=msg.concat("\n");
            msg=msg.concat("チェックが入っていない場合が未入力です");
            flag=false;
        }


        if(flag==false){
            toast(msg);
        }

        return flag;
    }

    private void toast(String text){
        if(text==null) text="";
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }
}
