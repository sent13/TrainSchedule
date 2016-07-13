package io.sent.trainschedule;

/**
 * Created by sent13 on 16/05/29.
 */
public class Time {

    int shurui;            //新快速＝１、快速＝２、普通＝３
    int hour;
    int minute;

    public static final int SINKAISOKU=1;
    public static final int KAISOKU=2;
    public static final int FUTUU=3;

    public Time(int shurui,int hour,int minute){
        this.shurui=shurui;
        this.hour=hour;
        this.minute=minute;
    }

    public String getShuruiStr(){
        if(shurui==SINKAISOKU){
            return "新快速";
        }else if(shurui==KAISOKU){
            return "快速";
        }else{
            return "普通";
        }
    }

    public String getShortShuruiStr(){
        if(shurui==SINKAISOKU){
            return "新";
        }else if(shurui==KAISOKU){
            return "快";
        }else{
            return "普";
        }
    }

}
