package io.sent.trainschedule;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.w3c.dom.Text;

public class MakeTimetableActivity extends AppCompatActivity {

    private final int ADD_TIME=1;
    private final int REMOVE_TIME=2;



    private ScheduleApplication application;


    private EditText stationNameEdit;
    private Button addOrRemoveTimeButton;
    private Button saveTimetableButton;
    private Button makeTimetableButton;
    private Button cancelTimetableButton;
    private InputMethodManager inputMethodManager;
    private AlertDialog.Builder alertDialogBuilder;
    private LayoutInflater inflater;
    private View layout;

    private Timetable timetable;
    private int editNumber;             //時刻表を上書き、もしくは書き込む場所の番号

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        application=(ScheduleApplication)this.getApplication();
        if(application.getThemeNum()!=1) setTheme(R.style.NoActionBarThemeDark);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.make_timetable);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Toolbar toolbar = (Toolbar) findViewById(R.id.make_timetable_toolbar);
        setSupportActionBar(toolbar);

        findViews();
        initViews();

    }

    //Fragment作成時のコールバック、FragmentをこのActivityから参照可能にする
    public void setFragment(){
        Fragment fragment
                =getSupportFragmentManager().findFragmentById(R.id.contain);

        btnSwitching(fragment);
    }

    //フラグメントの中身によってダイアログ作成かサイトへアクセスかを切り替える
    private void btnSwitching(Fragment fragment){
        if(fragment instanceof DefMakeTimetableFragment ==true){
            addOrRemoveTimeButton.setText("時刻を追加・\n削除");
            addOrRemoveTimeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    makeDialog();
                }
            });
        }else{
            addOrRemoveTimeButton.setText("サイトへ");
            addOrRemoveTimeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(Intent.ACTION_VIEW, Uri.
                            parse("http://www.character-jikokuhyo.com/%E6%99%82%E5%88%BB%E8%A1%A8/"));
                    startActivity(intent);
                }
            });
        }
    }

    private void findViews(){

        stationNameEdit=(EditText)findViewById(R.id.stationNameEdit);
        addOrRemoveTimeButton=(Button)findViewById(R.id.addOrRemoveTimeBtn);
        saveTimetableButton=(Button)findViewById(R.id.saveTimetableBtn);
        makeTimetableButton=(Button)findViewById(R.id.makeTimetableBtn);
        cancelTimetableButton=(Button)findViewById(R.id.cancelTimetableBtn);

        inputMethodManager=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);


        // カスタムビューを設定
        alertDialogBuilder = new AlertDialog.Builder(this);
        inflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);

    }

    private void initViews(){
        // FragmentTabHost を取得しタブ構造にする
        FragmentTabHost tabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
        tabHost.setup(this, getSupportFragmentManager(), R.id.contain);

        TabHost.TabSpec tabSpec1, tabSpec2;

        // TabSpec を生成する
        tabSpec1 = tabHost.newTabSpec("tab1");
        tabSpec1.setIndicator("最初から");
        // TabHost に追加
        tabHost.addTab(tabSpec1, DefMakeTimetableFragment.class, null);

        // TabSpec を生成する
        tabSpec2 = tabHost.newTabSpec("tab2");
        tabSpec2.setIndicator("サイトから");
        // TabHost に追加
        tabHost.addTab(tabSpec2, SiteMakeTimetableFragment.class, null);

        // リスナー登録
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                toast("タブが変わった");

            }
        });

        final Intent intent=getIntent();
        editNumber=intent.getIntExtra("editTimetableIndex",application.getTimetableListSize());

        if(editNumber==application.getTimetableListSize()){
            timetable=new Timetable();
        }else{
            timetable=application.getTimetable(editNumber);
            stationNameEdit.setText(timetable.ekimei);
        }




        stationNameEdit.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(),
                            InputMethodManager.RESULT_SHOWN);
                    return true;
                }
                return false;
            }
        });



        saveTimetableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               saveTimetable();
            }
        });

        makeTimetableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(saveTimetable()) {
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });

        //時刻表を作成せず前のアクティビティに戻る
        cancelTimetableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });


    }

    private boolean saveTimetable(){
        String name=stationNameEdit.getText().toString().trim();
        if(name.equals("")){
            toast("駅名を入力してください");
            return false;
        }else{
            timetable.setEkimei(name);
            timetable.conversionTimeToString();
            if(editNumber==application.getTimetableListSize()) {
                application.addTimetable(timetable);
                application.saveTimetableList();
                toast("保存に成功しました");
            }else{
                application.replaceTimetable(editNumber,timetable);
                application.saveTimetableList();
                toast("上書き保存に成功しました");
            }
            return true;
        }
    }




    private void makeDialog(){
        layout = inflater.inflate(R.layout.timetable_dialog, (ViewGroup)findViewById(R.id.time_layout_root));

        Button dialogRemoveTimeBtn=(Button)layout.findViewById(R.id.dialogBtn1);
        Button dialogAddTimeBtn=(Button)layout.findViewById(R.id.dialogBtn2);
        final NumberPicker dialogNumPicker1=(NumberPicker)layout.findViewById(R.id.dialogNumberPicker1);
        final NumberPicker dialogNumPicker2=(NumberPicker)layout.findViewById(R.id.dialogNumberPicker2);
        final RadioButton  dialogDaiyaRadioBtn1=(RadioButton)layout.findViewById(R.id.timeDaiyaRadioBtn1);
        final RadioButton  dialogShuruiRadioBtn1=(RadioButton)layout.findViewById(R.id.timeShuruiRadioBtn1);
        final RadioButton  dialogShuruiRadioBtn2=(RadioButton)layout.findViewById(R.id.timeShuruiRadioBtn2);
        final RadioButton  dialogShuruiRadioBtn3=(RadioButton)layout.findViewById(R.id.timeShuruiRadioBtn3);


        dialogNumPicker1.setMinValue(0);
        dialogNumPicker1.setMaxValue(23);
        dialogNumPicker1.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);
        dialogNumPicker2.setMinValue(0);
        dialogNumPicker2.setMaxValue(59);
        dialogNumPicker2.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);
        dialogDaiyaRadioBtn1.setChecked(true);
        dialogShuruiRadioBtn1.setChecked(true);
        dialogShuruiRadioBtn1.setText(application.getTrainShuruiStr(application.MOST_FAST_LONG));
        dialogShuruiRadioBtn2.setText(application.getTrainShuruiStr(application.FAST_LONG));
        dialogShuruiRadioBtn3.setText(application.getTrainShuruiStr(application.SLOW_LONG));

        alertDialogBuilder.setTitle("時刻を追加・削除");
        dialogRemoveTimeBtn.setText("削除");
        dialogAddTimeBtn.setText("追加");
        alertDialogBuilder.setView(layout);

        alertDialogBuilder.setNeutralButton("戻る", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        dialogAddTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = dialogNumPicker1.getValue();
                int minute = dialogNumPicker2.getValue();
                int daiya=getDaiya(dialogDaiyaRadioBtn1);
                int shurui=getTimeShurui(dialogShuruiRadioBtn1, dialogShuruiRadioBtn2);
                Time time = new Time(shurui, hour, minute);
                if (timetable.isSameTimeCheck(daiya, time)) {
                    timetable.addTime(daiya, time);
                } else {
                    toast("既に同じ時刻が追加されています");
                }
                //drawTimeTable();
            }
        });

        dialogRemoveTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = dialogNumPicker1.getValue();
                int minute = dialogNumPicker2.getValue();
                int daiya=getDaiya(dialogDaiyaRadioBtn1);
                int shurui=getTimeShurui(dialogShuruiRadioBtn1, dialogShuruiRadioBtn2);
                Time time = new Time(shurui, hour, minute);
                if(timetable.isSameTimeRemove(daiya,time)){
                    toast(hour+"時"+minute+"分を削除");
                }else{
                    toast("削除する時刻がありません");
                }
                //drawTimeTable();
            }
        });

        // 表示
        alertDialogBuilder.create().show();

    }

    private int getDaiya(RadioButton dialogDaiyaRadioBtn1){
        if (dialogDaiyaRadioBtn1.isChecked()) {
            return Timetable.HEIJITU;
        } else {
            return Timetable.KYUJITU;
        }
    }

    private int getTimeShurui(RadioButton dialogShuruiRadioBtn1,RadioButton dialogShuruiRadioBtn2){
        if (dialogShuruiRadioBtn1.isChecked()) {
            return Time.SINKAISOKU;
        } else if (dialogShuruiRadioBtn2.isChecked()) {
            return Time.KAISOKU;
        } else {
            return Time.FUTUU;
        }
    }

    private void toast(String text){
        if(text==null) text="";
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
