package com.shaya.poinila.android.presentation.view.fragments;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.shaya.poinila.android.presentation.PageChanger;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.presenter.RecyclerViewAdapter;
import com.shaya.poinila.android.presentation.presenter.RecyclerViewProvider;
import com.shaya.poinila.android.presentation.uievent.CollectionClickedUIEvent;
import com.shaya.poinila.android.presentation.uievent.FramesUpdatedUIEvent;
import com.shaya.poinila.android.presentation.uievent.HelpMyFollowedCollectionListFragment;
import com.shaya.poinila.android.presentation.uievent.MemberClickedUIEvent;
import com.shaya.poinila.android.presentation.uievent.OnFrameClickedUIEvent;
import com.shaya.poinila.android.presentation.view.dialog.NewCollectionDialog;
import com.shaya.poinila.android.presentation.view.help.Help;
import com.shaya.poinila.android.presentation.viewholder.BaseViewHolder;
import com.shaya.poinila.android.presentation.viewholder.MyFollowedCollectionViewHolder;
import com.shaya.poinila.android.util.BusProvider;
import com.shaya.poinila.android.util.Logger;
import com.shaya.poinila.android.util.PoinilaPreferences;
import com.squareup.otto.Subscribe;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import data.event.BaseEvent;
import data.event.CollectionsReceivedEvent;
import data.model.Collection;
import data.model.Frame;
import data.model.Member;
import manager.DBFacade;
import manager.DataRepository;

import static com.shaya.poinila.android.presentation.view.ViewUtils.setText;
import static com.shaya.poinila.android.util.ConstantsUtils.REQUEST_MEMBER_COLLECTIONS;
import static com.shaya.poinila.android.util.ConstantsUtils.REQUEST_MEMBER_FOLLOWED_COLLECTIONS;
import static com.shaya.poinila.android.util.ConstantsUtils.REQUEST_POST_REPOSTING_COLLECTIONS;

// TODO: public class MyFollowedCollectionsFragment extends BusFragment {
public class MyFollowedCollectionsFragment extends CollectionListFragment{

    //private MySpinnerAdapter mSpinnerAdapter;
    //private ArrayAdapter<String> mSpinnerAdapter;
    //public @Bind(R.actorID.select_frame) Spinner mFrameSpinner;
    private String selectedFrameID = null;
    private List<Frame> frames;
    @Bind(R.id.select_frame_container) View selectFrameContainer;
    @Bind(R.id.select_frame_button) ImageButton selectFrameBtn;
    @Bind(R.id.select_frame_text) TextView selectFrameText;

    public MyFollowedCollectionsFragment() {
        // Required empty public constructor
    }

    public static MyFollowedCollectionsFragment newInstance() {
        return new MyFollowedCollectionsFragment();
    }

    @Subscribe
    public void onFramesUpdated(FramesUpdatedUIEvent event){
        updateFrames(event.frames);
    }


    public void updateFrames(List<Frame> frames){
        if (frames != null && !frames.isEmpty()){
            this.frames.addAll(frames);
        }
    }

    @OnClick({R.id.select_frame_button, R.id.select_frame_text}) public void onSelectingFrame(){
        if (frames == null || frames.isEmpty())
            Logger.toast(R.string.error_no_frame_exist);
        else
            FramesDialog.newInstance(frames).show(getFragmentManager(), null);
    }

    @Subscribe public void onFrameSelected(OnFrameClickedUIEvent event){
        selectedFrameID = event.frame.id == -1 ? null : event.frame.getId();
        setText(selectFrameText, event.frame.id == -1 ? getString(R.string.select_frame) : event.frame.name);
        getRecyclerViewAdapter().clear();
        bookmark = null;
        initDataResponseReceived = false;
        initData();
    }

    @Override
    protected RecyclerView.OnScrollListener getRecyclerViewListener() {
        return RecyclerViewProvider.gridListEndDetectionListener(getRecyclerViewAdapter(), this);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (frames == null) {
            frames = DBFacade.getMyFrames();
            frames.add(0, new Frame(-1, getString(R.string.default_no_frame)));
        }
    }

    @Override
    public int getLayoutID() {
        return R.layout.fragment_my_followed_collections;
    }


    @Override
    public void onSuccessfulListData(BaseEvent baseEvent, String newBookmark) {
        super.onSuccessfulListData(baseEvent, newBookmark);

    }

    @Override
    protected void initUI() {

        mRecyclerView = new RecyclerViewProvider(mRecyclerView).
                setGridLayoutManager(GridLayoutManager.VERTICAL,
                        getResources().getInteger(R.integer.column_count), new GridLayoutManager.SpanSizeLookup() {
                            @Override
                            public int getSpanSize(int position) {
                                if(getRecyclerViewAdapter().getItemViewType(position) == RecyclerViewAdapter.VIEW_TYPE_LOAD_PROGRESS ){
                                    return getResources().getInteger(R.integer.column_count);
                                }
                                return 1;
                            }
                        }).
                //setAdapter(MY_FOLLOWING_COLLECTION_ADAPTER, getActivity()).
                setAdapter(getRecyclerViewAdapter()).
                bindViewToAdapter();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Boolean showFrame = sharedPref.getBoolean(getString(R.string.pref_show_select_frame_key), false);
        if (!showFrame)
            selectFrameContainer.setVisibility(View.GONE);


        mRecyclerView.addOnScrollListener(getRecyclerViewListener());
        //frames = new ArrayList<>();

    }

