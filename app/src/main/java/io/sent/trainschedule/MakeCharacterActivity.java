package io.sent.trainschedule;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class MakeCharacterActivity extends AppCompatActivity implements View.OnKeyListener{

    private static final int REQUEST_CHOOSER=1000;

    ScheduleApplication application;

    EditText charaNameEdit;
    ImageView charaImage;
    Button selectCharaImageBtn;
    EditText charaSerifNormalEdit;      //通常時のセリフを入力するエディタ
    EditText charaSerifNoTrainEdit;     //次の時間の電車がない場合のセリフを入力するエディタ
    EditText charaSerifNoCheckedEdit;       //チェックが入っていない時のセリフを入力するエディタ
    Button makeCharaButton;
    Button cancelButton;

    private InputMethodManager inputMethodManager;
    private Uri m_uri;
    private Uri resultUri;
    private Bitmap bitmap;
    private int editCharaIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.make_character);
        application=(ScheduleApplication)this.getApplication();
        Toolbar toolbar = (Toolbar) findViewById(R.id.make_character_toolbar);
        setSupportActionBar(toolbar);
        findViews();
        initViews();
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

        inputMethodManager=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    private void initViews(){
        Intent intent=getIntent();
        editCharaIndex=intent.getIntExtra("editCharaIndex", 0);

        //キャラ編集をする場合（デフォルトキャラは編集できないので値は０以外）
        if(editCharaIndex!=0){
            Character editChara=application.getCharacter(editCharaIndex);
            bitmap=editChara.charaImage;
            charaImage.setImageBitmap(bitmap);
            charaNameEdit.setText(editChara.name);
            charaSerifNormalEdit.setText(editChara.normalText);
            charaSerifNoTrainEdit.setText(editChara.noTrainText);
            charaSerifNoCheckedEdit.setText(editChara.noCheckedText);
        }else{
            bitmap= BitmapFactory.decodeResource(getResources(),R.drawable.unknown);
            charaImage.setImageResource(R.drawable.unknown);
        }
        charaNameEdit.setMaxLines(1);
        charaSerifNormalEdit.setMaxLines(3);
        charaSerifNoTrainEdit.setMaxLines(2);
        charaSerifNoCheckedEdit.setMaxLines(2);




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
                    String name=charaNameEdit.getText().toString().trim();
                    String normal=charaSerifNormalEdit.getText().toString().trim();
                    String noTrain=charaSerifNoTrainEdit.getText().toString().trim();
                    String noChecked=charaSerifNoCheckedEdit.getText().toString().trim();
                    Character chara=new Character(bitmap, name, normal, noTrain, noChecked);
                    if(editCharaIndex==0) {
                        application.addCharacter(chara);
                        application.setSelectedCharacterIndex(application.getCharaListSize() - 1);
                    }else{
                        application.setCharacter(editCharaIndex,chara);
                    }
                    setResult(RESULT_OK);
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

        charaNameEdit.setOnKeyListener(this);
        charaSerifNormalEdit.setOnKeyListener(this);
        charaSerifNoTrainEdit.setOnKeyListener(this);
        charaSerifNoCheckedEdit.setOnKeyListener(this);
    }

    //キーボードでエンターキーが押された時の処理
    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if((event.getAction()==KeyEvent.ACTION_DOWN) && (keyCode==KeyEvent.KEYCODE_ENTER)){

            switch(v.getId()){
                case R.id.chara_name_edit:
                    charaSerifNormalEdit.requestFocus();
                    break;
                case R.id.chara_serif_normal:
                    charaSerifNoTrainEdit.requestFocus();
                    break;
                case R.id.chara_serif_no_train:
                    charaSerifNoCheckedEdit.requestFocus();
                    break;
                case R.id.chara_serif_no_checked:
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(),
                            InputMethodManager.RESULT_SHOWN);
                    break;
            }
            return true;
        }
        return false;
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
