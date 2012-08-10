package jp.co.mti.adc.growup.fragmentplayer.utils;

/**
 * String文字列を変換するUtilityクラス<br>
 * @author $Author$
 * @version $Revision$
 */
public class StringConversionUtils {

    private static final char KATAKANA_SMALL_A = 12449;

    private static final char KATAKANA_NN = 12531;

    private static final char HIRAGANA_SMALL_A = 12353;

    private static final char HIRAGANA_NN = 12531;

    /**
     * コンストラクタ<br>
     */
    private StringConversionUtils() {
    }

    /**
     * カタカナをひらがなに変換する<br>
     * @param s String型 変換前の文字列
     * @return sb 変換後の文字列
     */
    public static String kanaToGana(String s) {
        StringBuffer sb = new StringBuffer(s);
        for (int i = 0; i < sb.length(); i++) {
            char c = sb.charAt(i);
            if (c >= KATAKANA_SMALL_A && c <= KATAKANA_NN) {
                sb.setCharAt(i, (char) (c - KATAKANA_SMALL_A + HIRAGANA_SMALL_A));
            }
        }
        return sb.toString();
    }

    /**
     * ひらがなをカタカナに変換する<br>
     * @param s String型 変換前の文字列
     * @return sb 変換後の文字列
     */
    public static String ganaToKana(String s) {
        StringBuffer sb = new StringBuffer(s);
        for (int i = 0; i < sb.length(); i++) {
            char c = sb.charAt(i);
            if (c >= HIRAGANA_SMALL_A && c <= HIRAGANA_NN) {
                sb.setCharAt(i, (char) (c - HIRAGANA_SMALL_A + KATAKANA_SMALL_A));
            }
        }
        return sb.toString();
    }

    /**
     * 半角英数字を全角に変換する<br>
     * @param value String型 変換対象の文字
     * @return value String型 変換後の文字
     */
    public static String hankakuToZenkaku(String value) {
        StringBuilder sb = new StringBuilder(value);
        for (int i = 0; i < sb.length(); i++) {
            int c = (int) sb.charAt(i);
            if ((c >= 0x30 && c <= 0x39) || (c >= 0x41 && c <= 0x5A) || (c >= 0x61 && c <= 0x7A)) {
                sb.setCharAt(i, (char) (c + 0xFEE0));
            }
        }
        value = sb.toString();
        return value;
    }

    /**
     * 全角英数字を半角に変換する<br>
     * @param value String型 変換前の文字
     * @return value String型 変換後の文字
     */
    public static String zenkakuToHankaku(String value) {
        StringBuilder sb = new StringBuilder(value);
        for (int i = 0; i < sb.length(); i++) {
            int c = (int) sb.charAt(i);
            if ((c >= 0xFF10 && c <= 0xFF19) || (c >= 0xFF21 && c <= 0xFF3A) || (c >= 0xFF41 && c <= 0xFF5A)) {
                sb.setCharAt(i, (char) (c - 0xFEE0));
            }
        }
        value = sb.toString();
        return value;
    }

}
