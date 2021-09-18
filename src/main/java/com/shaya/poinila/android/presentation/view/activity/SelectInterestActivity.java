package com.shaya.poinila.android.presentation.view.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.shaya.poinila.android.presentation.PageChanger;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.presenter.RecyclerViewAdapter;
import com.shaya.poinila.android.presentation.presenter.RecyclerViewProvider;
import com.shaya.poinila.android.presentation.uievent.CheckBoxClickUIEvent;
import com.shaya.poinila.android.presentation.view.ViewUtils;
import com.shaya.poinila.android.presentation.view.fragments.LoginFragment;
import com.shaya.poinila.android.presentation.viewholder.SelectableInterestViewHolder;
import com.shaya.poinila.android.util.Logger;
import com.shaya.poinila.android.util.StringUtils;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import data.PoinilaNetService;
import data.event.BaseEvent;
import data.event.InterestsReceivedEvent;
import data.event.ServerResponseEvent;
import data.event.UserInterestsReceivedEvent;
import data.model.ImageTag;
import data.model.Tag;
import manager.DataRepository;

import static com.shaya.poinila.android.util.ConstantsUtils.KEY_FIRST_LOGIN;

public class SelectInterestActivity extends BaseActivity {

    public static final int MINIMUM_ACCEPTABLE_INTERESTS = 5;
    private RecyclerViewAdapter<ImageTag, ?> mAdapter;
    //private HashSet<Integer> userInterestsIDs;
    private List<ImageTag> userInterests;
    private boolean firstTimeAfterRegister;
    private int checkedInterestCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: save and restore checkedInterestCount;
    }

    @Bind(R.id.selected_count)
    TextView checkedItemsCountTextView;
    @Bind(R.id.submit) Button submitBtn;
    @Bind(R.id.cancel) Button cancelBtn;
    @Bind(R.id.recycler_view) RecyclerView mRecyclerView;

    @Override
    protected void initUI() {
        mRecyclerView = new RecyclerViewProvider(mRecyclerView).
                setGridLayoutManager(GridLayoutManager.VERTICAL,
                        getResources().getInteger(R.integer.column_count)).
                setAdapter(new RecyclerViewAdapter<ImageTag, SelectableInterestViewHolder>(
                        getActivity(), R.layout.selectable_interest) {
                    @Override
                    protected SelectableInterestViewHolder getProperViewHolder(View v, int viewType) {
                        return new SelectableInterestViewHolder(v);
                    }
                }).
                bindViewToAdapter();
        mRecyclerView.getItemAnimator().setChangeDuration(0); // avoiding blink on rendering
        mAdapter = ((RecyclerViewAdapter<ImageTag, ?>) mRecyclerView.getAdapter());
        //TODO: handle first url
        userInterests = DataRepository.getInstance().getTempModel(List.class);
        if (userInterests == null)
            userInterests = new ArrayList<>(MINIMUM_ACCEPTABLE_INTERESTS);

        if (firstTimeAfterRegister){
            cancelBtn.setVisibility(View.GONE);
            updateCheckedInterestsCount();
        } else // we must show and update counter only directly after registering
            checkedItemsCountTextView.setVisibility(View.GONE);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (requestOnFirstTime)
            PoinilaNetService.getInterests();

        if (!firstTimeAfterRegister && userInterests.isEmpty())
            PoinilaNetService.getMemberInterests(DataRepository.getInstance().getMyId());
    }

    public List<ImageTag> selectUserInterests(List<ImageTag> receivedInterests) {
        if (!userInterests.isEmpty()) {
            for (ImageTag interest : receivedInterests) {
                interest.selected = userInterests.contains(interest);
                if (interest.selected)
                    PoinilaNetService.getSubInterests(interest.getId());
            }
        }
        return receivedInterests;
    }

    private void updateInterests() {
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.show();
        List<Integer> selectedInterests = new ArrayList<>();
        for (Object tag : mAdapter.getItems()) {
            if (((Tag)tag).selected)
                selectedInterests.add(((Tag)tag).id);
        }
        PoinilaNetService.updateUserInterests(selectedInterests);
    }

    @OnClick(R.id.submit)
    public void onSubmit() {
        if (firstTimeAfterRegister && !hasSelectedEnoughInterest(checkedInterestCount)) {
            Logger.toast(R.string.error_min_selected_interest);
            return;
        }
        updateInterests();
    }

    @OnClick(R.id.cancel)
    public void onCancel() { // when first time after register, cancel button is hidden
        finish();
    }

    // I hate my coding style! ServerResponse could be for any request :(
    @Subscribe public void onServerResponse(ServerResponseEvent event){
        progressDialog.dismiss();
        if (event.receiverName != BaseEvent.ReceiverName.SelectInterest)
            return;
        if (!event.succeed){
            Logger.toastError(R.string.error_add_interests);
            Logger.debugToast("updating interests failed");
        } else if (firstTimeAfterRegister) {
            progressDialog.setMessage(getString(R.string.loading_creating_suggestions));
            progressDialog.show();
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (progressDialog != null){
                        progressDialog.dismiss();
                    PageChanger.goToDashboard(SelectInterestActivity.this);
                }
            }
        }, 30000); // isn't it too much for user?
        }else{
            onCancel();
        }
    }

    /*-----------------------*/
    // important: either of responses might be received first so we call update function in each.
    // if app interest were not null and user interests were received, pre conditions is met.
    @Subscribe
    public void onInterestsReceived(InterestsReceivedEvent event) { // general interest! not related to the user
        requestOnFirstTime = false;
        if (!firstTimeAfterRegister)
            selectUserInterests(event.interests);
        mAdapter.addItems(event.interests);
    }

    @Subscribe
    public void onUserInterestsReceived(UserInterestsReceivedEvent event) {
        if (requestOnFirstTime) {
            userInterests = event.userInterests;
            selectUserInterests(mAdapter.getItems());
        }
    }
    /*------------------------*/

    @Subscribe
    public void onInterestChecked(CheckBoxClickUIEvent event) {
        ImageTag interest = mAdapter.getItem(event.adapterPosition);
        interest.selected = event.checked;

        userInterests.add(interest);

        mAdapter.notifyItemChanged(event.adapterPosition);
        if (firstTimeAfterRegister)
            updateCheckedInterestsCount();
        PoinilaNetService.getSubInterests(interest.getId());
    }

    private void updateCheckedInterestsCount() {
        int checkedInterestsCount = 0;
        for (Object imageTag : mAdapter.getItems()) {
            if (((ImageTag)imageTag).selected)
                checkedInterestsCount++;
        }
        this.checkedInterestCount = checkedInterestsCount;
        if (!hasSelectedEnoughInterest(checkedInterestsCount)) {
            ViewUtils.setText(checkedItemsCountTextView, StringUtils.getStringWithPersianNumber(
                    getString(R.string.x_outof_y_formatted), checkedInterestsCount, MINIMUM_ACCEPTABLE_INTERESTS));
            checkedItemsCountTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.flamingo));
        } else {
            ViewUtils.setText(checkedItemsCountTextView, StringUtils.persianNumber(checkedInterestsCount));
            checkedItemsCountTextView.setTextColor(ContextCompat.getColor(getActivity(), android.R.color.holo_green_dark));
        }
    }

    private boolean hasSelectedEnoughInterest(int checkedInterestsCount) {
        return checkedInterestsCount >= MINIMUM_ACCEPTABLE_INTERESTS;
    }

    @Override
    protected void handleToolbar() {
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_select_interests;
    }

    @Override
    protected void handleIntentExtras() {
        super.handleIntentExtras();
        firstTimeAfterRegister = getIntent().getBooleanExtra(KEY_FIRST_LOGIN, false);
    }
}
