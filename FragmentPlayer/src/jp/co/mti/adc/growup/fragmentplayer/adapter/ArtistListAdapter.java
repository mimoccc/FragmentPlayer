package jp.co.mti.adc.growup.fragmentplayer.adapter;

import java.util.ArrayList;

import jp.co.mti.adc.growup.fragmentplayer.R;
import jp.co.mti.adc.growup.fragmentplayer.dto.ArtistListDto;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * アーティスト名をList表示するAdapterクラス.<br>
 * @author $Author$
 * @version $Revision$
 */
public class ArtistListAdapter extends BaseAdapter {

    /** LayoutInflater. */
    private LayoutInflater mLayoutInflater = null;

    /** DtoInflater型のArrayList. */
    private ArrayList<ArtistListDto> mListDtoInflater = null;

    /**
     * コンストラクタ.<br>
     * @param context Context
     * @param listDtoInflater ArrayList<ArtistListDto>型
     */
    public ArtistListAdapter(Context context, ArrayList<ArtistListDto> listDtoInflater) {
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mListDtoInflater = listDtoInflater;
    }

    @Override
    public final int getCount() {
        return mListDtoInflater.size();
    }

    @Override
    public final Object getItem(int position) {
        return mListDtoInflater.get(position);
    }

    @Override
    public final long getItemId(int position) {
        return position;
    }

    @Override
    public final View getView(int position, View convertView, ViewGroup parent) {
        View sConvertView = convertView;
        if (sConvertView == null) {
            sConvertView = mLayoutInflater.inflate(R.layout.leftplaylist_low, null);
            TextView songName = (TextView) sConvertView.findViewById(R.id.SongNameText);
            TextView artistNameAndAlbumName = (TextView) sConvertView.findViewById(R.id.ArtistNameAndAlbumNameText);

            songName.setText(mListDtoInflater.get(position).getSongName());
            String createName = mListDtoInflater.get(position).getArtistName();
            createName = createName + "(" + mListDtoInflater.get(position).getAlbumName() + ")";
            artistNameAndAlbumName.setText(createName);

        } else {
            sConvertView = mLayoutInflater.inflate(R.layout.leftplaylist_low, null);
            TextView songName = (TextView) sConvertView.findViewById(R.id.SongNameText);
            TextView artistNameAndAlbumName = (TextView) sConvertView.findViewById(R.id.ArtistNameAndAlbumNameText);

            songName.setText(mListDtoInflater.get(position).getSongName());
            String createName = mListDtoInflater.get(position).getArtistName();
            createName = createName + "(" + mListDtoInflater.get(position).getAlbumName() + ")";
            artistNameAndAlbumName.setText(createName);
        }
        return sConvertView;
    }
}
