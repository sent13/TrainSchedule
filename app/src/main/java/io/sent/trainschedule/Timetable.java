package io.sent.trainschedule;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by sent13 on 16/05/29.
 */
public class Timetable implements Serializable{
    String ekimei;              //駅名方面を格納
    String heijituDaiyaStr;
    String kyujituDaiyaStr;

    transient ArrayList<Time> heijituDaiya;      //平日ダイヤの配列
    transient ArrayList<Time> kyujituDaiya;      //休日ダイヤの配列

    public static final int HEIJITU=1;
    public static final int KYUJITU=2;

    public Timetable(){
        heijituDaiya=new ArrayList<>();
        kyujituDaiya=new ArrayList<>();
        heijituDaiyaStr="";
        kyujituDaiyaStr="";
    }

    public Timetable(String ekimei,ArrayList<Time> heijituDaiya,ArrayList<Time> kyujituDaiya){
        this.ekimei=ekimei;
        this.heijituDaiya=heijituDaiya;
        this.kyujituDaiya=kyujituDaiya;
        heijituDaiyaStr="";
        kyujituDaiyaStr="";
    }

    //順番に並ぶように時間を挿入する
    public void addTime(int daiya,Time time){
        ArrayList<Time> trainDaiya;
        if(daiya==HEIJITU){
            trainDaiya=heijituDaiya;
        }else{
            trainDaiya=kyujituDaiya;
        }
        Iterator iterator=trainDaiya.iterator();


        int index=0;
        boolean complete=false;
        while (iterator.hasNext()) {
            Time hikakuTime = (Time) iterator.next();
            if (time.hour == hikakuTime.hour) {      //時間が同じなら
                if (time.minute < hikakuTime.minute) {   //分が次のより小さいなら
                    trainDaiya.add(index, time);
                    complete = true;
                    break;
                }
            } else if (time.hour < hikakuTime.hour) {      //次の時間が入れたい時間より大きいなら
                trainDaiya.add(index, time);
                complete = true;
                break;
            }
            index++;
        }
        if(trainDaiya.size()==0 || complete==false) trainDaiya.add(time);
    }

    //受け取ったダイヤと時間と同じ時刻がないかどうか（あるならfalse)
    public boolean isSameTimeCheck(int daiya,Time time){
        ArrayList<Time> trainDaiya;
        if(daiya==HEIJITU){
            trainDaiya=heijituDaiya;
        }else{
            trainDaiya=kyujituDaiya;
        }
        Iterator iterator=trainDaiya.iterator();

        boolean addAble=true;
        while (iterator.hasNext()) {
            Time hikakuTime = (Time) iterator.next();
            if (time.hour == hikakuTime.hour) {      //時間と分が同じなら
                if (time.minute==hikakuTime.minute) {
                    addAble=false;
                }
            }
        }
        return addAble;
    }

    public boolean isSameTimeRemove(int daiya,Time time){
        ArrayList<Time> trainDaiya;
        int index=0;
        if(daiya==HEIJITU){
            trainDaiya=heijituDaiya;
        }else{
            trainDaiya=kyujituDaiya;
        }
        Iterator iterator=trainDaiya.iterator();

        while (iterator.hasNext()) {
            Time hikakuTime = (Time) iterator.next();
            if (time.shurui==hikakuTime.shurui && time.hour == hikakuTime.hour && time.minute==hikakuTime.minute) {      //時間と分が同じなら
                trainDaiya.remove(index);
                return true;
            }
            index++;
        }
        return false;
    }

    //ArrayList<Time>を文字列に変換する
    public void conversionTimeToString(){
        StringBuffer heijituStrBuf=new StringBuffer();
        StringBuffer kyujituStrBuf=new StringBuffer();

        Iterator heijituIterator=heijituDaiya.iterator();
        while(heijituIterator.hasNext()){
            Time heijituTime=(Time)heijituIterator.next();
            heijituStrBuf.append(heijituTime.shurui+":"+heijituTime.hour+":"+heijituTime.minute+",");
        }

        Iterator kyujituIterator=kyujituDaiya.iterator();
        while(kyujituIterator.hasNext()){
            Time kyujituTime=(Time)kyujituIterator.next();
            kyujituStrBuf.append(kyujituTime.shurui+":"+kyujituTime.hour+":"+kyujituTime.minute+",");
        }

        heijituDaiyaStr=heijituStrBuf.toString();
        kyujituDaiyaStr=kyujituStrBuf.toString();
    }

    //文字列からArrayList<Time>を復元する
    public void conversionStringToTime(){
        if(heijituDaiya==null){
            heijituDaiya=new ArrayList<>();
        }
        if(kyujituDaiya==null){
            kyujituDaiya=new ArrayList<>();
        }

        //ダイヤを表す文字列が空でないならTimeを作り出す
        if(!heijituDaiyaStr.equals("")) {
            String [] heijituStr=heijituDaiyaStr.split(",");
            for (String s : heijituStr) {
                String[] temp = s.split(":");
                heijituDaiya.add(new Time(Integer.parseInt(temp[0]),Integer.parseInt(temp[1]),
                        Integer.parseInt(temp[2])));
            }
        }

        if(!kyujituDaiyaStr.equals("")) {
            String [] kyujituStr=kyujituDaiyaStr.split(",");
            for (String s : kyujituStr) {
                String[] temp = s.split(":");
                heijituDaiya.add(new Time(Integer.parseInt(temp[0]),Integer.parseInt(temp[1]),
                        Integer.parseInt(temp[2])));

            }
        }
    }

    public void setEkimei(String name){
        ekimei=name;
    }

}
