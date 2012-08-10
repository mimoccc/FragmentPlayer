package jp.co.mti.adc.growup.fragmentplayer.fragment;

import java.util.ArrayList;
import java.util.List;

import jp.co.mti.adc.growup.fragmentplayer.Const;
import jp.co.mti.adc.growup.fragmentplayer.R;
import jp.co.mti.adc.growup.fragmentplayer.dao.AudioDao;
import jp.co.mti.adc.growup.fragmentplayer.entity.AudioEntity;
import jp.co.mti.adc.growup.fragmentplayer.listener.OnListButtonListener;
import jp.co.mti.adc.growup.fragmentplayer.service.FragmentPlayerService;
import jp.co.mti.adc.growup.fragmentplayer.utils.ImageUtils;
import yanzm.products.quickaction.lib.ActionItem;
import yanzm.products.quickaction.lib.QuickAction;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.Prediction;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * TOP画面のFragment<br>
 * @author $Author$
 * @version $Revision$
 */
public class PlayerFragment extends Fragment implements OnClickListener, OnGesturePerformedListener {

    /** リストボタン用の独自ClickListener. */
    private OnListButtonListener mListButtonListener;

    /** ジャケ写の最大サイズ(単位：dip) */
    static final float ALBUM_ART_SIZE_MAX_DIP = 320.0f;

    /** ジャケ写の最大サイズ(単位：pixel) */
    static int mAlbumArtSizeMaxPixel;

    /** ジャケ写反射の最大サイズ(単位:dip) */
    static final float ALBUM_ART_REFLEC_SIZE_MAX_DIP = 20.0f;

    /** ジャケ写反射の最大サイズ(単位:pixel) */
    static int mAlbumArtRiflecSizeMaxPixel;

    /** TOP画面の楽曲タイトル表示部分のTextView. */
    private TextView mTitleView;

    /** TOP画面のアーティスト名前表示部分のTextView . */
    private TextView mArtistView;

    /** TOP画面のアルバム名表示部分のTextView . */
    private TextView mAlbumView;

    /** TOP画面のジャケ写表示部分のImageView . */
    private ImageView mJacketImage;

    /** context. */
    private Context mContext;

    /** 「曲を選択してください」. */
    private TextView mNoSelectText;

    /** タイトルなどのコンテナ. */
    private LinearLayout mTextContainer;

    /** ジェスチャーライブラリ. */
    GestureLibrary mGesLibraly;

    /** ジェスチャーオーバーレイビュー. */
    GestureOverlayView mGestures;

    /** audio dao. */
    AudioDao mAudioDao;

    /** 再生中のテキストビュー. */
    private TextView mPlayText;

    /** シャッフル中のテキストビュー. */
    private TextView mShuffleText;

    /** リピート中のテキストビュー. */
    private TextView mRepeatText;

    /** ヘルプボタン. */
    private ImageView mHelpBtn;

