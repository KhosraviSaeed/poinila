package com.shaya.poinila.android.presentation.view.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.presenter.RecyclerViewAdapter;
import com.shaya.poinila.android.presentation.presenter.RecyclerViewProvider;
import com.shaya.poinila.android.presentation.uievent.ImageClickedUIEvent;
import com.shaya.poinila.android.presentation.view.ViewUtils;
import com.shaya.poinila.android.presentation.viewholder.SingleImageViewHolder;
import com.shaya.poinila.android.util.BusProvider;
import com.shaya.poinila.android.util.ConstantsUtils;
import com.shaya.poinila.android.util.ResourceUtils;
import com.squareup.otto.Subscribe;

import java.util.List;

import data.event.BaseEvent;
import data.event.PostReceivedEvent;
import data.event.PostsReceivedEvent;
import data.model.ImageUrls;
import data.model.Post;
import data.model.PostType;
import manager.DataRepository;

import static android.support.v7.widget.StaggeredGridLayoutManager.VERTICAL;

/**
 * Created by iran on 2015-09-26.
 */
public class CoverFromPostsDialog extends ListBusDialogFragment<Post>{

    private static final String KEY_COLLECTION_ID = ConstantsUtils.KEY_COLLECTION_ID;
    private String collectionID;


    public static CoverFromPostsDialog newInstance(String collectionID){
        CoverFromPostsDialog dialogFragment = new CoverFromPostsDialog();
        Bundle arguments = new Bundle();
        arguments.putString(KEY_COLLECTION_ID, collectionID);
        dialogFragment.setArguments(arguments);
        return dialogFragment;
    }


    @Subscribe public void onPostsReceveid(PostsReceivedEvent event){
        onGettingInitDataResponse(event);
        onGettingListDataResponse(event, event.bookmark);
    }

    @Override
    protected boolean isInitDataResponseValid(BaseEvent baseEvent) {
        return ((PostsReceivedEvent) baseEvent).receiverName == BaseEvent.ReceiverName.PostsImagesDialog && super.isInitDataResponseValid(baseEvent);
    }

    @Override
    protected boolean isListDataResponseValid(BaseEvent baseEvent, String responseBookmark) {
        return ((PostsReceivedEvent) baseEvent).receiverName == BaseEvent.ReceiverName.PostsImagesDialog &&
                super.isListDataResponseValid(baseEvent, ((PostsReceivedEvent) baseEvent).bookmark);
    }

    @Override
    public void onSuccessfulListData(BaseEvent baseEvent, String newBookmark) {
        super.onSuccessfulListData(baseEvent, newBookmark);
        List<Post> posts = ((PostsReceivedEvent) baseEvent).posts;
        for (int i = posts.size() - 1; i >= 0; i--){
            if (posts.get(i).type == PostType.TEXT)
                posts.remove(i);
        }
        getRecyclerViewAdapter().addItems(posts);
    }

    @Override
    protected void initUI(Context context) {
        super.initUI(context);
        mRecyclerView = new RecyclerViewProvider(mRecyclerView).
                setAdapter(getRecyclerViewAdapter()).
                setStaggeredLayoutManager(VERTICAL, ResourceUtils.getInteger(R.integer.column_count)).
                bindViewToAdapter();
    }

    @Subscribe public void onImageClicked(ImageClickedUIEvent event){
        Post post = getRecyclerViewAdapter().getItem(event.adapterPosition);
        BusProvider.getBus().post(new PostReceivedEvent(post));
        dismiss();
    }

    @Override
    protected RecyclerView.OnScrollListener getRecyclerViewListener() {
        return RecyclerViewProvider.staggeredListEndDetectorListener(getRecyclerViewAdapter(), this);
    }

    @Override
    public void requestForMoreData() {
        DataRepository.getCollectionPostsImages(collectionID, null, bookmark, BaseEvent.ReceiverName.PostsImagesDialog);
    }

    @Override
    public RecyclerViewAdapter<Post, SingleImageViewHolder<Post>> createAndReturnRVAdapter() {
        return new RecyclerViewAdapter<Post, SingleImageViewHolder<Post>>(getActivity(), R.layout.single_image_staggered) {
            @Override
            protected SingleImageViewHolder<Post> getProperViewHolder(View v, int viewType) {
                return new SingleImageViewHolder<Post>(v) {
                    @Override
                    public void fill(Post post) {
                        ViewUtils.setImage(imageView, post.imagesUrls, ImageUrls.ImageType.POST, ImageUrls.ImageSize.MEDIUM);
                    }
                };
            }
        };
    }

    @Override
    protected boolean sendsRequestAutomatically() {
        return true;
    }

    @Override
    protected void requestInitialData() {
        requestForMoreData();
    }

    @Override
    public boolean mustShowProgressView() {
        return true;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.recycler_view_full;
    }

    @Override
    protected void loadStateFromBundle(Bundle savedInstanceState) {
        collectionID = savedInstanceState.getString(KEY_COLLECTION_ID);
    }

    @Override
    protected void saveStateToBundle(Bundle outState) {
        outState.putString(KEY_COLLECTION_ID, collectionID);
    }

    @Override
    protected GeneralDialogData getDialogGeneralAttributes() {
        return new GeneralDialogData(getString(R.string.hint_coverFromPosts), null, null, null, null);
    }

}
