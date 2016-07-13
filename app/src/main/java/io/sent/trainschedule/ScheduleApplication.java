package io.sent.trainschedule;

import android.app.Application;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by sent13 on 16/06/20.
 */
public class ScheduleApplication extends Application{

    private ArrayList<Character> charaList;
    private ArrayList<Timetable> timetableList;
    public ArrayAdapter<String> adapter;
    private int selectedCharacterIndex=0;

    @Override
    public void onCreate(){
        super.onCreate();
        adapter=new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        initViews();

    }

    private void initViews(){
        loadCharaList();
        loadTimetableList();
    }

    public ArrayList getCharaList(){
        return charaList;
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
                Timetable t=timetableList.get(i);
                t.conversionStringToTime();
                adapter.add(timetableList.get(i).ekimei);
            }
        }
    }

    //時刻表を保存する
    public void saveTimetableList(){
        DataUtil.store(this, timetableList, DataUtil.TIMETABLE_LIST_NAME);
    }

    public void addTimetable(Timetable timetable){
        timetableList.add(timetable);
    }

    public Timetable getTimetable(int index){
        return timetableList.get(index);
    }

    public int getSelectedCharacterIndex(){
        return selectedCharacterIndex;
    }

    public void setSelectedCharacterIndex(int index){
        selectedCharacterIndex=index;
    }
}