    @Override
    public RecyclerViewAdapter createAndReturnRVAdapter() {
        return new RecyclerViewAdapter(getActivity(), R.layout.collection_simple) {
            @Override
            protected BaseViewHolder getProperViewHolder(View v, int viewType) {
                if(viewType == RecyclerViewAdapter.VIEW_TYPE_LOAD_PROGRESS ){
                    return new BaseViewHolder.EmptyViewHolder(v);
                }
                return new MyFollowedCollectionViewHolder(v, BaseEvent.ReceiverName.MyFollowedCollections);
            }
        };
    }

    @Override
    @Subscribe
    public void onCollectionsReceived(CollectionsReceivedEvent event) {
        super.onCollectionsReceived(event);


    }

    @Subscribe
    public void answerAvailable(HelpMyFollowedCollectionListFragment event) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if(!PoinilaPreferences.getHelpStatus(getClass().getName())){
                    showHelp();
                    PoinilaPreferences.putHelpStatus(getClass().getName(), true);
                }

            }
        }, 500);

    }

    @Override
    protected boolean isListDataResponseValid(BaseEvent baseEvent, String responseBookmark) {
        CollectionsReceivedEvent event = ((CollectionsReceivedEvent) baseEvent);
        boolean condition = event.receiverName == BaseEvent.ReceiverName.MyFollowedCollections;
        return super.isListDataResponseValid(baseEvent, responseBookmark) && condition;

    }

    @Override
    protected boolean isInitDataResponseValid(BaseEvent event) {
        return super.isInitDataResponseValid(event) && event.receiverName == BaseEvent.ReceiverName.MyFollowedCollections;
    }

    @Override
    public void requestForMoreData() {

        DataRepository.getInstance().getMyFollowedCollections(selectedFrameID, bookmark);
    }

    @Subscribe public void onProfilePicClickedEvent(MemberClickedUIEvent event){
        if (event.receiverName != BaseEvent.ReceiverName.MyFollowedCollections)
            return;
        Member member = ((Collection)getRecyclerViewAdapter().getItem(event.adapterPosition)).owner;
        PageChanger.goToProfile(getActivity(), member);
    }

    @Subscribe public void onCollectionClicked(CollectionClickedUIEvent event){
        if (event.receiverName != BaseEvent.ReceiverName.MyFollowedCollections)
            return;
        Collection collection = (Collection)getRecyclerViewAdapter().getItem(event.adapterPosition);
        PageChanger.goToCollection(getActivity(), collection);
    }


    public void showHelp() {
        if(getRecyclerViewAdapter().getItemCount() > 0 && getRecyclerViewAdapter().getItem(0) instanceof Collection){
            Help.getInstance().showFollowedCollectionHelp(getActivity(), mRecyclerView.getLayoutManager().findViewByPosition(0));
            viewedHelp = true;
        }
    }


    /*-------------------------------------*/

    public static class MySpinnerAdapter extends BaseAdapter{

        List<Frame> items;
        private Context context;

        public MySpinnerAdapter(Context context, List<Frame> frames) {
            this.context = context;
            items = frames;
        }

        public void addItems(List<Frame> frames){
            /*for (Frame frame : circles)
                items.add(frame);*/
            items.addAll(frames);
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View spinView;
            if( convertView == null ){
                LayoutInflater inflater = LayoutInflater.from(context);
                spinView = inflater.inflate(R.layout.spinner_item, parent, false);
            } else {
                spinView = convertView;
            }
            ((TextView) spinView.findViewById(R.id.spinner_row_title)).setText(((Frame)getItem(position)).name);
            return spinView;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View spinView;
            if( convertView == null ){
                LayoutInflater inflater = LayoutInflater.from(context);
                spinView = inflater.inflate(R.layout.spinner_item, parent, false);
            } else {
                spinView = convertView;
            }
            ((TextView) spinView.findViewById(R.id.spinner_row_title)).setText(((Frame)getItem(position)).name);
            return spinView;
        }
    }


    public static class FramesDialog extends android.support.v4.app.DialogFragment {

        List<Frame> frames;

        public static FramesDialog newInstance(List<Frame> frames){
            FramesDialog d = new FramesDialog();
            // TODO: bullshit!
            d.frames = frames;
            return d;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            return builder.setTitle(R.string.frames).setAdapter(new MySpinnerAdapter(getActivity(), frames),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            BusProvider.getBus().post(new OnFrameClickedUIEvent(frames.get(which)));
                        }
                    }).create();
        }
    }


}
