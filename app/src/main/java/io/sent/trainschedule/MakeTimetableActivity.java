package io.sent.trainschedule;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class MakeTimetableActivity extends AppCompatActivity {

    private final int MP=TableLayout.LayoutParams.MATCH_PARENT;
    private final int WC=TableLayout.LayoutParams.WRAP_CONTENT;
    private int ROW1_TITLE_WIDTH;

    private ScheduleApplication application;

    private TextView rowText1Title;

    private EditText stationNameEdit;
    private Button addTimeButton;
    private Button removeTimeButton;
    private Button makeTimetableButton;
    private Button cancelTimetableButton;
    private InputMethodManager inputMethodManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.make_timetable);
        application=(ScheduleApplication)this.getApplication();
        Toolbar toolbar = (Toolbar) findViewById(R.id.make_timetable_toolbar);
        setSupportActionBar(toolbar);

        findViews();
        initViews();

    }

    private void findViews(){
        rowText1Title=(TextView)findViewById(R.id.rowtext1_title);
        stationNameEdit=(EditText)findViewById(R.id.stationNameEdit);
        addTimeButton=(Button)findViewById(R.id.addTimeBtn);
        removeTimeButton=(Button)findViewById(R.id.removeTimeBtn);
        makeTimetableButton=(Button)findViewById(R.id.makeTimetableBtn);
        cancelTimetableButton=(Button)findViewById(R.id.cancelTimetableBtn);

        inputMethodManager=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    private void initViews(){
        Intent intent=getIntent();


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
                if((event.getAction()==KeyEvent.ACTION_DOWN) && (keyCode==KeyEvent.KEYCODE_ENTER)){
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(),
                            InputMethodManager.RESULT_SHOWN);
                    return true;
                }
                return false;
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

    private void makeTimeTable(){
        TableLayout tableLayout = (TableLayout)findViewById(R.id.tableLayout);

        for(int i=0; i<25; i++){
            TableRow tableRow = (TableRow)getLayoutInflater().inflate(R.layout.table_row, null);
            TextView rowHour = (TextView)tableRow.findViewById(R.id.rowtext1);
            rowHour.setText("" + i);
            rowHour.setWidth(ROW1_TITLE_WIDTH);
            TextView rowHeijitu=(TextView)tableRow.findViewById(R.id.rowtext2);
            rowHeijitu.setText("" + i + " " + (i + 1) + " " + (i + 2) + " " + (i + 3) + " " + (i + 4) + " " + (i + 5) + " " + (i + 6) + " " + (i + 7) + " " + (i + 8));
            TextView rowKyujitu=(TextView)tableRow.findViewById(R.id.rowtext3);
            rowKyujitu.setText("" + i + " " + (i + 1) + " " + (i + 2) + " " + (i + 3) + " " + (i + 4) + " " + (i + 5) + " アオ" + (i + 6));

            if((i+1)%2 == 0){
                int color = getResources().getColor(R.color.lightgrey);
                rowHour.setBackgroundColor(color);
                rowHeijitu.setBackgroundColor(color);
                rowKyujitu.setBackgroundColor(color);
            }

            tableLayout.addView(tableRow, new TableLayout.LayoutParams(MP, WC));
        }

    }
}
