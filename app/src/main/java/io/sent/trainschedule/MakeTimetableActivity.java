package io.sent.trainschedule;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
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
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.w3c.dom.Text;

public class MakeTimetableActivity extends AppCompatActivity {

    private final int ADD_TIME=1;
    private final int REMOVE_TIME=2;
    private final int MP=TableLayout.LayoutParams.MATCH_PARENT;
    private final int WC=TableLayout.LayoutParams.WRAP_CONTENT;
    private int ROW1_TITLE_WIDTH;

    private ScheduleApplication application;

    private TextView rowText1Title;

    private EditText stationNameEdit;
    private Button addOrRemoveTimeButton;
    private Button saveTimetableButton;
    private Button makeTimetableButton;
    private Button cancelTimetableButton;
    private TableLayout tableLayout;
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

    private void findViews(){
        rowText1Title=(TextView)findViewById(R.id.rowtext1_title);
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
        final Intent intent=getIntent();
        editNumber=intent.getIntExtra("editTimetableIndex",application.getTimetableListSize());

        if(editNumber==application.getTimetableListSize()){
            timetable=new Timetable();
        }else{
            timetable=application.getTimetable(editNumber);
            stationNameEdit.setText(timetable.ekimei);
        }


        ViewTreeObserver observer = rowText1Title.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ROW1_TITLE_WIDTH=rowText1Title.getWidth();
                makeTimeTable();
                if (Build.VERSION.SDK_INT >= 16) {
                    rowText1Title.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    rowText1Title.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });

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

        addOrRemoveTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeDialog();
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
    private void makeTimeTable(){
        tableLayout = (TableLayout)findViewById(R.id.tableLayout);

        for(int i=0; i<24; i++){
            TableRow tableRow = (TableRow)getLayoutInflater().inflate(R.layout.table_row, null);
            TextView rowHour = (TextView)tableRow.findViewById(R.id.rowtext1);
            rowHour.setText("" + i);
            rowHour.setWidth(ROW1_TITLE_WIDTH);
            TextView rowHeijitu=(TextView)tableRow.findViewById(R.id.rowtext2);
            rowHeijitu.setText("");
            TextView rowKyujitu=(TextView)tableRow.findViewById(R.id.rowtext3);
            rowKyujitu.setText("");


            if((i+1)%2 == 0){
                int color=getResources().getColor(R.color.lightgrey);
                if(application.getThemeNum()!=1){
                    color= getResources().getColor(R.color.black2);
                }
                rowHour.setBackgroundColor(color);
                rowHeijitu.setBackgroundColor(color);
                rowKyujitu.setBackgroundColor(color);
            }else{
                if(application.getThemeNum()!=1){
                    int color= getResources().getColor(R.color.main_color);
                    rowHour.setBackgroundColor(color);
                    rowHeijitu.setBackgroundColor(color);
                    rowKyujitu.setBackgroundColor(color);
                }
            }

            tableLayout.addView(tableRow, new TableLayout.LayoutParams(MP, WC));
        }
        drawTimeTable();
    }

    private void drawTimeTable(){

        //行の数だけ繰り返す
        for(int i=0;i<tableLayout.getChildCount();i++){
            TableRow tableRow=(TableRow)tableLayout.getChildAt(i);
            TextView rowHeijitu=(TextView)tableRow.findViewById(R.id.rowtext2);
            TextView rowKyujitu=(TextView)tableRow.findViewById(R.id.rowtext3);

            StringBuffer strHeijitu=new StringBuffer();
            StringBuffer strKyujitu=new StringBuffer();

            //平日ダイヤの数だけ繰り返す
            for(int j=0;j<timetable.heijituDaiya.size();j++){
                Time heijituTime=timetable.heijituDaiya.get(j);
                if(i==heijituTime.hour){
                    strHeijitu.append(application.getTrainShuruiStr(heijituTime.getShuruiNum())
                            +String.format("%02d  ",heijituTime.minute));
                }
            }

            //休日ダイヤの数だけ繰り返す
            for(int j=0;j<timetable.kyujituDaiya.size();j++){
                Time kyujituTime=timetable.kyujituDaiya.get(j);
                if(i==kyujituTime.hour){
                    strKyujitu.append(application.getTrainShuruiStr(kyujituTime.getShuruiNum())
                            +String.format("%02d  ",kyujituTime.minute));
                }
            }

            rowHeijitu.setText(strHeijitu);
            rowKyujitu.setText(strKyujitu);

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
                drawTimeTable();
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
                drawTimeTable();
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
