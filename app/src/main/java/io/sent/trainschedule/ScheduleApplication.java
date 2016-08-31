package io.sent.trainschedule;

import android.app.Application;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

/**
 * Created by sent13 on 16/06/20.
 */
public class ScheduleApplication extends Application{

    public static final int THEME_LIGHT=1;
    public static final int THEME_DARK=2;

    public static final int MOST_FAST_SHORT=0;
    public static final int FAST_SHORT=1;
    public static final int SLOW_SHORT=2;
    public static final int MOST_FAST_LONG=3;
    public static final int FAST_LONG=4;
    public static final int SLOW_LONG=5;

    private ArrayList<Character> charaList;
    private ArrayList<Timetable> timetableList;
    public ArrayAdapter<String> adapter;         //駅名のSpinnerようのアダプタ
    private int selectedCharacterIndex=0;
    private int selectedTimetableIndex=0;
    private int selectedThemeNum=THEME_LIGHT;
    private String trainSaveShuruiStr;              //電車の種類の名称を一つにまとめた文字列（保存用）

    //電車種別の略称
    private String[] shuruiStr;


    @Override
    public void onCreate(){
        super.onCreate();
        adapter=new EkimeiAdapter(this,android.R.layout.simple_spinner_item, selectedThemeNum);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        initViews();
    }

    private void initViews(){
        loadCharaList();
        loadTimetableList();
        loadThemeNum();
        loadTrainShuruiName();
    }

    public ArrayList getCharaList(){
        return charaList;
    }

    public void setTrainSaveShuruiStr(String str){
        trainSaveShuruiStr=str;
    }

    //電車の種類の名称を保存する
    public void saveTrainShuruiName(){
        DataUtil.store(this, trainSaveShuruiStr, DataUtil.TRAIN_SHURUI_NAME);
    }

    //電車の種類の名称を読み出す
    public void loadTrainShuruiName(){
        trainSaveShuruiStr=(String)DataUtil.load(this, DataUtil.TRAIN_SHURUI_NAME);

        if(trainSaveShuruiStr==null){
            trainSaveShuruiStr="新,快,普,新快速,快速,普通";
        }
        conversionStringToShurui();
    }

    //まとまった種類の文字列から個別の文字列に変換する
    public void conversionStringToShurui(){
        if(!trainSaveShuruiStr.equals("")) {
            String [] shurui=trainSaveShuruiStr.split(",");
            shuruiStr=shurui;
        }
    }

    public String getTrainShuruiStr(int index){
        return shuruiStr[index];
    }

    //キャラクターのリスト・選ばれているキャラクターの番号を保存する
    public void saveCharaList(){
        DataUtil.store(this, charaList, DataUtil.CHARA_LIST_NAME);
        DataUtil.store(this, selectedCharacterIndex, DataUtil.CHARA_SELECTED_NUM_NAME);
    }

    //キャラクターのリストと番号を読み出す
    public void loadCharaList(){
        charaList=(ArrayList)DataUtil.load(this, DataUtil.CHARA_LIST_NAME);
        Object object=DataUtil.load(this, DataUtil.CHARA_SELECTED_NUM_NAME);
        if(object!=null) selectedCharacterIndex=(int)object;

        if(charaList==null){
            charaList=new ArrayList<>();
            Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.drawable.unknown);
            String defCharaNormal="次のshurui電車の時刻はtimeです。";
            String defCharaNoTrain="次の電車はありません。";
            String defCharaNoChecked="チェックが入っていません。";
            addCharacter(new Character(bitmap, "デフォルト", defCharaNormal, defCharaNoTrain, defCharaNoChecked));
        }else{
            for(int i=0;i<charaList.size();i++){
                charaList.get(i).restorationBitmap();
            }
        }
    }

    public void saveThemeNum(){
        DataUtil.store(this, selectedThemeNum, DataUtil.THEME_SELECTED_NUM_NAME);
    }

    public void loadThemeNum(){
        Object object=DataUtil.load(this,DataUtil.THEME_SELECTED_NUM_NAME);
        if(object!=null) selectedThemeNum=(int)object;
    }

    public int getThemeNum(){
        return selectedThemeNum;
    }

    public void setThemeNum(int num){
        if(num==1){
            selectedThemeNum=THEME_LIGHT;
        }else{
            selectedThemeNum=THEME_DARK;
        }
    }

    public void setCharaList(ArrayList charaList){
        this.charaList=charaList;
    }

    public void addCharacter(Character chara){
        charaList.add(chara);
    }

    public void setCharacter(int index,Character chara){
        charaList.set(index, chara);
    }

    public void deleteCharacter(int index){
        charaList.remove(index);
        if(index<=selectedCharacterIndex){
            selectedCharacterIndex--;
        }
    }

    public int getCharaListSize(){
        return charaList.size();
    }

    public Character getCharacter(int index){
        return charaList.get(index);
    }

    public Character getSelectCharacter(){
        return charaList.get(selectedCharacterIndex);
    }

    //時刻表のリストを読み出す
    public void loadTimetableList(){
        timetableList=(ArrayList)DataUtil.load(this, DataUtil.TIMETABLE_LIST_NAME);
        Object object=DataUtil.load(this, DataUtil.TIMETABLE_SELECTED_NUM_NAME);
        if(object!=null) selectedTimetableIndex=(int)object;

        if(timetableList==null){
            timetableList=new ArrayList<>();
            String ekimei="◯◯駅◯◯方面";
            ArrayList<Time> heijituDaiya=new ArrayList<>();
            ArrayList<Time> kyujituDaiya=new ArrayList<>();
            timetableList.add(new Timetable(ekimei,heijituDaiya,kyujituDaiya));
            adapter.add(ekimei);
        }else{
            for(int i=0;i<timetableList.size();i++){
                //平日ダイヤ・休日ダイヤの文字列からarrayListに復元する
                timetableList.get(i).conversionStringToTime();
                adapter.add(timetableList.get(i).ekimei);
            }
        }
    }

    public void reloadAdapter(){
        adapter=new EkimeiAdapter(this, android.R.layout.simple_spinner_item, selectedThemeNum);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        for(int i=0;i<timetableList.size();i++){
            adapter.add(timetableList.get(i).ekimei);
        }
    }

    //時刻表と時刻表が選択されている番号を保存する
    public void saveTimetableList(){
        DataUtil.store(this, timetableList, DataUtil.TIMETABLE_LIST_NAME);
        DataUtil.store(this, selectedTimetableIndex, DataUtil.TIMETABLE_SELECTED_NUM_NAME);
    }

    public void addTimetable(Timetable timetable){
        timetableList.add(timetable);
    }

    public void replaceTimetable(int index,Timetable timetable){
        timetableList.set(index,timetable);
    }

    public void deleteTimetable(int index){
        timetableList.remove(index);
        if(index==0 && selectedTimetableIndex==0) {
            return;
        }else if(index <= selectedTimetableIndex){
            selectedTimetableIndex--;
        }
    }

    public int getTimetableListSize(){
        return timetableList.size();
    }


    public Timetable getTimetable(int index){
        return timetableList.get(index);
    }

    public Timetable getSelectTimetable(){ return timetableList.get(selectedTimetableIndex);}

    public int getSelectedCharacterIndex(){
        return selectedCharacterIndex;
    }

    public int getSelectedTimetableIndex(){
        return selectedTimetableIndex;
    }

    public void setSelectedCharacterIndex(int index){
        selectedCharacterIndex=index;
    }

    public void setSelectedTimetableIndex(int index){
        selectedTimetableIndex=index;
    }
}

