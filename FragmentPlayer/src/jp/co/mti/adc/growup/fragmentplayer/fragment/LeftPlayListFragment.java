package jp.co.mti.adc.growup.fragmentplayer.fragment;

import java.util.ArrayList;

import jp.co.mti.adc.growup.fragmentplayer.R;
import jp.co.mti.adc.growup.fragmentplayer.adapter.ArtistListAdapter;
import jp.co.mti.adc.growup.fragmentplayer.dto.ArtistListDto;
import jp.co.mti.adc.growup.fragmentplayer.listener.OnActionSearchListener;
import jp.co.mti.adc.growup.fragmentplayer.listener.OnCancelButtonListener;
import jp.co.mti.adc.growup.fragmentplayer.listener.OnLeftFragmentListViewItemListener;
import jp.co.mti.adc.growup.fragmentplayer.model.LeftPlayListModel;
import jp.co.mti.adc.growup.fragmentplayer.view.ToastView;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

/**
 * リスト画面のFragmentクラス.<br>
 * @author $Author$
 * @version $Revision$
 */
public class LeftPlayListFragment extends ListFragment implements OnClickListener, OnEditorActionListener {

    /** ArtistListDtoのArrayList. */
    private ArrayList<ArtistListDto> mArtistList;

    /** ListView. */
    private ListView mListView;

    /** 独自のリストクリックListener . */
    private OnLeftFragmentListViewItemListener mListener;

    /** 独自のキャンセルボタンクリックListener. */
    private OnCancelButtonListener mCancelButtonListener;

    /** 独自の検索クリックListener. */
    private OnActionSearchListener mActionSearchListener;

    /** モデルクラス. */
    private LeftPlayListModel mLeftPlayListModel;

    /** Context. */
    private Context mContext;

    /** EditText. */
    private EditText mEditText;

    /** キャンセルボタン . */
    private Button mButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.leftplaylist, container, false);
        mListView = (ListView) view.findViewById(android.R.id.list);
        mEditText = (EditText) view.findViewById(R.id.editText1);
        mButton = (Button) view.findViewById(R.id.button1);
        mButton.setOnClickListener(this);
        mEditText.setOnFocusChangeListener((OnFocusChangeListener) mContext);
        mEditText.setOnEditorActionListener(this);

        // Adapterを生成してListViewにセットする
        ArtistListAdapter adapter = createAdapter();
        mListView.setAdapter(adapter);

        mListView.setFastScrollEnabled(true);
        mListView.setOnScrollListener((OnScrollListener) mContext);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnLeftFragmentListViewItemListener) activity;
            mCancelButtonListener = (OnCancelButtonListener) activity;
            mActionSearchListener = (OnActionSearchListener) activity;
            mLeftPlayListModel = new LeftPlayListModel();
            mArtistList = new ArrayList<ArtistListDto>();
            mContext = activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnLeftFragmentListViewItemListener");
        }
    }

    @Override
    public void onListItemClick(ListView listView, View parent, int position, long id) {
        mListener.onLeftItemSelected(position, mArtistList);
    }

    @Override
    public void onClick(View v) {
        mEditText.clearFocus();
        mEditText.setFocusable(false);
        mEditText.setFocusableInTouchMode(false);

        ArtistListAdapter adapter = createAdapter();
        mListView.setAdapter(adapter);

        mCancelButtonListener.onCancelButtonClick(mEditText);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

        mActionSearchListener.onActionSearch(v, actionId, event, mEditText);

        // 楽曲一覧取得用のCursorを取得
        Cursor cursor = mLeftPlayListModel.getSearchCursor(mContext, mEditText.getText().toString());

        // 検索結果用のリスト
        ArrayList<ArtistListDto> searchArtistList;

        // 取得したCursorを利用してListを取得
        searchArtistList = mLeftPlayListModel.getList(mContext, cursor);

        // リストのサイズが0じゃない場合はadapterにセットし表示させる
        if (searchArtistList.size() > 0) {
            mArtistList = searchArtistList;
            ArtistListAdapter adapter = new ArtistListAdapter(mContext, searchArtistList);
            mListView.setAdapter(adapter);
        } else {
            ToastView.showToast(mContext, mContext.getString(R.string.NoArtist));
        }
        return false;
    }

    /**
     * Adapterを生成する<br>
     * @return ArtistListAdapter
     */
    private ArtistListAdapter createAdapter() {
        // 楽曲一覧取得用のCursorを取得
        Cursor cursor = mLeftPlayListModel.getCursor(mContext);

        // 取得したCursorを利用してListを取得
        mArtistList = mLeftPlayListModel.getList(mContext, cursor);

        if (mArtistList.size() == 0) {
            ToastView.showToast(mContext, mContext.getString(R.string.NoSongs));
        }

        // Adapterを生成する
        return new ArtistListAdapter(mContext, mArtistList);
    }
}
