package com.shaya.poinila.android.presentation.view.fragments;

import android.net.Uri;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.util.Patterns;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.shaya.poinila.android.presentation.PageChanger;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.presenter.RecyclerViewAdapter;
import com.shaya.poinila.android.presentation.view.ViewUtils;
import com.shaya.poinila.android.presentation.view.activity.NewPostActivity;
import com.shaya.poinila.android.presentation.view.costom_view.PonilaChoiceView;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import data.PoinilaNetService;
import data.event.BaseEvent;
import data.event.SuggestedWebpagePostReceived;
import data.model.Image;
import data.model.PostType;
import data.model.SuggestedWebPagePost;

/**
 * Created by iran on 8/15/2016.
 */
public class NewWebSitePostInputURLFragment extends BusFragment implements PonilaChoiceView.OnOptionSelected {

    private static final String KEY_SITE_ADDRESS = "site address";
//    @Bind(R.id.image_radio_btn)
//    protected RadioButton image;
//    @Bind(R.id.text_radio_btn)
//    protected RadioButton text;
//    @Bind(R.id.post_type_container)
//    RadioGroup postTypeContainer;

    @Bind(R.id.new_web_site_post_options)
    PonilaChoiceView ponilaChoiceView;

    @Bind(R.id.url_field)
    TextInputEditText urlField;
    //    @Bind(R.id.recycler_view)
//    RecyclerView mRecyclerView;
    @Bind(R.id.progress_bar)
    ProgressBar progressWheel;
    @Bind(R.id.url_textinputlayout)
    TextInputLayout urlInputLayout;
    RecyclerViewAdapter<Image, ?> mRecyclerViewAdapter;
    String[] protocols = {"http", "https"};
    List<String> validProtocols = Arrays.asList(protocols);
    private String siteAddress;
    private PostType postType = PostType.TEXT;
    private SuggestedWebPagePost suggestedPost;

    @Override
    public int getLayoutID() {
        return R.layout.fragment_new_web_site_post_input_url;
    }

    @Override
    protected void initUI() {
        ponilaChoiceView.setOptionsText(R.string.text, R.string.image, R.string.video);
        ponilaChoiceView.setOnOptionSelected(this);

    }

    @OnClick(R.id.new_web_site_post_next_btn)
    public void nextLevel() {

        setSiteAddress(urlField.getText().toString());
        if (isAddressValid(siteAddress)) {
            if (postType.equals(PostType.TEXT)) {
                PoinilaNetService.getWebsiteInfo(siteAddress, postType);
                showProgressDialog();
            } else {
                String siteAddress = urlField.getText().toString();
                ((NewPostActivity) getActivity()).goToSelectMediaFragment(postType, siteAddress);
            }

        }
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

    @Subscribe
    public void onSuggestedWebpagePostReceived(SuggestedWebpagePostReceived event) {
        suggestedPost = event.webpagePost;
        suggestedPost.siteAddress = this.siteAddress;
        dismissProgressDialog();
        PageChanger.goToNewPost(getFragmentManager(), suggestedPost);
    }

    @Override
    public boolean mustShowProgressView() {
        return false;
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

    public void setSiteAddress(String siteAddress) {
        this.siteAddress = siteAddress;
    }

    @Override
    public void onFirstOption() {

        postType = PostType.TEXT;

    }

    @Override
    public void onSecondOption() {

        postType = PostType.IMAGE;

    }

    @Override
    public void onThirdOption() {

        postType = PostType.VIDEO;

    }
}
