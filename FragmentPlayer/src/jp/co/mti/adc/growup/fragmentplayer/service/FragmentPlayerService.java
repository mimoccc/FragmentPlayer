/*
 * Copyright (c) 2012 MTI Ltd.
 */
package jp.co.mti.adc.growup.fragmentplayer.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import jp.co.mti.adc.growup.fragmentplayer.Const;
import jp.co.mti.adc.growup.fragmentplayer.R;
import jp.co.mti.adc.growup.fragmentplayer.activity.FragmentPlayerActivity;
import jp.co.mti.adc.growup.fragmentplayer.dao.AudioDao;
import jp.co.mti.adc.growup.fragmentplayer.dto.ArtistListDto;
import jp.co.mti.adc.growup.fragmentplayer.entity.AudioEntity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
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

    public static final String ACTION_PAUSEPLAY = "pauseplay";

    public static final String ACTION_NEXT = "next";

    public static final String ACTION_PREV = "prev";

    private static final int NOTIFY_ID = 1;

    /** オーディオid. */
    private String mAudioId;

    /** MediaPlayer. */
    static MediaPlayer mMediaPlayer;

    /** NotificationManager. */
    private NotificationManager mNotifyManager;

    /** notification. */
    private Notification mNotification;

    /** audio dao. */
    AudioDao mAudioDao;

    /** 再生中のaudio entity. */
    AudioEntity mAudio;

    /** 再生準備完了のフラグ. */
    public static boolean isPrepared;

    /** 再生中ポジション. */
    public static int mPlayingPosition;

    /** 再生中リスト. */
    public static ArrayList<ArtistListDto> mArtistList;

    /** シャッフル状態フラグ. */
    private static boolean isShuffle = false;

    /** シャッフル状態セッター. */
    public static void setShuffle(boolean shuffle) {
        isShuffle = shuffle;
    }

    /** シャッフル状態ゲッター. */
    public static boolean getShuffle() {
        return isShuffle;
    }

    /** リピート状態フラグ. */
    private static boolean isRepeat = false;

    /** リピート状態セッター. */
    public static void setRepeat(boolean repeat) {
        isRepeat = repeat;
        mMediaPlayer.pause();
        mMediaPlayer.setLooping(repeat);
        mMediaPlayer.start();
    }

    /** リピート状態ゲッター. */
    public static boolean getRepeat() {
        return isRepeat;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "ccccccccccccccccccccCreate!!");

        // notification初期化
        mNotifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        int icon = R.drawable.ic_launcher;
        CharSequence tickerText = "再生中";
        long when = System.currentTimeMillis();
        mNotification = new Notification(icon, tickerText, when);

        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnCompletionListener(this);
        mAudioDao = new AudioDao(getApplicationContext().getContentResolver());
        isPrepared = false;
        isShuffle = false;
        isRepeat = false;
        mArtistList = new ArrayList<ArtistListDto>();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.e(TAG, "oooooooooooooooooonStartCommand");
        String action = intent.getAction();

        // Actionで処理分岐
        if (action.equals(ACTION_PLAY)) {
            Log.v(TAG, ACTION_PLAY);
            mAudioId = mArtistList.get(mPlayingPosition).getId();
            play(mAudioId);
        } else if (action.equals(ACTION_PAUSE)) {
            Log.v(TAG, ACTION_PAUSE);
            if (mMediaPlayer.isPlaying()) {
                pause();
                sendNotification(false);
            }
        } else if (action.equals(ACTION_PAUSEPLAY)) {
            Log.v(TAG, ACTION_PAUSEPLAY);
            if (!mMediaPlayer.isPlaying() && !(mArtistList.size() == 0)) {
                mMediaPlayer.start();
                sendBroadcast2FragmentPlayerActivity();
                sendNotification(true);
            }
        } else if (action.equals(ACTION_NEXT)) {
            next();
        } else if (action.equals(ACTION_PREV)) {
            prev();
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
            Log.e(TAG, "play" + audio.data);
        } catch (IllegalArgumentException e) {
            Log.e("play error IllegalArgument", e.toString());
        } catch (IllegalStateException e) {
            Log.e("play error IllegalState", e.toString());
        } catch (IOException e) {
            Log.e("play error IOException", e.toString());
        }

        mAudio = audio;
        sendBroadcast2FragmentPlayerActivity();
        sendNotification(true);
    }

    private void sendBroadcast2FragmentPlayerActivity() {
        // ブロードキャストを投げる
        Intent intent = new Intent("jp.co.mti.adc.growup.fragmentplayer.PLAY");
        intent.putExtra(Const.ID, mAudio.id);
        intent.putExtra(Const.ALBUMID, mAudio.albumId);
        intent.putExtra(Const.TITLE, mAudio.title);
        intent.putExtra(Const.ALBUM, mAudio.album);
        intent.putExtra(Const.ARTIST, mAudio.artist);
        sendBroadcast(intent);
        Log.e(TAG, "sendBroadcast!" + mAudio.id + ":" + mAudio.albumId + ":" + mAudio.title);
    }

    private void sendNotification(boolean send) {
        if (send) {
            Context context = getApplicationContext();
            CharSequence title = mAudio.title;
            CharSequence text = mAudio.artist;
            Intent i = new Intent(FragmentPlayerService.this, FragmentPlayerActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, 0);
            mNotification.setLatestEventInfo(context, title, text, pendingIntent);
            mNotifyManager.notify(NOTIFY_ID, mNotification);
        } else if (!send) {
            mNotifyManager.cancel(NOTIFY_ID);
        }
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
            mAudioId = mArtistList.get(playNo).getId();
            play(mAudioId);
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
            mAudioId = mArtistList.get(playNo).getId();
            play(mAudioId);
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
     * 再生リストのサイズチェック.<br>
     * nullもしくはsizeが0ならサービス使用中ではない<br>
     * @return
     */
    public static boolean isServiceLiving() {
        return !(mArtistList == null || mArtistList.size() == 0);
    }

    /**
     * メディアファイルの再生が終わった時のイベント
     */
    @Override
    public void onCompletion(MediaPlayer arg0) {
        pause();
        next();
    }

    /**
     * play中かどうか
     * @return
     */
    public static boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    /** Audioのゲッター. */
    public AudioEntity getAudio() {
        return mAudio;
    }
}
