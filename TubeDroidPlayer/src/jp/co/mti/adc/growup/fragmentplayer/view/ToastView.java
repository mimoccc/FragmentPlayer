package jp.co.mti.adc.growup.fragmentplayer.view;

import android.content.Context;
import android.widget.Toast;

/**
 * Toastを表示する為のViewクラス.<br>
 * @author $Author$
 * @version $Revision$
 */
public class ToastView {

    /**
     * コンストラクタ<br>
     */
    private ToastView() {

    }

    /**
     * 指定のメッセージでToastを表示させる.<br>
     * @param context Context
     * @param message String型 Toastに表示させるメッセージ
     */
    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
