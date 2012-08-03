package jp.co.mti.adc.growup.fragmentplayer.activity;

import java.util.ArrayList;

import jp.co.mti.adc.growup.fragmentplayer.Const;
import jp.co.mti.adc.growup.fragmentplayer.R;
import jp.co.mti.adc.growup.fragmentplayer.dto.ArtistListDto;
import jp.co.mti.adc.growup.fragmentplayer.entity.AudioEntity;
import jp.co.mti.adc.growup.fragmentplayer.fragment.PlayerFragment;
import jp.co.mti.adc.growup.fragmentplayer.listener.OnActionSearchListener;
import jp.co.mti.adc.growup.fragmentplayer.listener.OnCancelButtonListener;
import jp.co.mti.adc.growup.fragmentplayer.listener.OnLeftFragmentListViewItemListener;
import jp.co.mti.adc.growup.fragmentplayer.listener.OnListButtonListener;
import jp.co.mti.adc.growup.fragmentplayer.service.FragmentPlayerService;
import jp.co.mti.adc.growup.fragmentplayer.widget.FacebookLikePager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.EditText;
import android.widget.TextView;

/**
 * FragmentからのイベントをハンドリングするためのActivity<br>
 * @author $Author$
 * @version $Revision$
 */
public class FragmentPlayerActivity extends FragmentActivity implements OnLeftFragmentListViewItemListener, OnScrollListener, OnListButtonListener, OnCancelButtonListener, OnFocusChangeListener,
        OnActionSearchListener {

    /** TOP画面開閉処理クラス . */
    public FacebookLikePager mPager;

    /** Player画面. */
    private PlayerFragment mPlayerFragment;

    /** receiver. */
    private BroadcastReceiver receiver;

    private FragmentPlayerService mService = null;

    public ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            System.out.println("onServiceConnected2");
            mService = ((FragmentPlayerService.ServiceBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            System.out.println("onServiceDisconnected2");
            mService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainfragmentdisplay);

        // xmlからFacebookLikePagerを取得
        mPager = (FacebookLikePager) findViewById(R.id.fragment_slide_fragment_support_pager);
        mPlayerFragment = (PlayerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_slide_fragment_wrap_fragment);

        // 最初に裏のリストをクリックできないようにする
        // ClickListenerをセットしてなにもしない処理にしている
        View vb = findViewById(R.id.leftspacearea);
        vb.setVisibility(View.VISIBLE);
        vb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        receiver = new FragmentIntentReceiver();
        IntentFilter intentFilter = new IntentFilter();
        registerReceiver(receiver, intentFilter);
        // サービス起動
        Intent intent = new Intent(FragmentPlayerActivity.this, FragmentPlayerService.class);
        intent.setAction(FragmentPlayerService.ACTION_PAUSE);
        startService(intent);

        // サービスバインド
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }

    /** ブロードキャストレシーバ. */
    private class FragmentIntentReceiver extends BroadcastReceiver {

        /**
         * {@inheritDoc}
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            AudioEntity audio = new AudioEntity();
            audio.id = intent.getStringExtra(Const.ID);
            audio.albumId = intent.getStringExtra(Const.ALBUMID);
            audio.title = intent.getStringExtra(Const.TITLE);
            audio.album = intent.getStringExtra(Const.ALBUM);
            audio.artist = intent.getStringExtra(Const.ARTIST);
            mPlayerFragment.changeText(audio);
        }

    }

    /**
     * ページの開け閉め メソッド概要<br>
     * メソッド説明<br>
     */
    public void openBehind() {
        if (!mPager.isOpened()) {
            // リスト画面が閉じている場合、リスト画面を表示させる
            mPager.open();

            // ListView表示時はフィルターをはずす
            View vb = findViewById(R.id.leftspacearea);
            vb.setVisibility(View.GONE);

            // Layoutの重なり順を変更して、リスト画面をFrontにする
            View vi = findViewById(R.id.rightspacearea);
            vi.bringToFront();

            // はみ出している部分にフィルターをかけタッチしたら閉じるようにしている
            vi.setVisibility(View.VISIBLE);
            vi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentPlayerActivity.this.openBehind();
                }
            });

        } else {
            // リストが開いていた場合

            // 閉じている状態でも後ろのリストをクリックできる為フィルターをかけ
            // そのフィルターにonClickをセットし何も処理をしないようにする
            View vb = findViewById(R.id.leftspacearea);
            vb.setVisibility(View.VISIBLE);
            vb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            });

            // TOP画面のフィルターをはずす
            View vi = findViewById(R.id.rightspacearea);
            vi.setVisibility(View.GONE);

            // スクロールさせて閉じる処理を開始する
            mPager.close();
        }

    }

    @Override
    public void onListButtonTouch() {
        // PlayerFragmentのメニューボタンクリックされた場合の処理
        // openBehindメソッドを実行する
        openBehind();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v.getId() == R.id.editText1) {
            if (hasFocus) {
                mPager.fullOpen();
            }
        }
    }

    @Override
    public void onCancelButtonClick(EditText editText) {

        // ボタンを押したときにソフトキーボードを閉じる
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        editText.setFocusableInTouchMode(true);

        mPager.littleClose();
    }

    @Override
    public boolean onActionSearch(TextView v, int actionId, KeyEvent event, EditText editText) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {

            // ボタンを押したときにソフトキーボードを閉じる
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }

        editText.setFocusableInTouchMode(true);

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    }

    private boolean mScrlFlg = false;

    /**
     * {@inheritDoc}
     */
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
        case OnScrollListener.SCROLL_STATE_IDLE:
            mPager.littleClose();
            mScrlFlg = false;
            break;
        case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
            if (!mScrlFlg) {
                mPager.fullOpen();
                mScrlFlg = true;
            }
            break;
        default:
            break;
        }
    }

    /** 楽曲がクリックされたら、サービスに再生要求を送る. */
    @Override
    public void onLeftItemSelected(int position, ArrayList<ArtistListDto> artistList) {
        mService.setArtistList(artistList);
        mService.setPlayPosition(position);
        Intent intent = new Intent(FragmentPlayerActivity.this, FragmentPlayerService.class);
        intent.setAction(FragmentPlayerService.ACTION_PLAY);
        startService(intent);
        openBehind();
    }
}