package jp.co.mti.adc.growup.fragmentplayer;

/**
 * 定数クラス.<br>
 * @author $Author$
 * @version $Revision$
 */
public class Const {

    /**
     * コンストラクタ<br>
     */
    private Const() {

    }

    /**
     * ラジオボタンの状態を表すenumクラス<br>
     * @author $Author$
     * @version $Revision$
     */
    public static enum State {
        NAME, BACKGROUND, TEXT
    }

    /** シャッフルを表す文字列. */
    public final static String SHUFFLE = "shuffle";

    /** リピートを表す文字列. */
    public final static String REPEAT = "repeat";

    /** 次の曲を表す文字列. */
    public final static String NEXT = "next";

    /** 前の曲を表す文字列. */
    public final static String PREV = "prev";

    /** titleあらわす文字列. */
    public final static String TITLE = "title";

    /** artistあらわす文字列. */
    public final static String ARTIST = "artist";

    /** albumあらわす文字列. */
    public final static String ALBUM = "album";

    /** albumIDあらわす文字列. */
    public final static String ALBUMID = "albumid";

    /** mIdをあらわす文字列. */
    public final static String ID = "productid";

}
