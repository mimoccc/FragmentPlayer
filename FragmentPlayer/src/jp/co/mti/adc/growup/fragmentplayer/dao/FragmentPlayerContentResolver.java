package jp.co.mti.adc.growup.fragmentplayer.dao;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

/**
 * コンテンツリゾルバのラッパー
 * @author $Author: hozono_t $
 * @version $Revision: 18 $
 */
public class FragmentPlayerContentResolver {
    /** コンテンツリゾルバ */
    private ContentResolver cr;

    /**
     * コンストラクタ<br>
     * @param contentresolver コンテンツリゾルバ
     */
    public FragmentPlayerContentResolver(ContentResolver contentresolver) {
        this.cr = contentresolver;
    }

    /**
     * query<br>
     * @param uri uri
     * @param projection projection
     * @param selection selection
     * @param selectionArgs selectionArgs
     * @param sortOrder sortOrder
     * @return Cursor
     */
    public final Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return cr.query(uri, projection, selection, selectionArgs, sortOrder);
    }

    /**
     * delete<br>
     * @param url url
     * @param where where
     * @param selectionArgs selectionArgs
     * @return int
     */
    public final int delete(Uri url, String where, String[] selectionArgs) {
        return cr.delete(url, where, selectionArgs);
    }

    /**
     * insert<br>
     * @param url url
     * @param values values
     * @return Uri
     */
    public final Uri insert(Uri url, ContentValues values) {
        return cr.insert(url, values);
    }

    /**
     * update<br>
     * @param uri uri
     * @param values values
     * @param where where
     * @param selectionArgs selectionArgs
     * @return int
     */
    public final int update(Uri uri, ContentValues values, String where, String[] selectionArgs) {
        return cr.update(uri, values, where, selectionArgs);
    }

    /**
     * bulkInsert<br>
     * @param url url
     * @param values valuesの配列
     * @return int
     */
    public final int bulkInsert(Uri url, ContentValues[] values) {
        return cr.bulkInsert(url, values);
    }

    /**
     * コンテンツリゾルバの取得<br>
     * @return ContentResolver
     */
    public ContentResolver getContentResolver() {
        return cr;
    }
}
