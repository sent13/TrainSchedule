package io.sent.trainschedule;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import java.lang.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    private ScheduleApplication application;

    private TextView textView1;
    private TextView textView2;
    private NumberPicker numberPicker1;
    private NumberPicker numberPicker2;
    private TextView textResult1;
    private ImageView imageView1;
    private ImageView imageView2;
    private Calendar calendar;
    private Spinner spinner;
    private RadioGroup radioGroup;
    private RadioButton radioButton1;
    private RadioButton radioButton2;
    private CheckBox checkBox1;
    private CheckBox checkBox2;
    private CheckBox checkBox3;
    private Timetable timetable;



    static final int TRAIN_START_TIME=0;
    static final int TRAIN_FINISH_TIME=23;
    static final int REQUEST_CHARA_MAKE=1000;
    static final int REQUEST_CHARA_EDIT=1010;
    static final int REQUEST_CHARA_SELECT=1020;
    static final int REQUEST_CHARA_DELETE=1030;
    static final int REQUEST_CHARA_EDIT_SET=1040;
    static final int REQUEST_TIMETABLE_MAKE=1050;
    static final int REQUEST_TIMETABLE_EDIT=1060;
    static final int REQUEST_TIMETABLE_DELETE=1070;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        application=(ScheduleApplication)this.getApplication();

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        findViews();
        initViews();
    }

    @Override
    protected void onStop(){
        super.onStop();
        application.saveCharaList();
        application.saveTimetableList();
    }

    //ビューを見つける
    private void findViews(){
        numberPicker1=(NumberPicker)findViewById(R.id.numberPicker1);
        numberPicker2=(NumberPicker)findViewById(R.id.numberPicker2);
        textView1=(TextView)findViewById(R.id.textView1);
        textView2=(TextView)findViewById(R.id.textView2);
        textResult1=(TextView)findViewById(R.id.textResult1);
        imageView2=(ImageView)findViewById(R.id.imageView2);
        imageView1=(ImageView)findViewById(R.id.imageView1);
        spinner=(Spinner)findViewById(R.id.spinner1);
        checkBox1=(CheckBox)findViewById(R.id.checkBox1);
        checkBox2=(CheckBox)findViewById(R.id.checkBox2);
        checkBox3=(CheckBox)findViewById(R.id.checkBox3);
        radioGroup=(RadioGroup)findViewById(R.id.radioGroup);
        radioButton1=(RadioButton)findViewById(R.id.radioButton1);
        radioButton2=(RadioButton)findViewById(R.id.radioButton2);
    }


    //各ビューの初期設定
    private void initViews() {

        numberPicker1.setMinValue(TRAIN_START_TIME);
        numberPicker1.setMaxValue(TRAIN_FINISH_TIME);
        numberPicker1.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);
        numberPicker2.setMinValue(0);
        numberPicker2.setMaxValue(59);
        numberPicker2.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);
        calendar=Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        imageView2.setImageBitmap(application.getSelectCharacter().charaImage);

        application.uploadAdapter();
        spinner.setAdapter(application.adapter);
        spinner.setSelection(application.getSelectedTimetableIndex());
        radioButton1.setChecked(true);
        timetable=application.getSelectTimetable();
        pickerTimeSet();

        ViewTreeObserver observer=imageView1.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                listenerSet();
                searchTrainTime();
                if (Build.VERSION.SDK_INT >= 16) {
                    imageView1.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    imageView1.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });


    }

    private void listenerSet(){
        //ラジオボタンの状態が変化した時に呼ばれる
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                searchTrainTime();
            }
        });

        numberPicker1.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                searchTrainTime();
            }
        });

        numberPicker2.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                searchTrainTime();
            }
        });

        checkBox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                searchTrainTime();
            }
        });

        checkBox2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                searchTrainTime();
            }
        });

        checkBox3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                searchTrainTime();
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                application.setSelectedTimetableIndex(position);
                searchTrainTime();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void searchTrainTime(){
        int hour =numberPicker1.getValue();
        int minute=numberPicker2.getValue();

        timetable=application.getSelectTimetable();

        ArrayList<Time> daiya;

        //チェックボックスから平日ダイヤか休日ダイヤか調べる
        if(radioButton1.isChecked()){
            daiya=timetable.heijituDaiya;
        }else{
            daiya=timetable.kyujituDaiya;
        }

        Iterator iterator=daiya.iterator();

        if(!checkBox1.isChecked() && !checkBox2.isChecked() && !checkBox3.isChecked()){
            textResult1.setText(insertKaigyouCode(application.getSelectCharacter().noCheckedText));
            return;
        }else if (daiya.size() == 0) {
            textResult1.setText(insertKaigyouCode(application.getSelectCharacter().noTrainText));
            return;
        }

        Time setTime=daiya.get(0);
        boolean complete=false;
        //最も近い電車の時刻を調べる
        while(iterator.hasNext()){
            Time time=(Time)iterator.next();
            if(isShuruiContent(time.shurui)) {
                if (hour == time.hour && minute <= time.minute) {
                    setTime = time;
                    complete=true;
                    break;
                } else if (hour < time.hour) {
                    setTime = time;
                    complete=true;
                    break;
                }
            }
        }

        if (complete) {
            textResult1.setText(insertKaigyouCode(replaceTrainStr(setTime)));
        }else{
            setTime=firstShuruiTime(daiya);
            if(setTime==null) {
                textResult1.setText(insertKaigyouCode(application.getSelectCharacter().noTrainText));
            }else{
                textResult1.setText(insertKaigyouCode(replaceTrainStr(setTime)));
            }
        }

    }

    //時刻を受け取り種類と時間を置き換えた文字を返す
    private String replaceTrainStr(Time time){
        String str=application.getSelectCharacter().normalText;
        str=str.replaceAll("shurui",time.getShuruiStr());
        str=str.replaceAll("time",time.hour+"時"+time.minute+"分");
        return str;
    }

    //文字列を受け取り適切な位置で改行した文字列を返す
    private String insertKaigyouCode(String str){
        StringBuffer stringBuffer=new StringBuffer(str);
        int fukidasiWidth=imageView1.getWidth();
        int mojiWidth=(int)textResult1.getTextSize();
        int gyouMojisuu=fukidasiWidth/mojiWidth-2;


        for(int i=gyouMojisuu;i<stringBuffer.length();i=i+gyouMojisuu){
            stringBuffer.insert(i++,"\n");
        }

        return stringBuffer.toString();
    }

    //種類を受け取りチェックボックスにその種類のチェックが入っているか返す
    private boolean isShuruiContent(int shurui){
        switch (shurui){
            case Time.SINKAISOKU:
                return checkBox1.isChecked();
            case Time.KAISOKU:
                return checkBox2.isChecked();
            case Time.FUTUU:
                return checkBox3.isChecked();
        }
        return false;
    }

    //ダイヤと種類を受け取り、一番最初のその種類の時刻を返す
    private Time firstShuruiTime(ArrayList<Time> daiya){
        Iterator iterator=daiya.iterator();
        while(iterator.hasNext()){
            Time time=(Time)iterator.next();
            if(isShuruiContent(time.shurui)) {
                return time;
            }
        }
        return null;
    }

    private void pickerTimeSet(){
        int time=calendar.get(Calendar.HOUR_OF_DAY);
        if(TRAIN_START_TIME <= time && time <= TRAIN_FINISH_TIME){
            numberPicker1.setValue(time);
            numberPicker2.setValue(calendar.get(Calendar.MINUTE));
        }
    }

    //アクションバーを作成
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    //メニューから選択された時の処理
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        try {

            switch (id) {
                case R.id.makeCharacter:
                    startActivityForResult(this, "io.sent.trainschedule",
                            "io.sent.trainschedule.MakeCharacterActivity",REQUEST_CHARA_MAKE);
                    break;

                case R.id.changeCharacter:
                    startActivityForResult(this,"io.sent.trainschedule",
                            "io.sent.trainschedule.SelectCharacterActivity",REQUEST_CHARA_SELECT);
                    break;

                case R.id.editCharacter:
                    startActivityForResult(this,"io.sent.trainschedule",
                            "io.sent.trainschedule.SelectCharacterActivity",REQUEST_CHARA_EDIT);
                    toast("編集したいキャラを選択してください");
                    break;

                case R.id.deleteCharacter:
                    startActivityForResult(this,"io.sent.trainschedule",
                            "io.sent.trainschedule.SelectCharacterActivity",REQUEST_CHARA_DELETE);
                    toast("削除したいキャラを選択してください");
                    break;

                case R.id.makeTimetable:
                    startActivityForResult(this, "io.sent.trainschedule",
                            "io.sent.trainschedule.MakeTimetableActivity", REQUEST_TIMETABLE_MAKE);
                    break;

                case R.id.editTimetable:
                    startActivityForResult(this,"io.sent.trainschedule",
                            "io.sent.trainschedule.SelectTimetableActivity",REQUEST_TIMETABLE_EDIT);
                    toast("編集したい時刻表を選択してください");
                    break;

                case R.id.deleteTimetable:
                    startActivityForResult(this,"io.sent.trainschedule",
                            "io.sent.trainschedule.SelectTimetableActivity",REQUEST_TIMETABLE_DELETE);
                    toast("削除したい時刻表を選択してください");
                    break;
            }
        }catch(Exception e){
            toast(e.getMessage());
        }
        return super.onOptionsItemSelected(item);
    }

    //アクティビティの起動
    private static void startActivityForResult(Activity activity,String packageName,
                                      String className,int requestCode) throws Exception{
        Intent intent=new Intent();
        intent.setComponent(new ComponentName(packageName, className));
        intent.removeCategory(Intent.CATEGORY_DEFAULT);
        activity.startActivityForResult(intent, requestCode);
    }

    //アクティビティの起動(intentあり)
    private static void startActivityForResult(Activity activity,String packageName,
                                               String className,int requestCode,Intent intent) throws Exception{
        intent.setComponent(new ComponentName(packageName, className));
        intent.removeCategory(Intent.CATEGORY_DEFAULT);
        activity.startActivityForResult(intent, requestCode);
    }

    //アクティビティから返ってくる値を受け取る
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);

        if(resultCode==RESULT_OK) {

            switch (requestCode){
                case REQUEST_CHARA_MAKE:
                    imageView2.setImageBitmap(application.getSelectCharacter().charaImage);
                    searchTrainTime();
                    break;

                case REQUEST_CHARA_SELECT:
                    application.setSelectedCharacterIndex(intent.getIntExtra("index", 0));
                    imageView2.setImageBitmap(application.getSelectCharacter().charaImage);
                    searchTrainTime();
                    break;

                case REQUEST_CHARA_EDIT:
                    startCharaEdit(intent.getIntExtra("index", 0));
                    break;

                case REQUEST_CHARA_EDIT_SET:
                    imageView2.setImageBitmap(application.getSelectCharacter().charaImage);
                    searchTrainTime();
                    break;

                case REQUEST_CHARA_DELETE:
                    charaDelete(intent.getIntExtra("index",0));
                    imageView2.setImageBitmap(application.getSelectCharacter().charaImage);
                    searchTrainTime();
                    break;

                case REQUEST_TIMETABLE_MAKE:
                    application.uploadAdapter();
                    spinner.setAdapter(application.adapter);
                    spinner.setSelection(application.getSelectedTimetableIndex());
                    searchTrainTime();
                    break;

                case REQUEST_TIMETABLE_EDIT:
                    startTimetableEdit(intent.getIntExtra("index",-1));
                    break;

                case REQUEST_TIMETABLE_DELETE:
                    timetableDelete(intent.getIntExtra("index",-1));
                    application.uploadAdapter();
                    spinner.setAdapter(application.adapter);
                    spinner.setSelection(application.getSelectedTimetableIndex());
                    searchTrainTime();
                    break;
            }


        } else if (resultCode == RESULT_CANCELED) {
            if(requestCode ==REQUEST_TIMETABLE_MAKE){
                application.uploadAdapter();
                spinner.setAdapter(application.adapter);
                spinner.setSelection(application.getSelectedTimetableIndex());
                searchTrainTime();
            }
        }

    }

    //選択された番号からキャラ編集画面を開く
    private void startCharaEdit(int editCharaIndex){
        if(editCharaIndex==0){
            toast("デフォルトキャラは編集出来ません");
            return;
        }
        Intent charaEditIntent=new Intent();
        charaEditIntent.putExtra("editCharaIndex", editCharaIndex);
        try {
            startActivityForResult(this, "io.sent.trainschedule",
                    "io.sent.trainschedule.MakeCharacterActivity", REQUEST_CHARA_EDIT_SET,charaEditIntent);
        }catch (Exception e){
            toast(e.getMessage());
        }
        return;
    }

    //選択された番号から時刻表編集画面を開く
    private void startTimetableEdit(int editTimetableIndex){
        if(editTimetableIndex==-1) return;

        Intent timetableEditIntent=new Intent();
        timetableEditIntent.putExtra("editTimetableIndex", editTimetableIndex);
        try {
            startActivityForResult(this, "io.sent.trainschedule",
                    "io.sent.trainschedule.MakeTimetableActivity", REQUEST_TIMETABLE_MAKE,timetableEditIntent);
        }catch (Exception e){
            toast(e.getMessage());
        }
        return;
    }

    //時刻表が削除可能かどうか
    private void timetableDelete(int position){
        if(application.getTimetableListSize()==1 ||position==-1){
            toast("時刻表はこれ以上削除出来ません");
            return;
        }
        application.deleteTimetable(position);
        return;
    }

    //キャラが削除可能かどうか
    private void charaDelete(int position){
        if(position==0){
            toast("デフォルトキャラは削除出来ません");
            return;
        }
        application.deleteCharacter(position);
        return;
    }


    private void toast(String text){
        if(text==null) text="";
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
