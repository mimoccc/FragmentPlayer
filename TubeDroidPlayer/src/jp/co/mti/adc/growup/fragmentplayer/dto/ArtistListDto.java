package jp.co.mti.adc.growup.fragmentplayer.dto;

/**
 * アーティスト情報のDTOクラス.<br>
 * @author $Author$
 * @version $Revision$
 */
public class ArtistListDto {

    /** アーティスト名. */
    private String mArtistName;

    /** 曲名。 */
    private String mSongName;

    /** アルバム名. */
    private String mAlbumName;

    /** ID . */
    private String mId;

    /** アルバム画像のパス. */
    private String mAlbumId;

    /**
     * コンストラクタ.<br>
     */
    public ArtistListDto() {

    }

    /**
     * mArtistNameのゲッター.<br>
     * @return mArtistName String型 アーティスト名
     */
    public final String getArtistName() {
        return mArtistName;
    }

    /**
     * mArtistNameのセッター.<br>
     * @param artistName String型 アーティスト名
     */
    public final void setArtistName(String artistName) {
        this.mArtistName = artistName;
    }

    /**
     * mSongNameのゲッター.<br>
     * @return mSongName String型 曲名
     */
    public String getSongName() {
        return mSongName;
    }

    /**
     * mSongNameのセッター.<br>
     * @param songName String型 曲名
     */
    public void setSongName(String songName) {
        this.mSongName = songName;
    }

    /**
     * mAlbumNameのゲッター<br>
     * @return mAlbumName String型 アルバム名
     */
    public String getAlbumName() {
        return mAlbumName;
    }

    /**
     * mAlbumNameのセッター.<br>
     * @param albumName String型 アルバム名
     */
    public void setAlbumName(String albumName) {
        this.mAlbumName = albumName;
    }

    /**
     * mIdのゲッター.<br>
     * @return mId String型 ID
     */
    public final String getId() {
        return mId;
    }

    /**
     * mIdのセッター.<br>
     * @param id String型 ID
     */
    public final void setId(String id) {
        this.mId = id;
    }

    /**
     * mAlbumArtのゲッター.<br>
     * @return mAlbumArt String型 アルバム画像のパス
     */
    public String getAlbumId() {
        return mAlbumId;
    }

    /**
     * mAlbumArtのセッター.<br>
     * @param albumArt String型 アルバム画像のパス
     */
    public void setAlbumId(String albumId) {
        this.mAlbumId = albumId;
    }

}