    /** QuickActionList/ */
    private List<ActionItem> mItemList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.playerfragment, container, false);

        ImageButton menubtn = (ImageButton) view.findViewById(R.id.menu_btn);
        menubtn.setOnClickListener(this);

        // dp -> px コンバート
        final float scale = getResources().getDisplayMetrics().density;
        mAlbumArtSizeMaxPixel = (int) (ALBUM_ART_SIZE_MAX_DIP * scale);
        mAlbumArtRiflecSizeMaxPixel = (int) (ALBUM_ART_REFLEC_SIZE_MAX_DIP * scale);

        // 各ビューの読み込み
        mTextContainer = (LinearLayout) view.findViewById(R.id.textContainer);
        mNoSelectText = (TextView) view.findViewById(R.id.noSelectionText);
        mTitleView = (TextView) view.findViewById(R.id.title);
        mArtistView = (TextView) view.findViewById(R.id.artist);
        mAlbumView = (TextView) view.findViewById(R.id.album);
        mJacketImage = (ImageView) view.findViewById(R.id.jacketImage);
        mPlayText = (TextView) view.findViewById(R.id.pause_start);
        mShuffleText = (TextView) view.findViewById(R.id.shuffleTtext);
        mRepeatText = (TextView) view.findViewById(R.id.repeatText);
        mHelpBtn = (ImageView) view.findViewById(R.id.helpbutton);
        mHelpBtn.setOnClickListener(this);

        // helpボタンの中身の初期化
        helpItemInit();

        // ジェスチャーライブラリの読み込み
        mGesLibraly = GestureLibraries.fromRawResource(getActivity().getApplicationContext(), R.raw.gesture);
        if (mGesLibraly.load()) {
            // TODO String.xmlに
            Toast.makeText(getActivity().getApplicationContext(), getString(R.string.EnableGestureText), Toast.LENGTH_LONG).show();
            mGestures = (GestureOverlayView) view.findViewById(R.id.gestures);
            mGestures.addOnGesturePerformedListener(this);
            // 単体タップの時のリスナ
            mGestures.setOnClickListener(this);
        }

        return view;
    }

    /**
     * ヘルプアイテムの初期化.
     */
    private void helpItemInit() {
        mItemList = new ArrayList<ActionItem>();

        // play item
        final ActionItem play = new ActionItem();
        play.setTitle("play / pause");
        play.setIcon(getResources().getDrawable(R.drawable.play));
        // next item
        final ActionItem next = new ActionItem();
        next.setTitle("next");
        next.setIcon(getResources().getDrawable(R.drawable.next));
        // prev item
        final ActionItem prev = new ActionItem();
        prev.setTitle("prev");
        prev.setIcon(getResources().getDrawable(R.drawable.prev));
        // repeat item
        final ActionItem repeat = new ActionItem();
        repeat.setTitle("repeat");
        repeat.setIcon(getResources().getDrawable(R.drawable.repeat));
        // shuffle item
        final ActionItem shuffle = new ActionItem();
        shuffle.setTitle("shuffle");
        shuffle.setIcon(getResources().getDrawable(R.drawable.shuffle));

        mItemList.add(play);
        mItemList.add(next);
        mItemList.add(prev);
        mItemList.add(repeat);
        mItemList.add(shuffle);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * Activityに関連付けられたときに1度だけ呼ばれる<br>
     * {@inheritDoc}
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mContext = activity;
            mListButtonListener = (OnListButtonListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnMenuButtonListener");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.menu_btn:
            mListButtonListener.onListButtonTouch();
            break;
        case R.id.gestures:// ジェスチャのタップでplay/pauseを切り替える
            if (FragmentPlayerService.isPlaying()) {
                changePlayingText();
                Log.v("PlayerFragment", "clickpause!");
                Intent intent = new Intent(mContext, FragmentPlayerService.class);
                intent.setAction(FragmentPlayerService.ACTION_PAUSE);
                mContext.startService(intent);
            } else {
                changePlayingText();
                Log.v("PlayerFragment", "clickplay!");
                Intent intent = new Intent(mContext, FragmentPlayerService.class);
                intent.setAction(FragmentPlayerService.ACTION_PAUSEPLAY);
                mContext.startService(intent);
            }
            break;
        case R.id.helpbutton: // ヘルプボタン
            // QuickActionのitemListで簡単なジェスチャリストを表示させる
            QuickAction qa = new QuickAction(mHelpBtn);
            for (ActionItem item : mItemList) {
                qa.addActionItem(item);
            }
            qa.setAnimStyle(QuickAction.ANIM_GROW_FROM_CENTER);
            qa.setLayoutStyle(QuickAction.STYLE_LIST);
            qa.setItemLayoutId(R.layout.helpitem);
            qa.show();
            break;
        default:
            break;
        }
    }

    /**
     * テキスト変更メソッド<br>
     * @param audio AudioEntity
     */
    public void changeText(AudioEntity audio) {
        Log.v("aaaaaaaaa", "change!");
        // タイトル変更処理
        mTitleView.setText(audio.title);
        // アルバム変更処理
        mAlbumView.setText(audio.album);
        // アーティスト変更処理
        mArtistView.setText(audio.artist);
        // 表示切替
        mTextContainer.setVisibility(View.VISIBLE);
        mNoSelectText.setVisibility(View.GONE);
        // ジャケ写変更処理
        if (audio.albumId != null) {
            Drawable jacketImage = ImageUtils.getCachedArtwork(mContext.getApplicationContext(), audio.albumId, mAlbumArtSizeMaxPixel, mAlbumArtSizeMaxPixel);
            if (jacketImage != null) {
                mJacketImage.setImageDrawable(jacketImage);
            } else {
                mJacketImage.setImageBitmap(ImageUtils.getDefaultAlbumArt(mContext.getApplicationContext()));
            }
        } else if (audio.albumId == null) {
            mJacketImage.setImageBitmap(ImageUtils.getDefaultAlbumArt(mContext.getApplicationContext()));
        }
    }

    /**
     * 取得したジェスチャーがジェスチャー辞書のどれともっとも一致するかを判定し 一致したactionを実行する.<br>
     * {@inheritDoc}
     */
    @Override
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
        ArrayList<Prediction> predictions = mGesLibraly.recognize(gesture);
        if (predictions.size() > 0) {
            Prediction prediction = predictions.get(0);
            // 1.0より低スコアは一致したとみなさない
            if (prediction.score > 1.0) {
                if (FragmentPlayerService.isPrepared) {
                    action(prediction.name);
                }
            }
        }
    }

    /**
     * ジェスチャーを受け取って再生停止などを行うメソッド<br>
     * @param action
     */
    public void action(String action) {
        if (action.equals(Const.SHUFFLE)) {
            if (FragmentPlayerService.getShuffle()) {
                FragmentPlayerService.setShuffle(false);
                changeVisibleShuffleText(false);
            } else {
                FragmentPlayerService.setShuffle(true);
                changeVisibleShuffleText(true);
            }
        } else if (action.equals(Const.REPEAT)) {
            if (FragmentPlayerService.getRepeat()) {
                FragmentPlayerService.setRepeat(false);
                changeVisibleRepeatText(false);
            } else {
                FragmentPlayerService.setRepeat(true);
                changeVisibleRepeatText(true);
            }
        } else if (action.equals(Const.NEXT)) {
            Log.v("PlayerFragment", "Next!");
            Intent intent = new Intent(mContext, FragmentPlayerService.class);
            intent.setAction(FragmentPlayerService.ACTION_NEXT);
            mContext.startService(intent);
        } else if (action.endsWith(Const.PREV)) {
            Log.v("PlayerFragment", "PREV!");
            Intent intent = new Intent(mContext, FragmentPlayerService.class);
            intent.setAction(FragmentPlayerService.ACTION_PREV);
            mContext.startService(intent);
        }
    }

    public void changeVisibleRepeatText(boolean flg) {
        if (flg) {
            mRepeatText.setVisibility(View.VISIBLE);
        } else {
            mRepeatText.setVisibility(View.INVISIBLE);
        }
    }

    public void changeVisibleShuffleText(boolean flg) {
        if (flg) {
            mShuffleText.setVisibility(View.VISIBLE);
        } else {
            mShuffleText.setVisibility(View.INVISIBLE);
        }
    }

    public void changePlayingText() {
        if (FragmentPlayerService.isPlaying() && FragmentPlayerService.isServiceLiving()) {
            Log.v("PlayerFragment", "change to pause");
            mPlayText.setText(R.string.PlayerPause);
        } else if (!FragmentPlayerService.isPlaying() && FragmentPlayerService.isServiceLiving()) {
            Log.v("PlayerFragment", "change to playing");
            mPlayText.setText(R.string.PlayerPlaying);
        }
    }

    public void changePlayingText(boolean playing) {
        if (playing) {
            Log.v("PlayerFragment", "change to playing");
            mPlayText.setText(R.string.PlayerPlaying);
        } else {
            Log.v("PlayerFragment", "change to pause");
            mPlayText.setText(R.string.PlayerPause);
        }
    }
}
