package io.sent.trainschedule;

/**
 * Created by sent13 on 16/09/23.
 */
public interface RetTimetableStrInterface {

    abstract public Timetable getTimetable();       //フラグメントの持つタイムテーブルを返す

    abstract public void editTimetableInit(int index);  //編集する場合に受け取った番号のタイムテーブルをそれぞれセット

    abstract public void drawTimetable();
}
