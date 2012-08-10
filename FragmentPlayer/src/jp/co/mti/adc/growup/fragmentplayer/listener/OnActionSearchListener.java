package jp.co.mti.adc.growup.fragmentplayer.listener;

import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;

public interface OnActionSearchListener {
    public boolean onActionSearch(TextView v, int actionId, KeyEvent event, EditText editText);

}
