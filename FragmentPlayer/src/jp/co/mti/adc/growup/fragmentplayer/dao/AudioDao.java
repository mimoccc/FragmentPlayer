package jp.co.mti.adc.growup.fragmentplayer.dao;

import java.util.ArrayList;
import java.util.List;

import jp.co.mti.adc.growup.fragmentplayer.entity.AudioEntity;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.Audio.Media;


/**
 * Audio情報取得用DAO
 * @author $Author: hozono_t $
 * @version $Revision: 18 $
 */
public class AudioDao {

    /** タグ */
    private static final String TAG = "AudioDao";

    /**
     * 楽曲ID
     */
    public static final int ID = 0;

    /**
     * アーティスト名
     */
    public static final int ARTIST = 1;

    /**
     * アルバム名
     */
    public static final int ALBUM = 2;

    /**
     * 楽曲名
     */
    public static final int TITLE = 3;

    /**
     * 楽曲の長さ
     */
    public static final int DURATION = 4;

    /**
     * アーティストID
     */
    public static final int ARTIST_ID = 5;

    /**
     * アルバムID
     */
    public static final int ALBUM_ID = 6;

    /**
     * 楽曲登録場所
     */
    public static final int DATA = 7;

    /**
     * 作曲家
     */
    public static final int COMPOSER = 8;

    /**
     * トラック
     */
    public static final int TRACK = 9;

    /**
     * カラム名一覧
     */
    public static final String[] COLUMNS = {Audio.Media._ID, Audio.Media.ARTIST, Audio.Media.ALBUM, Audio.Media.TITLE, Audio.Media.DURATION, Audio.Media.ARTIST_ID, Audio.Media.ALBUM_ID,
            Audio.Media.DATA, Audio.Media.COMPOSER, Audio.Media.TRACK };

    /**
     * コンテンツリゾルバ
     */
    private FragmentPlayerContentResolver mContentResolver;

    /**
     * 指定された引数の値を持つ新しいインスタンスを生成します。
     * @param cr ContentResolver
     */
    public AudioDao(ContentResolver cr) {
        this.mContentResolver = new FragmentPlayerContentResolver(cr) {
        };
    }

    /**
     * ID指定で楽曲を取得する
     * @param id オーディオID
     * @return 楽曲エンティティ
     */
    public AudioEntity findById(String id) {
        if (id == null) {
            return null;
        }
        Cursor c = findAudioCursorByAudioId(id);
        if (c == null) {
            return null;
        }
        try {
            List<AudioEntity> audioList = extractEntityListFromCursor(c);
            if (audioList.size() >= 1) {
                return audioList.get(0);
            } else {
                return null;
            }
        } finally {
            c.close();
        }
    }

    /**
     * カーソルから楽曲情報のリストを抽出する
     * @param c カーソル
     * @return 楽曲情報のリスト
     */
    public static ArrayList<AudioEntity> extractEntityListFromCursor(Cursor c) {
        ArrayList<AudioEntity> audioList = new ArrayList<AudioEntity>();
        while (c.moveToNext()) {
            audioList.add(extractCurrentEntityFromCursor(c));
        }
        return audioList;
    }

    /**
     * カーソルの現在位置から楽曲情報1件を取得する
     * @param c カーソル
     * @return 楽曲エンティティ
     */
    public static AudioEntity extractCurrentEntityFromCursor(Cursor c) {
        AudioEntity audio = new AudioEntity();
        audio.id = c.getString(ID);
        audio.title = c.getString(TITLE);
        audio.artistId = c.getString(ARTIST_ID);
        audio.artist = c.getString(ARTIST);
        audio.albumId = c.getString(ALBUM_ID);
        audio.album = c.getString(ALBUM);
        audio.data = c.getString(DATA);
        audio.composer = c.getString(COMPOSER);
        audio.duration = c.getString(DURATION);
        audio.track = c.getString(TRACK);
        return audio;
    }

    /**
     * オーディオIDから楽曲のカーソルを取得する
     * @param audioId オーディオID
     * @return オーディオIDに一致する楽曲を示すカーソル
     */
    public Cursor findAudioCursorByAudioId(String audioId) {
        return mContentResolver.query(Media.EXTERNAL_CONTENT_URI, COLUMNS, Audio.Media._ID + "=? AND " + Audio.Media.IS_MUSIC + "=1", new String[] {audioId }, null);
    }

    /**
     * アルバムIDから対象となる楽曲リストのカーソルを取得する
     * @param albumId アルバムID
     * @return アルバムに含まれる楽曲一覧を示すカーソル
     */
    public Cursor findAudioCursorByAlbumId(String albumId) {
        if (albumId != null) {
            return mContentResolver.query(Media.EXTERNAL_CONTENT_URI, COLUMNS, Audio.Media.ALBUM_ID + "=? AND " + Audio.Media.IS_MUSIC + "=1", new String[] {albumId }, Audio.Media.TRACK + " ASC");
        } else {
            return null;
        }
    }
}
