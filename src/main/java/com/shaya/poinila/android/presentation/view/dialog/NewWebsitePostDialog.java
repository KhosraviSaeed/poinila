package com.shaya.poinila.android.presentation.view.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.presenter.RecyclerViewAdapter;
import com.shaya.poinila.android.presentation.presenter.RecyclerViewProvider;
import com.shaya.poinila.android.presentation.uievent.ImageClickedUIEvent;
import com.shaya.poinila.android.presentation.uievent.NewWebsitePostEvent;
import com.shaya.poinila.android.presentation.view.ViewUtils;
import com.shaya.poinila.android.presentation.view.costom_view.AspectRatioImageView;
import com.shaya.poinila.android.presentation.view.help.Help;
import com.shaya.poinila.android.presentation.viewholder.SingleImageViewHolder;
import com.shaya.poinila.android.util.BusProvider;
import com.shaya.poinila.android.util.ConstantsUtils;
import com.shaya.poinila.android.util.Logger;
import com.shaya.poinila.android.util.PoinilaPreferences;
import com.shaya.poinila.android.util.ResourceUtils;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import data.PoinilaNetService;
import data.event.BaseEvent;
import data.event.LoadingImagedFailedEvent;
import data.event.SuggestedWebpagePostReceived;
import data.model.Image;
import data.model.PostType;
import data.model.SuggestedWebPagePost;

/**
 * Created by iran on 2015-07-15.
 */
public class NewWebsitePostDialog extends BusDialogFragment {
    private static final String KEY_SITE_ADDRESS = "site address";
    @Bind(R.id.image_radio_btn)
    protected RadioButton image;
    @Bind(R.id.text_radio_btn)
    protected RadioButton text;
    @Bind(R.id.post_type_container)
    RadioGroup postTypeContainer;

