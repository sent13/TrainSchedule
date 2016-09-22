package io.sent.trainschedule;

/**
 * Created by sent13 on 16/09/23.
 */
public interface RetTimetableStrInterface {

    abstract public void setEditTimetable();        //defの時は受け取った番号の時刻表をそのままセットし表示、
                                                    //siteの時は受け取った番号の時刻表を文字列に直しエディタにセット

}
