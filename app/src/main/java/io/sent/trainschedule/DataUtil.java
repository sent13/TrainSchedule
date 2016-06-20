package io.sent.trainschedule;

import android.content.Context;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by sent13 on 16/06/20.
 */
public class DataUtil {

    /**
            * 保存するファイル名
    */
    public final static String CHARA_LIST_NAME = "charaList.obj";
    public final static String CHARA_SELECTED_NUM_NAME="selectedChara.obj";
    public final static String TIMETABLE_LIST_NAME="timetableList.obj";

    /**
     * データを保存する
     * @param context
     * @param object 保存するオブジェクト
     */
    public static void store(Context context, Serializable object,String fileName){
        try {
            ObjectOutputStream out = new ObjectOutputStream(
                    context.openFileOutput(fileName, Context.MODE_PRIVATE));
            out.writeObject(object);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * データを読み込む
     * @param context
     * @return 保存しているデータがない場合は null
     */
    public static Object load(Context context,String fileName){
        Object retObj = null;
        try {
            ObjectInputStream in = new ObjectInputStream(
                    context.openFileInput(fileName)
            );
            retObj = in.readObject();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return retObj;
    }
}
