package com.shaya.poinila.android.presentation.view.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.presenter.RecyclerViewAdapter;
import com.shaya.poinila.android.presentation.presenter.RecyclerViewProvider;
import com.shaya.poinila.android.presentation.uievent.DeleteCircleUIEvent;
import com.shaya.poinila.android.presentation.uievent.EditCircleNameUIEvent;
import com.shaya.poinila.android.presentation.uievent.PositiveButtonClickedUIEvent;
import com.shaya.poinila.android.presentation.uievent.SimpleSettingTextSetEvent;
import com.shaya.poinila.android.presentation.uievent.ViewCircleMembersUIEvent;
import com.shaya.poinila.android.presentation.view.dialog.ChangeCircleNameDialog;
import com.shaya.poinila.android.presentation.view.dialog.CircleMembersManagementDialog;
import com.shaya.poinila.android.presentation.view.dialog.NewCircleDialog;
import com.shaya.poinila.android.presentation.view.dialog.PoinilaAlertDialog;
import com.shaya.poinila.android.presentation.viewholder.CircleEditViewHolder;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import data.PoinilaNetService;
import data.event.CircleReceivedEvent;
import data.model.Circle;
import manager.DBFacade;

public class CirclesManagementActivity extends ToolbarActivity {

    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    private RecyclerViewAdapter<Circle, CircleEditViewHolder> mAdapter;
    private int clickedCirclePosition;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    @Override
    protected void initUI() {
        mRecyclerView = new RecyclerViewProvider(mRecyclerView).
                setLinearLayoutManager(LinearLayoutManager.VERTICAL).
                setAdapter(new RecyclerViewAdapter<Circle, CircleEditViewHolder>(getActivity(), R.layout.circle_edit_item) {
                    @Override
                    protected CircleEditViewHolder getProperViewHolder(View v, int viewType) {
                        return new CircleEditViewHolder(v);
                    }
                }).bindViewToAdapter();
        mAdapter = (RecyclerViewAdapter<Circle, CircleEditViewHolder>) mRecyclerView.getAdapter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_circles_management, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_add_circle){
            new NewCircleDialog().show(getSupportFragmentManager(), null);
        }
        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAdapter.resetData(DBFacade.getMyCircles());

    }

    @Subscribe public void onViewMembersEvent(ViewCircleMembersUIEvent event){
        String circleID = mAdapter.getItem(event.adapterPosition).getId();
        CircleMembersManagementDialog.newInstance(circleID).show(getSupportFragmentManager(), null);
    }

    @Subscribe public void onEditCircleNameClick(final EditCircleNameUIEvent event){
        clickedCirclePosition = event.adapterPosition;
        ChangeCircleNameDialog.newInstance(mAdapter.getItem(clickedCirclePosition).name).show(getSupportFragmentManager(), null);
    }

    @Subscribe public void onCircleChange(SimpleSettingTextSetEvent event){
        switch (event.settingType) {
            case CIRCLE_NAME:
                Circle editedCircle = mAdapter.getItem(clickedCirclePosition);
                editedCircle.name = event.value;
                mAdapter.notifyItemChanged(clickedCirclePosition);
                PoinilaNetService.updateCircle(editedCircle);
                break;
            case NEW_CIRCLE:
                PoinilaNetService.createCircle(event.value);
                break;
        }
    }

    @Subscribe public void onDeleteCircle(DeleteCircleUIEvent event){
        clickedCirclePosition = event.adapterPosition;
        new PoinilaAlertDialog.Builder().setMessage(R.string.confirm_delete_circle).
                setPositiveBtnText(R.string.yes).setNegativeBtnText(R.string.no).
                build().show(getSupportFragmentManager(), null);
    }

    @Subscribe
    public void onPositiveDialogButton(PositiveButtonClickedUIEvent event) {
        PoinilaNetService.deleteCircle(mAdapter.getItem(clickedCirclePosition));
        mAdapter.removeItem(clickedCirclePosition);
    }


    @Subscribe public void onCircleReceived(CircleReceivedEvent event){
        mAdapter.addItem(event.circle, 0);
    }

    @Override
    protected void handleToolbar() {
        super.handleToolbar();
        setTitle(R.string.title_activity_circles_management);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_circle_management;
    }
}
