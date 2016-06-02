package io.sent.trainschedule;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by sent13 on 16/05/29.
 */
public class Timetable {
    String ekimei;              //駅名方面を格納
    ArrayList<Time> heijituDaiya;      //平日ダイヤの配列
    ArrayList<Time> kyujituDaiya;      //休日ダイヤの配列

    public static final int HEIJITU=1;
    public static final int KYUJITU=2;

    public Timetable(){
        heijituDaiya=new ArrayList<>();
        kyujituDaiya=new ArrayList<>();
    }

    public void addTime(int daiya,Time time){           //時間を追加
        if(daiya==HEIJITU){
            searchTime(heijituDaiya,time);
        }else if(daiya==KYUJITU){
            searchTime(kyujituDaiya,time);
        }
    }

    //順番に並ぶように時間を挿入する
    private void searchTime(ArrayList<Time> trainDaiya,Time time){
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

    public void setEkimei(String name){
        ekimei=name;
    }

}
