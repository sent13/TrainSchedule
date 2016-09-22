package io.sent.trainschedule;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


public class DefMakeTimetableFragment extends Fragment {

    View view;
    ScheduleApplication application;
    LayoutInflater inflater;

    private int ROW1_TITLE_WIDTH;
    private final int MP=TableLayout.LayoutParams.MATCH_PARENT;
    private final int WC=TableLayout.LayoutParams.WRAP_CONTENT;

    private Timetable timetable;
    private TextView rowText1Title;
    private TableLayout tableLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.inflater=inflater;
        view=inflater.inflate(R.layout.fragment_def_make_timetable, container, false);
        application=(ScheduleApplication)getActivity().getApplication();
        ((MakeTimetableActivity)getActivity()).setFragment();
        timetable=new Timetable();
        findViews();
        initViews();
        return view;
    }


    private void findViews(){
        rowText1Title=(TextView)view.findViewById(R.id.rowtext1_title);
    }

    private void initViews(){
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
    }

    private void makeTimeTable(){
        tableLayout = (TableLayout)view.findViewById(R.id.tableLayout);

        for(int i=0; i<24; i++){
            TableRow tableRow = (TableRow)inflater.inflate(R.layout.table_row, null);
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
}
