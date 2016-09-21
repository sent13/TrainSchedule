package io.sent.trainschedule;

/**
 * Created by sent13 on 16/09/20.
 */
public interface RetCharaStrInterface {
    abstract public String getCharaSerif();     //エディタが三つの場合は文字列を統合して返す、一つの場合はそのまま返す
    abstract public void editCharaInit(Character editChara);       //キャラクターを編集する場合受け取ったキャラの情報を適切に入力する
}
