package com.shaya.poinila.android.presentation.view.fragments;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;

import com.shaya.poinila.android.presentation.PageChanger;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.presenter.RecyclerViewAdapter;
import com.shaya.poinila.android.presentation.presenter.RecyclerViewProvider;
import com.shaya.poinila.android.presentation.uievent.ImageClickedUIEvent;
import com.shaya.poinila.android.presentation.uievent.NewWebsitePostEvent;
import com.shaya.poinila.android.presentation.view.costom_view.AspectRatioImageView;
import com.shaya.poinila.android.presentation.viewholder.SingleImageViewHolder;
import com.shaya.poinila.android.util.BusProvider;
import com.shaya.poinila.android.util.ConstantsUtils;
import com.shaya.poinila.android.util.Logger;
import com.shaya.poinila.android.util.ResourceUtils;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import data.PoinilaNetService;
import data.event.BaseEvent;
import data.event.SuggestedWebpagePostReceived;
import data.model.Image;
import data.model.PostType;
import data.model.SuggestedWebPagePost;

/**
 * Created by hossein on 8/18/16.
 */
public class NewWebSitePostSelectMediaFragment extends ListBusFragment {


    private PostType postType;
    private List<MeasureTarget> targets;
    private String siteAddress;
    private SuggestedWebPagePost suggestedPost;
    String[] protocols = {"http", "https"};
    List<String> validProtocols = Arrays.asList(protocols);
    RecyclerViewAdapter<Image, ?> mRecyclerViewAdapter;

    public static NewWebSitePostSelectMediaFragment newInstance(PostType postType, String siteAddress){

        NewWebSitePostSelectMediaFragment fragment = new NewWebSitePostSelectMediaFragment();
        fragment.postType = postType;
        fragment.siteAddress = siteAddress;

        return fragment;
    }

    @Override
    protected void initUI() {
        super.initUI();

        targets = new ArrayList<>();

        // TODO: parse url and extract imagesUrls greater than a specific size in each dimension
        mRecyclerView = new RecyclerViewProvider(mRecyclerView).
                setStaggeredLayoutManager(StaggeredGridLayoutManager.VERTICAL,
                        ResourceUtils.getInteger(R.integer.column_count)).
                setAdapter(new RecyclerViewAdapter<Image, SingleImageViewHolder<Image>>(getActivity(), R.layout.single_image_staggered) {
                    @Override
                    protected SingleImageViewHolder<Image> getProperViewHolder(View v, int viewType) {
                        return new SingleImageViewHolder<Image>(v) {
                            @Override
                            public void fill(Image image) {
                                ((AspectRatioImageView) imageView).setAspectRatio(image.height * 1f / image.width);
                                imageView.requestLayout();
                                Picasso.with(imageView.getContext()).load(image.url).into(imageView);
                            }
                        };
                    }
                }).bindViewToAdapter();

        mRecyclerViewAdapter = (RecyclerViewAdapter<Image, ?>) mRecyclerView.getAdapter();
    }

    @Override
    protected RecyclerView.OnScrollListener getRecyclerViewListener() {
        return null;
    }

    @Override
    public void requestForMoreData() {
        //TODO
    }

    @Override
    public RecyclerViewAdapter<Image, ?> createAndReturnRVAdapter() {
        return mRecyclerViewAdapter;
    }

    @Override
    protected boolean sendsRequestAutomatically() {
        return true;
    }

    @Override
    protected void requestInitialData() {
        fetchSiteInfo();
    }

    private void fetchSiteInfo() {
        targets.clear();
        suggestedPost = null;
//        setLoading(new Loading());
        PoinilaNetService.getWebsiteInfo(siteAddress, postType);
    }

    @Subscribe
    public void urlsReceivedEvent(SuggestedWebpagePostReceived event) {
        onGettingInitDataResponse(event);
    }

    @Override
    public void onSuccessfulInitData(BaseEvent baseEvent) {
        super.onSuccessfulInitData(baseEvent);
        getRecyclerViewAdapter().clear();
        suggestedPost = ((SuggestedWebpagePostReceived) baseEvent).webpagePost;
        suggestedPost.siteAddress = this.siteAddress;
        feedback();
        for (final Image image : suggestedPost.images) {
            MeasureTarget target = new MeasureTarget(image.url);
            targets.add(target);
            Picasso.with(getActivity()).load(image.url).into(target);
        }
    }

    private void feedback() {
        // TODO:
        if (suggestedPost.images.isEmpty()) {
            Logger.toast(R.string.error_no_image_found);
        }
        if ((suggestedPost.name != null || suggestedPost.summary != null)) {
            Logger.toast(R.string.successfully_loaded);
        }
    }

    @Subscribe
    public void onSiteImageClickEvent(ImageClickedUIEvent event) {
        suggestedPost.imageAddress = mRecyclerViewAdapter.getItem(event.adapterPosition).url;
        BusProvider.getBus().post(new NewWebsitePostEvent(suggestedPost));
        PageChanger.goToNewPost(getFragmentManager(), suggestedPost);
    }

    @Override
    public boolean mustShowProgressView() {
        return false;
    }

    @Override
    public int getLayoutID() {
        return R.layout.recycler_view_full;
    }

    class MeasureTarget implements Target {
        private final String address;

        public MeasureTarget(String address) {
            this.address = address;
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            if (bitmap.getWidth() > ConstantsUtils.MINIMUM_POST_IMAGE_WIDTH &&
                    bitmap.getHeight() > ConstantsUtils.MINIMUM_POST_IMAGE_HEIGHT) {
                getRecyclerViewAdapter().addItem(new Image(address, bitmap.getWidth(), bitmap.getHeight()));
                Log.w("poinila_image", String.format("width: %d, height: %d", bitmap.getWidth(), bitmap.getHeight()));
            }
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    }
}