    @Bind(R.id.url_field)
    TextInputEditText urlField;
    @Bind(R.id.go_btn)
    ImageButton goBtn;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.progress_bar)
    ProgressBar progressWheel;
    @Bind(R.id.url_textinputlayout)
    TextInputLayout urlInputLayout;
    RecyclerViewAdapter<Image, ?> mRecyclerViewAdapter;
    String[] protocols = {"http", "https"};
    List<String> validProtocols = Arrays.asList(protocols);
    private String siteAddress;
    protected boolean isImaged = true;

    // picasso doesn't hold a reference to `Target`s. So during download, they are being garbage collected.
    // We hold a strong reference to array of targets just to avoid that
    List<MeasureTarget> targets;
    private SuggestedWebPagePost suggestedPost;

    @Override
    public int getLayoutResId() {
        return R.layout.dialog_new_website_post;
    }

    @Override
    protected void initUI(Context context) {
        targets = new ArrayList<>();

        // TODO: parse url and extract imagesUrls greater than a specific size in each dimension
        mRecyclerView = new RecyclerViewProvider(mRecyclerView).
                setStaggeredLayoutManager(StaggeredGridLayoutManager.VERTICAL,
                        ResourceUtils.getInteger(R.integer.column_count)).
                setAdapter(new RecyclerViewAdapter<Image, SingleImageViewHolder<Image>>(context, R.layout.single_image_staggered) {
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

        urlInputLayout.setHint(getString(R.string.hint_website_url));
        urlField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    fetchSiteInfo();
                    return true;
                }
                return false; // ??
            }
        });

        postTypeContainer.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.image_radio_btn:
                        isImaged = true;
                        mRecyclerView.setVisibility(View.VISIBLE);

                        break;
                    case R.id.text_radio_btn:
                        isImaged = false;
                        mRecyclerView.setVisibility(View.GONE);
                        break;
                }
            }
        });




    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if(!PoinilaPreferences.getHelpStatus(this.getClass().getName())){
            getDialog().setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    Help.getInstance().showNewPostHelp(getActivity(), urlField);
                    PoinilaPreferences.putHelpStatus(NewWebsitePostDialog.this.getClass().getName(), true);
                }
            });

        }


        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Subscribe
    public void urlsReceivedEvent(SuggestedWebpagePostReceived event) {
        onGettingInitDataResponse(event);
    }

    @Override
    public void onSuccessfulInitData(BaseEvent baseEvent) {
        super.onSuccessfulInitData(baseEvent);
        showGoBtn(false);
        mRecyclerViewAdapter.clear();
        suggestedPost = ((SuggestedWebpagePostReceived) baseEvent).webpagePost;
        suggestedPost.siteAddress = this.siteAddress;
        feedback();
        if (!isImaged)
            return;
        for (final Image image : suggestedPost.images) {
            MeasureTarget target = new MeasureTarget(image.url);
            targets.add(target);
            Picasso.with(getActivity()).load(image.url).into(target);
        }
    }

    private void feedback() {
        // TODO:
        if (isImaged && suggestedPost.images.isEmpty()) {
            Logger.toast(R.string.error_no_image_found);
        }
        if (!isImaged && (suggestedPost.name != null || suggestedPost.summary != null)) {
            Logger.toast(R.string.successfully_loaded);
        }
    }

    @OnClick(R.id.go_btn)
    void fetchSiteInfo() {
        targets.clear();
        siteAddress = urlField.getText().toString().trim();
        suggestedPost = null;
        if (isAddressValid(siteAddress)) {
            showProgressBar();
            PoinilaNetService.getWebsiteInfo(siteAddress, isImaged ? PostType.IMAGE : PostType.TEXT);
        }
    }

    @Subscribe
    public void loadingFailed(LoadingImagedFailedEvent event) {
        showGoBtn(true);
    }

    private void showProgressBar() {
        progressWheel.setVisibility(View.VISIBLE);
        goBtn.setVisibility(View.INVISIBLE);
        //progressWheel.;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (progressWheel != null && progressWheel.getVisibility() == View.VISIBLE) {
                    showGoBtn(true);
                }
            }
        }, ConstantsUtils.READ_TIME_OUT_MILLISECONDS);
    }

    private void showGoBtn(boolean failed) {
        //progressWheel.stopSpinning();
        progressWheel.setVisibility(View.INVISIBLE);
        goBtn.setVisibility(View.VISIBLE);
        if (failed) Logger.toast(R.string.error_loading_images);
    }

    private boolean isAddressValid(String address) {
        String error;
        Uri uri = Uri.parse(address);

        if (Patterns.WEB_URL.matcher(uri.toString()).matches()) { // trim is essential.
            String protocol = uri.getScheme();
            if (protocol == null) {
                protocol = "http";
                setSiteAddress("http://" + address);
            }
            if (!validProtocols.contains(protocol)) {
                error = getString(R.string.wrong_protocol);
            } else {
                setSiteAddress(new Uri.Builder().scheme(uri.getScheme()).
                        authority(uri.getAuthority()).
                        path(uri.getPath()).
                        query(uri.getQuery()).build().toString());
                urlInputLayout.setErrorEnabled(false);
                return true;
            }
        } else {
            error = getString(R.string.error_invalid_url);
        }
        ViewUtils.setInputError(urlField, error);
        return false;
    }

    @Subscribe
    public void onSiteImageClickEvent(ImageClickedUIEvent event) {
        suggestedPost.imageAddress = mRecyclerViewAdapter.getItem(event.adapterPosition).url;
        BusProvider.getBus().post(new NewWebsitePostEvent(suggestedPost));
        onNegativeButton();
    }

    @Override
    public void onPositiveButton() {
        if (suggestedPost == null) {
            Logger.toast(R.string.error_page_info_not_loaded);
            return;
        }
        BusProvider.getBus().post(new NewWebsitePostEvent(suggestedPost));
        super.onNeutralButton();
    }

    @Override
    protected void loadStateFromBundle(Bundle savedInstanceState) {
        siteAddress = savedInstanceState.getString(ConstantsUtils.KEY_WEBSITE_URL, null);
    }

    @Override
    protected void saveStateToBundle(Bundle outState) {
        outState.putString(KEY_SITE_ADDRESS, siteAddress);
    }

    @Override
    protected GeneralDialogData getDialogGeneralAttributes() {
        return new GeneralDialogData(R.string.website_post, RESOURCE_NONE, R.string.create_text_post, R.string.cancel, RESOURCE_NONE);
    }

    public void setSiteAddress(String siteAddress) {
        this.siteAddress = siteAddress;
    }

    @Override
    protected boolean sendsRequestAutomatically() {
        return false;
    }

    @Override
    protected void requestInitialData() {

    }

    @Override
    public ViewGroup getLoadableView() {
        return null;
    }

    @Override
    public boolean mustShowProgressView() {
        return false;
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
                mRecyclerViewAdapter.addItem(new Image(address, bitmap.getWidth(), bitmap.getHeight()));
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
