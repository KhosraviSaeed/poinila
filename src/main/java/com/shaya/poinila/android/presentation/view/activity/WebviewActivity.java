package com.shaya.poinila.android.presentation.view.activity;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.view.fragments.BusFragment;
import com.shaya.poinila.android.util.ConstantsUtils;
import com.shaya.poinila.android.util.Logger;
import com.shaya.poinila.android.util.NavigationUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import data.PoinilaNetService;
import manager.DataRepository;

import static com.shaya.poinila.android.util.ConstantsUtils.KEY_ENTITY;
import static com.shaya.poinila.android.util.ConstantsUtils.KEY_PAGE_TITLE_PARAMETER;
import static com.shaya.poinila.android.util.ConstantsUtils.KEY_POST_ID;
import static com.shaya.poinila.android.util.ConstantsUtils.KEY_WEBSITE_URL;

public class WebviewActivity extends FragmentHostActivity {

    @Override
    protected android.support.v4.app.Fragment getHostedFragment() {
        return PoinilaWebViewFragment.newInstance(
                getIntent().getStringExtra(KEY_WEBSITE_URL),
                getIntent().getStringExtra(KEY_PAGE_TITLE_PARAMETER),
                getIntent().getStringExtra(KEY_ENTITY));
    }

    @Override
    protected boolean withToolbar() {
        return true;
    }

    @Override
    protected void initUI() {
    }

    public static String getPostUrl(String postId){
        return ConstantsUtils.POINILA_SERVER_ADDRESS.concat("post/" + postId + "/");
    }

    public static class PoinilaWebViewFragment extends BusFragment {
        @Bind(R.id.webview)
        WebView webview;
        ProgressBar progressBar;

        private String postId, url, pageTitle;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setHasOptionsMenu(true);

            url = getArguments().getString(KEY_WEBSITE_URL);
            pageTitle = getArguments().getString(KEY_PAGE_TITLE_PARAMETER);
            postId = getArguments().getString(KEY_POST_ID);

            if (!DataRepository.isUserAnonymous())
                PoinilaNetService.informServerOfInlineBrowsing(postId);
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            menu.clear();
            inflater.inflate(R.menu.menu_inline_browser, menu);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            if (item.getItemId() == R.id.browser) {
                if (!DataRepository.isUserAnonymous())
                    PoinilaNetService.informServerOfExternalBrowsing(postId);

                Bundle extraHeaders = new Bundle();
                extraHeaders.putString("Referer", getPostUrl(postId));
                NavigationUtils.goToUrl(getActivity(), url, extraHeaders);
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        @Override
        protected boolean sendsRequestAutomatically() {
            return true;
        }

        @Override
        protected void requestInitialData() {
        }

        @Override
        public ViewGroup getLoadableView() {
            return webview;
        }

        @Override
        public boolean mustShowProgressView() {
            return true;
        }

        @Override
        public int getLayoutID() {
            return R.layout.fragment_webview;
        }

        /*@Override
        public ProgressBar getProgressBar() {
            ProgressBar progressBar = ButterKnife.findById(progressView, R.id.progress_bar);
            if (progressBar == null){
                progressBar = new ProgressBar(getActivity(), null, android.R.attr.progressBarStyleHorizontal);
                progressBar.setIndeterminate(false);
            }
            return progressBar;
        }*/

        @Override
        protected void initProgressBar(ProgressBar progressBar) {

        }

        @Override
        protected int getProgressViewLayoutID() {
            return R.layout.progress_determinate;
        }

        @SuppressLint("SetJavaScriptEnabled")
        @Override
        protected void initUI() {
            if (url == null || !Patterns.WEB_URL.matcher(url).matches()) {
                Logger.toastError(R.string.error_invalid_url);
                getActivity().finish();
                return;
            }
            getActivity().setTitle(pageTitle);

            webview.getSettings().setJavaScriptEnabled(true);
            webview.getSettings().setAllowContentAccess(false);
            webview.getSettings().setAllowFileAccess(false);
            webview.getSettings().setDomStorageEnabled(true);
            webview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            webview.getSettings().setAppCacheEnabled(true);
            webview.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
            webview.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);

            webview.getSettings().setSupportZoom(true);
            webview.getSettings().setBuiltInZoomControls(true);
            webview.getSettings().setDisplayZoomControls(false);

            webview.setInitialScale(1);
            webview.getSettings().setLoadWithOverviewMode(true);
            webview.getSettings().setUseWideViewPort(true);
            webview.getSettings().setSaveFormData(true);

            if (Build.VERSION.SDK_INT >= 17) {
                webview.getSettings().setMediaPlaybackRequiresUserGesture(false);
            }



            if (Build.VERSION.SDK_INT >= 19) {
                webview.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            }
            else {
                webview.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            }

            webview.setWebChromeClient(new WebChromeClient() {
                public void onProgressChanged(WebView view, int progress) {
                    // Activities and WebViews measure progress with different scales.
                    // The progress meter will automatically disappear when we reach 100%
                    getProgressBar().setProgress(progress);
                    if (progress == 100)
                        onGettingInitDataResponse(null);
                }
            });
            webview.setWebViewClient(new WebViewClient() {
                // disable page navigation through webview. webview must show only the initial url
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if (url != null && Patterns.WEB_URL.matcher(url).matches()) {
                        NavigationUtils.goToUrl(getActivity(), url, null);
                    }
                    return true;
                }
            });

            // from Wikipedia: The HTTP referrer (originally a misspelling of referrer)...
            // So don't change the string!!
            Map<String, String> extraHeaders = new HashMap<>();
            extraHeaders.put("Referer", getPostUrl(postId));
            webview.loadUrl(url, extraHeaders);
        }

        public static android.support.v4.app.Fragment newInstance(String url, String pageTitle, String postId) {
            PoinilaWebViewFragment fragment = new PoinilaWebViewFragment();
            Bundle b = new Bundle();
            b.putString(KEY_WEBSITE_URL, url);
            b.putString(KEY_PAGE_TITLE_PARAMETER, pageTitle);
            b.putString(KEY_POST_ID, postId);
            fragment.setArguments(b);
            return fragment;
        }
    }
}
