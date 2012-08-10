package jp.co.mti.adc.growup.fragmentplayer.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.HashMap;

import jp.co.mti.adc.growup.fragmentplayer.R;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

/**
 * 画像関係のユーティリティクラス
 * @author $Author: hozono_t $
 * @version $Revision: 18 $
 */
public class ImageUtils {

    /**
     * ユーティリティクラス用の非公開コンストラクタ
     */
    protected ImageUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * デフォルトのジャケ写
     */
    private static Bitmap mDefaultAlbumArt;

    /**
     * 初期化処理
     * @param context Context
     */
    public static void init(Context context) {
        mDefaultAlbumArt = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_album_art);
    }

    /** MediaStoreアルバムアートのUri */
    protected static final Uri ARTWORK_URI_MEDIASTORE = Uri.parse("content://media/external/audio/albumart");

    /** キャッシュから取得するためのBitmapFactoryオプション */
    protected static final BitmapFactory.Options BITMAP_OPTION_CACHE = new BitmapFactory.Options();

    /** タグ */
    private static final String TAG = "ImageUtils";

    /**
     * currentSize*scale<=maxSize && scale<=limitScaleとなる最大のscaleの値を返す
     * @param currentSize 現在の画像サイズ
     * @param maxSize 最大の画像サイズ
     * @param limitScale scaleの上限
     * @return 取りうる最大のscale値
     */
    public static double getMaxScale(int currentSize, int maxSize, double limitScale) {
        final double scale = (double) maxSize / currentSize;
        if (scale > limitScale) {
            return limitScale;
        } else {
            return scale;
        }
    }

    /**
     * currentSize*scale<=maxSizeとなる最大のscaleの値を返す
     * @param currentSize 現在の画像サイズ
     * @param maxSize 最大の画像サイズ
     * @return 取りうる最大のscale値
     */
    public static double getMaxScale(int currentSize, int maxSize) {
        final double scale = (double) maxSize / currentSize;
        return scale;
    }

    /**
     * キャッシュを利用してアルバムアートを読み込む
     * @param context Context
     * @param artIndex キャッシュのキー
     * @param w 読み込み後の画像の横幅
     * @param h 読み込み後の画像の高さ
     * @return 画像
     */
    public static Drawable getCachedArtwork(Context context, String artIndex, int w, int h) {
        Drawable d = null;
        // マイナス値のAlbumIdは直接Nullで返す
        if (artIndex.startsWith("-")) {
            return d;
        }
        // synchronized (sArtCache) {
        d = getImage(artIndex);
        // }
        if (d == null) {
            Bitmap b = getArtworkQuick(context, artIndex, w, h);
            if (b != null) {
                d = new FastBitmapDrawable(b);
                // synchronized (sArtCache) {
                // the cache may have changed since we checked
                Drawable value = getImage(artIndex);
                if (value == null) {
                    setImage(artIndex, d);
                } else {
                    d = value;
                }
                // }
            }
        }
        return d;
    }

    /**
     * アルバム画像を取得する。
     * @param context Context
     * @param albumArtPath アルバム画像のパス
     * @param maxWidth 画像の最大横幅
     * @param maxHeight 画像の最大高さ
     * @return アルバム画像を返す。アルバム画像が無い場合はデフォルトの画像を返す。
     */
    public static Bitmap createAlbumArt(Context context, String albumArtPath, int maxWidth, int maxHeight) {
        Bitmap bitmap = null;
        if (albumArtPath == null || albumArtPath.length() == 0) {
            bitmap = getDefaultBitmap(context, maxWidth, maxHeight, R.drawable.default_album_art);
        } else {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(albumArtPath, options);
            int scaleW = options.outWidth / maxWidth;
            int scaleH = options.outHeight / maxHeight;
            int scale = Math.max(scaleW, scaleH);
            options.inJustDecodeBounds = false;
            options.inSampleSize = scale;
            bitmap = BitmapFactory.decodeFile(albumArtPath, options);
        }
        return bitmap;
    }

    /**
     * デフォルトの画像を取得します。
     * @param context Context
     * @param maxWidth 画像の最大幅
     * @param maxHeight 画像の最大高
     * @param resourceId 画像のリソースID
     * @return 画像イメージ
     */
    public static Bitmap getDefaultBitmap(Context context, int maxWidth, int maxHeight, int resourceId) {
        Bitmap bitmap;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(), resourceId, options);
        int scaleW = options.outWidth / maxWidth;
        int scaleH = options.outHeight / maxHeight;
        int scale = Math.max(scaleW, scaleH);
        options.inJustDecodeBounds = false;
        options.inSampleSize = scale;
        bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);
        return bitmap;
    }

    /**
     * A really simple BitmapDrawable-like class, that doesn't do scaling, dithering or filtering.
     * @author $Author: hozono_t $
     * @version $Revision: 18 $
     */
    public static class FastBitmapDrawable extends Drawable {
        /** canvasに描画するbitmap */
        private Bitmap mBitmap;

        /** 表示するアートワークのImageViewの縦横幅(Pixel) */
        private int mArtworkSize;

        /**
         * コンストラクタ<br>
         * @param b canvusに描画したいbitmap
         */
        public FastBitmapDrawable(Bitmap b) {
            mBitmap = b;
            if (b.getWidth() >= b.getHeight()) {
                mArtworkSize = b.getWidth();
            } else {
                mArtworkSize = b.getHeight();
            }
        }

        @Override
        public void draw(Canvas canvas) {
            // bitmapが横長、縦長の場合、canvasの中央に表示させる。余白は透明
            if (mBitmap.getWidth() >= mBitmap.getHeight()) {
                canvas.drawBitmap(mBitmap, 0, (mArtworkSize - mBitmap.getHeight()) / 2 + 0.5f, null);
            } else {
                canvas.drawBitmap(mBitmap, (mArtworkSize - mBitmap.getWidth()) / 2 + 0.5f, 0, null);
            }
        }

        @Override
        public int getOpacity() {
            return PixelFormat.OPAQUE;
        }

        @Override
        public void setAlpha(int alpha) {
        }

        @Override
        public void setColorFilter(ColorFilter cf) {
        }
    }

    /**
     * デフォルトのジャケ写画像を取得する
     * @param context Context
     * @return デフォルトのジャケ写画像
     */
    public static Bitmap getDefaultAlbumArt(Context context) {
        if (mDefaultAlbumArt == null) {
            init(context);
        }
        return mDefaultAlbumArt;
    }

    // Get album art for specified album. This method will not try to
    // fall back to getting artwork directly from the file, nor will
    // it attempt to repair the database.
    public static Bitmap getArtworkQuick(Context context, String albumId, int w, int h) {
        // NOTE: There is in fact a 1 pixel border on the right side in the ImageView
        // used to display this drawable. Take it into account now, so we don't have to
        // scale later.
        // w -= 1;
        ContentResolver res = context.getContentResolver();
        // keyId = albumId+"_"+drmMode

        Uri uri = null;
        uri = ContentUris.withAppendedId(ARTWORK_URI_MEDIASTORE, Long.parseLong(albumId));
        if (uri != null) {
            ParcelFileDescriptor fd = null;
            try {
                fd = res.openFileDescriptor(uri, "r");
                int sampleSize = 1;

                // Compute the closest power-of-two scale factor
                // and pass that to sBitmapOptionsCache.inSampleSize, which will
                // result in faster decoding and better quality
                BITMAP_OPTION_CACHE.inJustDecodeBounds = true;
                BitmapFactory.decodeFileDescriptor(fd.getFileDescriptor(), null, BITMAP_OPTION_CACHE);

                int nextWidth = BITMAP_OPTION_CACHE.outWidth >> 1;
                int nextHeight = BITMAP_OPTION_CACHE.outHeight >> 1;
                while (nextWidth > w && nextHeight > h) {
                    sampleSize <<= 1;
                    nextWidth >>= 1;
                    nextHeight >>= 1;
                }

                BITMAP_OPTION_CACHE.inSampleSize = sampleSize;
                BITMAP_OPTION_CACHE.inJustDecodeBounds = false;
                Bitmap b = BitmapFactory.decodeFileDescriptor(fd.getFileDescriptor(), null, BITMAP_OPTION_CACHE);
                if (b != null) {
                    // finally rescale to exactly the size we need
                    if (BITMAP_OPTION_CACHE.outWidth != w || BITMAP_OPTION_CACHE.outHeight != h) {
                        if (nextWidth > w) {
                            float scale = (float) w / nextWidth;
                            nextWidth = (int) (nextWidth * scale);
                            nextHeight = (int) (nextHeight * scale);
                        } else {
                            float scale = (float) h / nextHeight;
                            nextWidth = (int) (nextWidth * scale);
                            nextHeight = (int) (nextHeight * scale);
                        }
                        Bitmap tmp = Bitmap.createScaledBitmap(b, nextWidth, nextHeight, true);
                        // Bitmap.createScaledBitmap() can return the same bitmap
                        if (tmp != b) {
                            b.recycle();
                        }
                        b = tmp;
                    }
                }
                return b;
            } catch (FileNotFoundException e) {
                // Log.e(TAG, "file not found", e);
            } finally {
                try {
                    if (fd != null) {
                        fd.close();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "can't close file", e);
                }
            }
        }
        return null;
    }

    /**
     * キャッシュの連想配列
     */
    private static HashMap<String, SoftReference<Drawable>> sArtCache = new HashMap<String, SoftReference<Drawable>>();

    /**
     * キャッシュから画像を取得する
     * @param key キャッシュのキー
     * @return 画像。キャッシュ上にキーに対応する画像が存在しない場合はnull
     */
    public static synchronized Drawable getImage(String key) {
        SoftReference<Drawable> ref = sArtCache.get(key);
        if (ref != null) {
            return ref.get();
        }
        return null;
    }

    /**
     * キャッシュに画像をセットする
     * @param key キャッシュのキー
     * @param image キャッシュにセットする画像
     */
    public static synchronized void setImage(String key, Drawable image) {
        sArtCache.put(key, new SoftReference<Drawable>(image));
    }

    /**
     * 反転画像を取得する
     * @param b 加工元画像
     * @param reflectionHeightPxcel 付加する反射画像の高さ(px) 0の場合元画像の半分の値になります
     * @return ミラー画像付きジャケ写。null対応していません。
     */
    public static Bitmap createReflectArt(Bitmap b, int reflectionHeightPxcel) {

        /** 元画像と反射画像の隙間 */
        final int reflectionGap = 0;

        int width = b.getWidth();
        int height = b.getHeight();

        // y軸反転
        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);

        // y軸反転画像をオリジナル画像の半分の高さで生成
        Bitmap reflectionImage = Bitmap.createBitmap(b, 0, height / 2, width, height / 2, matrix, false);
        // オリジナル画像＋反転画像を書き込むBitmapを用意
        if (reflectionHeightPxcel == 0) {
            Bitmap bitmapWithReflection = Bitmap.createBitmap(width, (height + height / 2), Config.ARGB_8888);
        }
        Bitmap bitmapWithReflection = Bitmap.createBitmap(width, (height + reflectionHeightPxcel), Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmapWithReflection);
        canvas.drawBitmap(b, 0, 0, null);
        Paint deafaultPaint = new Paint();
        canvas.drawRect(0, height, width, height + reflectionGap, deafaultPaint);
        canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

        Paint paint = new Paint();
        LinearGradient shader = new LinearGradient(0, b.getHeight(), 0, bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff, 0x00ffffff, TileMode.CLAMP);
        paint.setShader(shader);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.drawRect(0, height, width, bitmapWithReflection.getHeight() + reflectionGap, paint);
        return bitmapWithReflection;
    }

    /**
     * 画像をリサイズします
     * @param resizeWidth リサイズ後横幅
     * @param resizeHeight リサイズ後縦幅
     * @param b 元の画像
     * @return リサイズされた画像
     */
    public static Bitmap resizeBitmap(float resizeWidth, float resizeHeight, Bitmap b) {
        int srcWidth = b.getWidth();
        int srcHeight = b.getHeight();

        Matrix matrix = new Matrix();

        float widthScale = resizeWidth / srcWidth;
        float heightScale = resizeHeight / srcHeight;

        if (widthScale > heightScale) {
            matrix.postScale(heightScale, heightScale);
        } else {
            matrix.postScale(widthScale, widthScale);
        }
        Bitmap dst = Bitmap.createBitmap(b, 0, 0, srcWidth, srcHeight, matrix, true);

        b = null;
        return dst;
    }

}
