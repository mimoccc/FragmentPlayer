/*
 * Copyright (c) 2012 MTI Ltd.
 */
package jp.co.mti.adc.growup.fragmentplayer.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import jp.co.mti.adc.growup.fragmentplayer.R;
import jp.co.mti.adc.growup.fragmentplayer.dao.AudioDao;
import jp.co.mti.adc.growup.fragmentplayer.dto.ArtistListDto;
import jp.co.mti.adc.growup.fragmentplayer.entity.AudioEntity;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * クラス概要<br>
 * クラス説明<br>
 */
public class FragmentPlayerService extends Service implements OnCompletionListener {

    private static final String TAG = "FragmentPlayerService";

    public static final String ACTION_PLAY = "play";

    public static final String ACTION_PAUSE = "pause";

    /** オーディオid. */
    private String mAudioId;

    /** MediaPlayer. */
    static MediaPlayer mMediaPlayer;

    /** NotificationManager. */
    private NotificationManager mNotify;

    /** audio dao. */
    AudioDao mAudioDao;

    /** 再生準備完了のフラグ. */
    public static boolean isPrepared;

    /** 再生中ポジション. */
    public static int mPlayingPosition;

    /** 再生中リスト. */
    public static ArrayList<ArtistListDto> mArtistList;

    /** シャッフルフラグ. */
    private static boolean isShuffle = false;

    public static void setShuffle(boolean shuffle) {
        isShuffle = shuffle;
    }

    public static boolean getShuffle() {
        return isShuffle;
    }

    /** リピートフラグ. */
    private static boolean isRepeat = false;

    public static void setRepeat(boolean repeat) {
        isRepeat = repeat;
        mMediaPlayer.pause();
        mMediaPlayer.setLooping(repeat);
        mMediaPlayer.start();
    }

    public static boolean getRepeat() {
        return isRepeat;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "ccccccccccccccccccccCreate!!");
        mNotify = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnCompletionListener(this);
        mAudioDao = new AudioDao(getApplicationContext().getContentResolver());
        isPrepared = false;
        isShuffle = false;
        isRepeat = false;
        mArtistList = new ArrayList<ArtistListDto>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.e(TAG, "oooooooooooooooooonStartCommand");
        String action = intent.getAction();
        if (action.equals(ACTION_PLAY)) {
            Log.e(TAG, ACTION_PLAY);
            mAudioId = mArtistList.get(mPlayingPosition).getId();
            play(mAudioId);
        }
        return START_NOT_STICKY;
    }

    private IBinder binder = new ServiceBinder();

    public class ServiceBinder extends Binder {
        public FragmentPlayerService getService() {
            return FragmentPlayerService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "onBind");
        // TODO Bindされたらそのとき再生している楽曲に関する情報
        return binder;
    }

    /**
     * 再生処理
     * @param productId
     * @param albumId
     */
    public void play(String audioId) {
        if (audioId == null || audioId == "") {
            Log.e(TAG, "play bat not find audioID");
            Toast.makeText(getApplicationContext(), getString(R.string.SelectMusic), Toast.LENGTH_SHORT).show();
            return;
        }
        mAudioId = audioId;
        AudioEntity audio = mAudioDao.findById(audioId);
        // 再生処理
        try {
            isPrepared = false;
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(audio.data);
            mMediaPlayer.prepare();
            isPrepared = true;
            mMediaPlayer.start();
        } catch (IllegalArgumentException e) {
            Log.e("play error IllegalArgument", e.toString());
        } catch (IllegalStateException e) {
            Log.e("play error IllegalState", e.toString());
        } catch (IOException e) {
            Log.e("play error IOException", e.toString());
        }

        // TODO ブロードキャストを投げる
        Intent intent = new Intent();

        sendBroadcast(intent);
    }

    /**
     * 停止処理
     */
    private void pause() {
        // 一時停止処理
        mMediaPlayer.pause();
    }

    /** 次の曲 */
    private void next() {
        if (mPlayingPosition != mArtistList.size() - 1) {
            int playNo = mPlayingPosition + 1;
            // シャッフル再生の場合
            if (isShuffle) {
                Random rnd = new Random();
                playNo = rnd.nextInt(mArtistList.size());
            } else {
                mPlayingPosition++;
            }
            play(mArtistList.get(playNo).getId());
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.NoSongs), Toast.LENGTH_SHORT).show();
        }
    }

    /** 前の曲 */
    private void prev() {
        if (mPlayingPosition != 0) {
            int playNo = mPlayingPosition - 1;
            // シャッフル再生の場合
            if (isShuffle) {
                Random rnd = new Random();
                playNo = rnd.nextInt(mArtistList.size());
            } else {
                mPlayingPosition--;
            }
            play(mArtistList.get(playNo).getId());
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.NoSongs), Toast.LENGTH_SHORT).show();
        }
    }

    /** 再生リストをセットする. */
    public void setArtistList(ArrayList<ArtistListDto> artistList) {
        mArtistList = artistList;
    }

    /** 再生ポジションをセットする. */
    public void setPlayPosition(int position) {
        mPlayingPosition = position;
        Log.e(TAG, Integer.toString(mPlayingPosition));
    }

    /**
     * メディアファイルの再生が終わった時のイベント
     */
    @Override
    public void onCompletion(MediaPlayer arg0) {
        pause();
        next();
    }
}
