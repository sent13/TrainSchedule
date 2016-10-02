package io.sent.trainschedule;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;


public class SiteMakeTimetableFragment extends Fragment implements RetTimetableStrInterface{

    ScheduleApplication application;
    View view;
    EditText timetableSiteEdit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup contain,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        application=(ScheduleApplication)getActivity().getApplication();
        view=inflater.inflate(R.layout.fragment_site_make_timetable, contain, false);
        findViews();
        initViews();
        ((MakeTimetableActivity)getActivity()).setFragment();
        return view;
    }

    private void findViews(){
        timetableSiteEdit=(EditText)view.findViewById(R.id.timetable_site_edit);

    }

    private void initViews(){
        if(application.getThemeNum()!=1){
            timetableSiteEdit.setBackgroundResource(R.drawable.frame2);
        }else{
            timetableSiteEdit.setBackgroundResource(R.drawable.frame1);
        }
    }

    //エディタの文字列から時刻表の作成を行う
    private Timetable makeTimetable(){
        if(!timetableSiteEdit.getText().toString().trim().equals("")) {
            String[] daiyaStr = timetableSiteEdit.getText().toString().split("\n\n");  //ダイヤに分割
            if (daiyaStr.length != 2) {
                toast("形式が正しくありません");
                return null;
            }
            Timetable timetable = new Timetable();
            timetable.heijituDaiyaStr = daiyaStr[0];
            timetable.kyujituDaiyaStr = daiyaStr[1];

            if (!timetable.conversionStringToTime()) {
                toast("形式が正しくありません");
                return null;
            }
            return timetable;
        }else{
            return new Timetable();
        }

    }

    @Override
    public Timetable getTimetable() {
        //エディタの文字列を判定、Timetableクラスに変換し返す
        return makeTimetable();
    }

    @Override
    public void editTimetableInit(int index) {
        Timetable editTimetable=application.getTimetable(index);
        timetableSiteEdit.setText(editTimetable.heijituDaiyaStr + "\n\n" + editTimetable.kyujituDaiyaStr);
    }

    @Override
    public void drawTimetable() {
        return;
    }

    private void toast(String text){
        if(text==null) text="";
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
    }
}