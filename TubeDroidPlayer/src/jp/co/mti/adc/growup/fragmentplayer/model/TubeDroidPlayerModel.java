package jp.co.mti.adc.growup.fragmentplayer.model;

import jp.co.mti.adc.growup.fragmentplayer.Const.State;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;


/**
 * TubeDroidPlayerActivityのロジック部分を処理するModelクラス.<br>
 * @author $Author$
 * @version $Revision$
 */
public class TubeDroidPlayerModel {

    /**
     * コンストラクタ.<br>
     */
    public TubeDroidPlayerModel() {

    }

    /**
     * 押されたラジオボタンを判別し、現在のステータスを返す.<br>
     * @param checkedId 押されたラジオボタンのID
     * @return state 現在のステータス
     */
    public State changeState(int checkedId) {
        State state = null;

        return state;
    }

    /**
     * 選択された色名からカラーコードを返す.<br>
     * @param name 選択された色名
     * @return color カラーコード
     */
    public int getColorCode(String name) {
        int color = 0;
        if (name.equals("WHITE")) {
            color = Color.WHITE;

        } else if (name.equals("RED")) {
            color = Color.RED;

        } else if (name.equals("GREEN")) {
            color = Color.GREEN;

        } else if (name.equals("BLACK")) {

            color = Color.BLACK;

        } else if (name.equals("BLUE")) {
            color = Color.BLUE;

        } else if (name.equals("YELLOW")) {
            color = Color.YELLOW;
        }

        return color;
    }

    public void test(Context context, String name, String backColor, String textColor) {
        SharedPreferences pref = context.getSharedPreferences("TEST", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("NAME", name);
        editor.putString("BACKCOLOR", backColor);
        editor.putString("TEXTCOLOR", textColor);
        editor.commit();

    }

}
