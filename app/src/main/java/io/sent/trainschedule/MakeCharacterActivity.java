package io.sent.trainschedule;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.Toast;

public class MakeCharacterActivity extends AppCompatActivity{

    private static final int REQUEST_CHOOSER=1000;
    private static int PERMISSION_REQUEST_CODE=1;

    ScheduleApplication application;

    EditText charaNameEdit;
    ImageView charaImage;
    Button selectCharaImageBtn;
    Button makeCharaButton;
    Button cancelButton;

    private RetCharaStrInterface charaStrInterface;
    private InputMethodManager inputMethodManager;

    private Uri m_uri;
    private Uri resultUri;
    private Bitmap bitmap;
    private int editCharaIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        application=(ScheduleApplication)this.getApplication();
        if(application.getThemeNum()!=1) setTheme(R.style.NoActionBarThemeDark);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.make_character);
        Toolbar toolbar = (Toolbar) findViewById(R.id.make_character_toolbar);
        setSupportActionBar(toolbar);
        findViews();
        initViews();
    }

    //Fragment作成時のコールバック、FragmentをこのActivityから参照可能にする
    public void setCharaSerifInterface(){
        charaStrInterface=(RetCharaStrInterface)getSupportFragmentManager().findFragmentById(R.id.container);
        Intent intent=getIntent();
        editCharaIndex=intent.getIntExtra("editCharaIndex", 0);

        //キャラ編集をする場合（デフォルトキャラは編集できないので値は０以外）
        if(editCharaIndex!=0){
            Character editChara=application.getCharacter(editCharaIndex);
            bitmap=editChara.charaImage;
            charaImage.setImageBitmap(bitmap);
            charaNameEdit.setText(editChara.name);
            charaStrInterface.editCharaInit(editChara);
        }else{
            bitmap= BitmapFactory.decodeResource(getResources(),R.drawable.unknown);
            charaImage.setImageResource(R.drawable.unknown);
        }
    }


    private void findViews(){
        charaNameEdit=(EditText)findViewById(R.id.chara_name_edit);
        charaImage=(ImageView)findViewById(R.id.chara_image);
        selectCharaImageBtn=(Button)findViewById(R.id.select_chara_imagebtn);
        inputMethodManager=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        makeCharaButton=(Button)findViewById(R.id.make_chara_btn);
        cancelButton=(Button)findViewById(R.id.make_chara_cancel_btn);
    }

    private void initViews(){

        // FragmentTabHost を取得しタブ構造にする
        FragmentTabHost tabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
        tabHost.setup(this, getSupportFragmentManager(), R.id.container);

        TabHost.TabSpec tabSpec1, tabSpec2;

        // TabSpec を生成する
        tabSpec1 = tabHost.newTabSpec("tab1");
        tabSpec1.setIndicator("最初から");
        // TabHost に追加
        tabHost.addTab(tabSpec1, DefMakeCharaFragment.class, null);

        // TabSpec を生成する
        tabSpec2 = tabHost.newTabSpec("tab2");
        tabSpec2.setIndicator("サイトから");
        // TabHost に追加
        tabHost.addTab(tabSpec2, SiteMakeCharaFragment.class, null);

        // リスナー登録
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                
            }
        });


        charaNameEdit.setMaxLines(1);
        charaNameEdit.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if((event.getAction()== android.view.KeyEvent.ACTION_DOWN) && (keyCode== android.view.KeyEvent.KEYCODE_ENTER)) {

                    switch (v.getId()) {
                        case R.id.chara_name_edit:
                            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(),
                                    InputMethodManager.RESULT_SHOWN);
                            break;
                    }
                    return true;
                }
                return false;
            }
        });


        //キャラの画像をギャラリーから選ぶ
        selectCharaImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Android6.0以降ならパーミッションをチェックする
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //許可が下りているなら実行
                    if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_GRANTED) {
                        selectGallery();
                    } else {
                        //許可がないなら許可を得る処理
                        requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}
                                , PERMISSION_REQUEST_CODE);
                    }
                } else {
                    selectGallery();
                }
            }
        });

        //未入力がなければキャラクターを作成して前のアクティビティに戻る
        makeCharaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkCharaMake()) {
                    String name = charaNameEdit.getText().toString().trim();

                    String charaSerif=charaStrInterface.getCharaSerif();
                    String[] strings=charaSerif.split(",",-1);
                    if(strings.length!=3){
                        toast("作成できませんでした");
                        return;
                    }
                    Character chara = new Character(bitmap, name, strings[0].trim(), strings[1].trim()
                            , strings[2].trim());

                    if (editCharaIndex == 0) {
                        application.addCharacter(chara);
                        application.setSelectedCharacterIndex(application.getCharaListSize() - 1);
                    } else {
                        application.setCharacter(editCharaIndex, chara);
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


    }




    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        if(PERMISSION_REQUEST_CODE == requestCode){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //許可されたなら
                selectGallery();
            }else{
                toast("許可がないので画像を選択できません");
            }
        }
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
        StringBuffer msg=new StringBuffer("");

        String name,normal,noTrain,noCheckd;
        name=charaNameEdit.getText().toString().trim();

        if(name.equals("")){
            msg=msg.append("キャラクターの名前が未入力です");
            flag=false;
        }

        String charaSerif=charaStrInterface.getCharaSerif();
        String[] strings=charaSerif.split(",",-1);
        if(strings.length!=3){
            if(!msg.toString().equals("")) msg = msg.append("\n");
            msg=msg.append("形式が正しくありません");
            toast(msg.toString());
            return false;
        }
        normal=strings[0].trim();
        noTrain=strings[1].trim();
        noCheckd=strings[2].trim();

        if(normal.equals("")){
            if(!msg.toString().equals("")) msg=msg.append("\n");
            msg=msg.append("通常の案内が未入力です");
            flag=false;
        }
        if(noTrain.equals("")){
            if(!msg.toString().equals("")) msg=msg.append("\n");
            msg=msg.append("電車がない場合が未入力です");
            flag=false;
        }
        if(noCheckd.equals("")){
            if(!msg.toString().equals("")) msg=msg.append("\n");
            msg=msg.append("チェックが入っていない場合が未入力です");
            flag=false;
        }
        if(name.length()>=50 || normal.length()>=50 || noTrain.length()>=50 || noCheckd.length()>=50){
            if(!msg.equals("")) msg=msg.append("\n");
            msg=msg.append("文字数が50を超えている項目があります");
            flag=false;
        }


        if(flag==false){
            toast(msg.toString());
        }

        return flag;
    }

    private void toast(String text){
        if(text==null) text="";
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }
}
