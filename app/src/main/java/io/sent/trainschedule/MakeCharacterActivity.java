package io.sent.trainschedule;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.EditText;
import android.widget.ImageView;

public class MakeCharacterActivity extends AppCompatActivity {

    EditText charaName;
    ImageView charaImage;

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
        charaName=(EditText)findViewById(R.id.chara_name_edit);
        charaImage=(ImageView)findViewById(R.id.chara_image);
    }

    private void initViews(){
        charaImage.setImageResource(R.drawable.nanaturao1);
    }
}
