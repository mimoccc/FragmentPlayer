/*
 * Copyright (c) 2012 MTI Ltd.
 */
package jp.co.mti.adc.growup.fragmentplayer.utils;

import java.util.ArrayList;

import jp.co.mti.adc.growup.fragmentplayer.dto.ArtistListDto;

/**
 * クラス概要<br>
 * クラス説明<br>
 */
public class ArtistPlayListUtils {

    /** シングルトン. */
    private static ArtistPlayListUtils instance = new ArtistPlayListUtils();

    /** デフォルトコンストラクタをprivateに. */
    private ArtistPlayListUtils() {
    }

    /** インスタンス取得メソッド. */
    public static ArtistPlayListUtils getInstance() {
        return instance;
    }

    /** アーティスト再生リスト. */
    private ArrayList<ArtistListDto> mList;

    public void setList(ArrayList<ArtistListDto> list) {
        mList = list;
    }

    public ArrayList<ArtistListDto> getList() {
        return mList;
    }

    /** 再生リストの再生ポジション. */
    private int mPlayNo;

    /**
     * mPlayNoを取得する<br>
     * @return mPlayNo
     */
    public int getPlayNo() {
        return mPlayNo;
    }

    /**
     * mPlayNoを設定する<br>
     * @param playNo playNo
     */
    public void setPlayNo(int playNo) {
        mPlayNo = playNo;
    }

}
