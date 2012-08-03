package jp.co.mti.adc.growup.fragmentplayer.listener;

import java.util.ArrayList;

import jp.co.mti.adc.growup.fragmentplayer.dto.ArtistListDto;

/**
 * LeftFragmentのListViewクリック用の独自ListenerInterface<br>
 * @author $Author$
 * @version $Revision$
 */
public interface OnLeftFragmentListViewItemListener {
    public void onLeftItemSelected(int position, ArrayList<ArtistListDto> artistList);
}
