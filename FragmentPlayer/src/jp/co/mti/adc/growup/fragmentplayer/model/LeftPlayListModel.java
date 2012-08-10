package jp.co.mti.adc.growup.fragmentplayer.model;

import java.util.ArrayList;

import jp.co.mti.adc.growup.fragmentplayer.R;
import jp.co.mti.adc.growup.fragmentplayer.dto.ArtistListDto;
import jp.co.mti.adc.growup.fragmentplayer.utils.StringConversionUtils;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

/**
 * LeftPlayListFragmentのロジック部分を処理するModelクラス.<br>
 * @author $Author$
 * @version $Revision$
 */
public class LeftPlayListModel {

    /**
     * コンストラクタ.<br>
     */
    public LeftPlayListModel() {

    }

    /**
     * アーティスト名取得用のCursorを返す.<br>
     * @param context Context型
     * @return cursor アーティスト名取得用のCursor
     */
    public final Cursor getCursor(Context context) {

        // ContentResolverの取得
        ContentResolver resolver = context.getContentResolver();

        // 取得したContentResolverを利用して、Cursorに情報をセットする
        // @formatter:off
        Cursor cursor = resolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[] {
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ALBUM_ID
        }, null, null, null);
        // @formatter:on

        return cursor;
    }

    /**
     * 指定された文字列からアーティスト名をLike検索する<br>
     * メソッド説明<br>
     * @param context Context型
     * @param text String型 検索文字列
     * @return cursor アーティスト名取得用のCursor
     */
    public final Cursor getSearchCursor(Context context, String text) {

        // 「％」の文字を取得
        String percent = context.getString(R.string.Percent);

        // 検索文字列がカタカナだった場合、ひらがなに変換する
        String searchTextGana = percent + StringConversionUtils.kanaToGana(text) + percent;

        // 検索文字列がひらがなだった場合、カタカナに変換する
        // カタカナをカタカナに変換するとおかしくなるので一回ひらがなにしたものを変換する
        String searchTextKana = percent + StringConversionUtils.ganaToKana(searchTextGana) + percent;

        // 全角英数字を半角に変換
        String searchTextHan = StringConversionUtils.zenkakuToHankaku(text);

        // 半角英数字を全角に変換
        String searchTextZen = StringConversionUtils.hankakuToZenkaku(text);

        // ContentResolverの取得
        ContentResolver resolver = context.getContentResolver();

        // 取得したContentResolverを利用して、Cursorに情報をセットする
        // @formatter:off
        Cursor cursor = resolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[] {
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ALBUM_ID },
                MediaStore.Audio.Media.ARTIST + " like ? or " +
                MediaStore.Audio.Media.ARTIST + " like ? or " +
                MediaStore.Audio.Media.ARTIST + " like ? or " +
                MediaStore.Audio.Media.ARTIST + " like ? or " +
                MediaStore.Audio.Media.ALBUM  + " like ? or " +
                MediaStore.Audio.Media.ALBUM  + " like ? or " +
                MediaStore.Audio.Media.ALBUM  + " like ? or " +
                MediaStore.Audio.Media.ALBUM  + " like ? or " +
                MediaStore.Audio.Media.TITLE + " like ? or " +
                MediaStore.Audio.Media.TITLE + " like ? or " +
                MediaStore.Audio.Media.TITLE + " like ? or " +
                MediaStore.Audio.Media.TITLE + " like ?",
        new String[] {
                searchTextKana,
                searchTextGana,
                searchTextHan,
                searchTextZen,
                searchTextKana,
                searchTextGana,
                searchTextHan,
                searchTextZen,
                searchTextKana,
                searchTextGana,
                searchTextHan,
                searchTextZen
                },  null);
        // @formatter:on

        return cursor;
    }

    /**
     * アーティスト名取得後のArtistListDtoのListを返す.<br>
     * @param context Context
     * @param cursor アーティスト名取得用のCursor
     * @return artists ArtistListDtoのArrayList
     */
    public final ArrayList<ArtistListDto> getList(Context context, Cursor cursor) {
        ArtistListDto artistListDto;
        ArrayList<ArtistListDto> artists = new ArrayList<ArtistListDto>();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                artistListDto = new ArtistListDto();
                artistListDto.setId(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));
                artistListDto.setArtistName(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
                artistListDto.setAlbumId(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));
                artistListDto.setAlbumName(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)));
                artistListDto.setSongName(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
                artists.add(artistListDto);
            }
            cursor.close();
        }
        return artists;
    }
}
