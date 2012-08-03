package jp.co.mti.adc.growup.fragmentplayer.widget;

import jp.co.mti.adc.growup.fragmentplayer.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.Scroller;

/**
 * TOP画面の開閉を処理するwidgetクラス<br>
 * @author $Author$
 * @version $Revision$
 */
public class FacebookLikePager extends FrameLayout {

    /** Scroller . */
    private Scroller mScroller;

    /** TOP画面の開閉フラグ . */
    private boolean mIsOpened = false;

    /** リスト画面のView . */
    private View mBehindView;

    /** TOP画面のView . */
    private View mWrapView;

    /** はみ出しているTOP画面の値. */
    public int mRightSpaceWidth;

    /**
     * コンストラクタ<br>
     * @param context Context
     */
    public FacebookLikePager(Context context) {
        this(context, (AttributeSet) null);

    }

    /**
     * コンストラクタ<br>
     * @param context
     * @param attrs
     */
    public FacebookLikePager(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray tArray = context.obtainStyledAttributes(attrs, R.styleable.FacebookLikePager);

        mRightSpaceWidth = tArray.getDimensionPixelSize(R.styleable.FacebookLikePager_rightspace, 80);
        mScroller = new Scroller(context, new AccelerateDecelerateInterpolator());
    }

    @Override
    public void computeScroll() {
        if (mWrapView == null) {
            mWrapView = findViewWithTag("Wrap");
        }

        if (mBehindView == null) {
            mBehindView = findViewWithTag("Behind");
        }

        // 現時点でのスクロール位置を計算、スクロールが継続中かどうかをbooleanで返す
        if (mScroller.computeScrollOffset()) {

            int x = mScroller.getCurrX();
            if (x > 0) {
                x = x - (getMeasuredWidth() - mRightSpaceWidth);
            }
            mWrapView.scrollTo(x, 0);
            postInvalidate();
        }
    }

    /**
     * TOP画面の状態を返す.<br>
     * @return mIsOpened boolean型 開閉の状態
     */
    public boolean isOpened() {
        return mIsOpened;
    }

    /**
     * TOP画面少し残してを開く.<br>
     */
    public void open() {
        if (mIsOpened)
            return;

        mScroller.startScroll(-1, 0, -(getMeasuredWidth() - mRightSpaceWidth) + 1, 0, 250);
        invalidate();
        mIsOpened = true;
    }

    /**
     * TOP画面をフルに開く<br>
     */
    public void fullOpen() {
        if (!mIsOpened) {
            return;
        }

        mScroller.startScroll(-(getMeasuredWidth() - mRightSpaceWidth), 0, -mRightSpaceWidth, 0, 250);
        invalidate();
        mIsOpened = true;
    }

    /**
     * TOP画面を少しだけ閉じる<br>
     */
    public void littleClose() {
        if (!mIsOpened) {
            return;
        }
        mScroller.startScroll(-getMeasuredWidth(), 0, mRightSpaceWidth, 0, 250);
        invalidate();
        mIsOpened = true;
    }

    /**
     * TOP画面フルを閉じる.<br>
     */
    public void close() {
        if (!mIsOpened) {
            return;
        }

        mScroller.startScroll(1, 0, (getMeasuredWidth() - mRightSpaceWidth) - 1, 0, 250);
        invalidate();
        mIsOpened = false;
    }
}
