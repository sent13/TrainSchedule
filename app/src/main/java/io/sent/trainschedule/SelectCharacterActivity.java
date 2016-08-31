package io.sent.trainschedule;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SelectCharacterActivity extends AppCompatActivity {

    ScheduleApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        application=(ScheduleApplication)this.getApplication();
        if(application.getThemeNum()!=1) setTheme(R.style.NoActionBarThemeDark);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_character);
        initViews();
    }

    private void initViews(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.select_character_toolbar);
        setSupportActionBar(toolbar);

        List<CustomData> objects=new ArrayList<>();
        // アイテムを追加します
        for(int i=0;i<application.getCharaListSize();i++){
            CustomData item=new CustomData();
            item.setImageBitmap(application.getCharacter(i).charaImage);
            item.setTextData(application.getCharacter(i).name);
            objects.add(item);
        }


        CustomAdapter customAdapter=new CustomAdapter(this, 0, objects);

        ListView listView = (ListView) findViewById(R.id.characterListView);
        // アダプターを設定します
        listView.setAdapter(customAdapter);


        // リストビューのアイテムがクリックされた時に呼び出されるコールバックリスナーを登録します
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent=new Intent();
                intent.putExtra("index",position);
                setResult(RESULT_OK,intent);
                finish();
            }
        });

        // リストビューのアイテムが選択された時に呼び出されるコールバックリスナーを登録します
        listView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                ListView listView = (ListView) parent;
                // 選択されたアイテムを取得します
                String item = (String) listView.getSelectedItem();
                Toast.makeText(SelectCharacterActivity.this, item, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

}
