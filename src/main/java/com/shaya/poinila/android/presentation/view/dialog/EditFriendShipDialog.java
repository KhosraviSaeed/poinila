package com.shaya.poinila.android.presentation.view.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.presenter.RecyclerViewAdapter;
import com.shaya.poinila.android.presentation.view.ViewUtils;
import com.shaya.poinila.android.presentation.viewholder.SimpleTextViewHolder;

import org.parceler.Parcels;

import java.util.List;

import butterknife.Bind;
import data.PoinilaNetService;
import data.model.Circle;
import data.model.FriendshipStatus;
import data.model.Member;
import manager.DBFacade;

import static com.shaya.poinila.android.presentation.view.ViewUtils.setText;

/**
 * Created by iran on 12/28/2015.
 */
public class EditFriendShipDialog extends BusDialogFragment {

    @Bind(R.id.llcontainer)
    LinearLayout mLinearLayoutContainer;

    private static final String KEY_FRIEND = "friend";
    private Member friend;

    public static EditFriendShipDialog newInstance(Member friend) {
        Bundle args = new Bundle();
        EditFriendShipDialog fragment = new EditFriendShipDialog();
        fragment.friend = friend;
        fragment.saveStateToBundle(args);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected boolean sendsRequestAutomatically() {
        return true;
    }

    @Override
    protected void requestInitialData() {
        List<Circle> allCircles = DBFacade.getMyCircles();
        onGettingInitDataResponse(null);
        if (friend.circle_ids == null)
            return;
        for (Circle circle : allCircles) {
            if (friend.circle_ids.contains(circle.id)) {
                TextView textView = (TextView) getActivity().getLayoutInflater().inflate(R.layout.simple_textview, mLinearLayoutContainer, false);
                setText(textView, circle.name);
                mLinearLayoutContainer.addView(textView);
            }
        }
    }

    @Override
    public ViewGroup getLoadableView() {
        return null;
    }


    @Override
    public boolean mustShowProgressView() {
        return false;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.scrollable_linearlayout;
    }

    @Override
    protected void loadStateFromBundle(Bundle savedInstanceState) {
        this.friend = Parcels.unwrap(savedInstanceState.getParcelable(KEY_FRIEND));
    }

    @Override
    protected void saveStateToBundle(Bundle outState) {
        outState.putParcelable(KEY_FRIEND, Parcels.wrap(friend));
    }

    @Override
    protected GeneralDialogData getDialogGeneralAttributes() {
        return new GeneralDialogData(R.string.edit_friendship, RESOURCE_NONE,
                R.string.remove_friend, R.string.cancel, R.string.edit_circle);
    }

    @Override
    protected void initUI(Context context) {

    }

    @Override
    public void onPositiveButton() {
        PoinilaNetService.removeFriend(friend.getId());
        friend.friendshipStatus = FriendshipStatus.NotFriend;
        super.onPositiveButton();
    }

    @Override
    public void onNeutralButton() {
        DialogLauncher.launchChangeFriendCircle(
                getFragmentManager(), friend);
        super.onNeutralButton();
    }
}