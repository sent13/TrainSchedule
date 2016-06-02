package io.sent.trainschedule;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    TextView textView1;
    TextView textView2;
    NumberPicker numberPicker1;
    NumberPicker numberPicker2;
    TextView textResult1;
    ImageView imageView1;
    ImageView imageView2;
    Calendar calendar;
    Spinner spinner;
    RadioGroup radioGroup;
    RadioButton radioButton1;
    RadioButton radioButton2;
    CheckBox checkBox1;
    CheckBox checkBox2;
    CheckBox checkBox3;
    ArrayAdapter<String> adapter;
    Timetable timetable;

    static final int TRAIN_START_TIME=5;
    static final int TRAIN_FINISH_TIME=24;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        findViews();
        initViews();
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
        imageView1.setImageResource(R.drawable.fukidasi);
        imageView2.setImageResource(R.drawable.nanaturao1);
        adapter=new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.add("長尾駅尼崎方面");
        adapter.add("尼崎駅姫路方面");
        adapter.add("余部駅姫路方面");
        spinner.setAdapter(adapter);
        radioButton1.setChecked(true);
        timetable=new Timetable();
        timetable.setEkimei("七面頰駅");
        timetable.addTime(Timetable.HEIJITU, new Time(Time.SINKAISOKU, 8, 5));
        timetable.addTime(Timetable.HEIJITU, new Time(Time.SINKAISOKU, 8, 10));
        timetable.addTime(Timetable.HEIJITU, new Time(Time.FUTUU, 8, 40));
        timetable.addTime(Timetable.HEIJITU,new Time(1,9,7));
        timetable.addTime(Timetable.HEIJITU, new Time(1, 9, 20));
        timetable.addTime(Timetable.HEIJITU, new Time(1, 10, 20));
        timetable.addTime(Timetable.HEIJITU, new Time(Time.FUTUU, 12, 50));
        timetable.addTime(Timetable.HEIJITU, new Time(1, 12, 20));
        timetable.addTime(Timetable.HEIJITU, new Time(1, 19, 10));
        timetable.addTime(Timetable.HEIJITU, new Time(1, 2, 20));

        listenerSet();

        pickerTimeSet();
        searchTrainTime();
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
    }

    private void searchTrainTime(){
        int hour =numberPicker1.getValue();
        int minute=numberPicker2.getValue();

        ArrayList<Time> daiya;

        //チェックボックスから平日ダイヤか休日ダイヤか調べる
        if(radioButton1.isChecked()){
            daiya=timetable.heijituDaiya;
        }else{
            daiya=timetable.kyujituDaiya;
        }

        Iterator iterator=daiya.iterator();

        if(!checkBox1.isChecked() && !checkBox2.isChecked() && !checkBox3.isChecked()){
            textResult1.setText("チェックがないから。分からない。");
            return;
        }else if (daiya.size() == 0) {
            textResult1.setText("次の電車は。ない。");
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
            textResult1.setText("次の"+setTime.getShuruiStr()+"電車は。" + setTime.hour + "時" + setTime.minute + "分");
        }else{
            setTime=firstShuruiTime(daiya);
            if(setTime==null) {
                textResult1.setText("この種類での次の電車はない");
            }else{
                textResult1.setText("次の"+setTime.getShuruiStr()+"電車は。" + setTime.hour + "時" + setTime.minute + "分");
            }
        }

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
                case R.id.changeCharacter:
                    setContentView(R.layout.select_character);
                    break;
                case R.id.makeCharacter:
                    startActivity(this, "io.sent.trainschedule", "io.sent.trainschedule.MakeCharacterActivity");
                    break;
                case R.id.makeTimetable:
                    startActivity(this, "io.sent.trainschedule", "io.sent.trainschedule.MakeTimetableActivity");
                    break;
            }
        }catch(Exception e){
            toast(e.getMessage());
        }
        return super.onOptionsItemSelected(item);
    }

    //アクティビティの起動
    private static void startActivity(Activity activity,String packageName,
                                      String className) throws Exception{
        Intent intent=new Intent(Intent.ACTION_MAIN);
        intent.setComponent(new ComponentName(packageName, className));
        intent.removeCategory(Intent.CATEGORY_DEFAULT);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    private void toast(String text){
        if(text==null) text="";
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }
}
