package com.shaya.poinila.android.presentation.view.fragments.notification;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.shaya.poinila.android.presentation.PageChanger;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.presenter.RecyclerViewAdapter;
import com.shaya.poinila.android.presentation.presenter.RecyclerViewProvider;
import com.shaya.poinila.android.presentation.uievent.CollectionClickedUIEvent;
import com.shaya.poinila.android.presentation.uievent.MemberClickedUIEvent;
import com.shaya.poinila.android.presentation.uievent.PostClickedUIEvent;
import com.shaya.poinila.android.presentation.uievent.SuggestionPosts;
import com.shaya.poinila.android.presentation.view.fragments.ListBusFragment;
import com.shaya.poinila.android.presentation.viewholder.BaseViewHolder;
import com.shaya.poinila.android.presentation.viewholder.DashboardPostViewHolder;
import com.shaya.poinila.android.presentation.viewholder.PostViewHolder;
import com.shaya.poinila.android.utils.PushNotificationUtils;
import com.squareup.otto.Subscribe;

import java.util.List;

import data.PoinilaNetService;
import data.event.BaseEvent;
import data.model.Collection;
import data.model.Member;
import data.model.Post;

/**
 * Created by iran on 6/15/2016.
 */
public class NPostListFragment extends ListBusFragment<Post> {

    private List<Post> mData;
    private String ids;
    private PushNotificationUtils.NOTIFICATION_TYPE type;

    public static NPostListFragment newInstance(List list, PushNotificationUtils.NOTIFICATION_TYPE type){
        NPostListFragment fragment = new NPostListFragment();
        fragment.mData = list;
        fragment.type = type;
        return fragment;
    }

    public static NPostListFragment newInstance(String ids, PushNotificationUtils.NOTIFICATION_TYPE type){
        NPostListFragment fragment = new NPostListFragment();
        fragment.ids = ids;
        fragment.type = type;
        return fragment;
    }

    @Override
    protected void initUI() {
//        super.initUI();

        mRecyclerView = new RecyclerViewProvider(mRecyclerView).
                setStaggeredLayoutManager(StaggeredGridLayoutManager.VERTICAL,
                        getResources().getInteger(R.integer.column_count)).
                setAdapter(getRecyclerViewAdapter()).
                bindViewToAdapter();

        if(mData != null)
            getRecyclerViewAdapter().addItems(mData);

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected RecyclerView.OnScrollListener getRecyclerViewListener() {
        return null;
    }

    @Override
    public void requestForMoreData() {

    }

    @Override
    public RecyclerViewAdapter createAndReturnRVAdapter() {

        return new RecyclerViewAdapter<Post, DashboardPostViewHolder>(getActivity(), R.layout.post_dashboard) {
            @Override
            protected DashboardPostViewHolder getProperViewHolder(View v, int viewType) {
                return new DashboardPostViewHolder(v, BaseEvent.ReceiverName.NPostListFragment);
            }
        };

    }



    @Override
    protected boolean sendsRequestAutomatically() {
        return true;
    }

    @Override
    protected void requestInitialData() {

        if(!type.equals(PushNotificationUtils.NOTIFICATION_TYPE.POST_SUGGESTION)) return;

        PoinilaNetService.getSuggestedPosts(ids);

    }

    @Override
    public boolean mustShowProgressView() {
        return false;
    }

    @Override
    public int getLayoutID() {
        return R.layout.recycler_view_full;
    }

    @Subscribe
    public void onPostClickedEvent(PostClickedUIEvent event) {
        if (event.receiverName != BaseEvent.ReceiverName.NPostListFragment) return;
        PageChanger.goToPost(getActivity(), getRecyclerViewAdapter().getItem(event.adapterPosition));
    }

    @Subscribe
    public void onProfilePicClickedEvent(MemberClickedUIEvent event) {
        if (event.receiverName != BaseEvent.ReceiverName.NPostListFragment) return;
        Member member =getRecyclerViewAdapter().getItem(event.adapterPosition).author;
        PageChanger.goToProfile(getActivity(), member);
    }

    @Subscribe
    public void onPostCollectionClickedEvent(CollectionClickedUIEvent event) {
        if (event.receiverName != BaseEvent.ReceiverName.NPostListFragment) return;
        Collection collection = getRecyclerViewAdapter().getItem(event.adapterPosition).collection;
        PageChanger.goToCollection(getActivity(), collection);
    }

    @Subscribe
    public void onSuggestionPosts(SuggestionPosts event){
        getRecyclerViewAdapter().addItems(event.posts);
    }
}
