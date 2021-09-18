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
import com.shaya.poinila.android.presentation.uievent.DeleteFrameUIEvent;
import com.shaya.poinila.android.presentation.uievent.EditFrameNameUIEvent;
import com.shaya.poinila.android.presentation.uievent.PositiveButtonClickedUIEvent;
import com.shaya.poinila.android.presentation.uievent.SimpleSettingTextSetEvent;
import com.shaya.poinila.android.presentation.uievent.ViewFrameMembersUIEvent;
import com.shaya.poinila.android.presentation.view.dialog.ChangeFrameNameDialog;
import com.shaya.poinila.android.presentation.view.dialog.FrameCollectionsManagementDialog;
import com.shaya.poinila.android.presentation.view.dialog.NewFrameDialog;
import com.shaya.poinila.android.presentation.view.dialog.PoinilaAlertDialog;
import com.shaya.poinila.android.presentation.viewholder.FrameEditViewHolder;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import data.PoinilaNetService;
import data.event.FrameReceivedEvent;
import data.model.Frame;
import manager.DBFacade;

public class FramesManagementActivity extends ToolbarActivity {

    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    private RecyclerViewAdapter<Frame, ?> mAdapter;
    private int clickedFramePosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initUI() {
        mRecyclerView = new RecyclerViewProvider(mRecyclerView).
                setLinearLayoutManager(LinearLayoutManager.VERTICAL).
                setAdapter(new RecyclerViewAdapter<Frame, FrameEditViewHolder>(getActivity(), R.layout.circle_edit_item) {
                    @Override
                    protected FrameEditViewHolder getProperViewHolder(View v, int viewType) {
                        return new FrameEditViewHolder(v);
                    }
                }).bindViewToAdapter();
        mAdapter = (RecyclerViewAdapter<Frame, ?>)mRecyclerView.getAdapter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_frame_management, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_add_circle){
            new NewFrameDialog().show(getSupportFragmentManager(), null);
        }
        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAdapter.resetData(DBFacade.getMyFrames());
    }

    @Subscribe public void onViewCollectionsEvent(ViewFrameMembersUIEvent event){
        String frameID = mAdapter.getItem(event.adapterPosition).getId();
        FrameCollectionsManagementDialog.newInstance(frameID).show(getSupportFragmentManager(), null);//ConstantsUtils.TAG_FRAME_MEMBERS_MANAGEMENT);
    }


    @Subscribe public void onEditFrameNameClick(final EditFrameNameUIEvent event){
        clickedFramePosition = event.adapterPosition;
        ChangeFrameNameDialog.newInstance(mAdapter.getItem(clickedFramePosition).name).show(getSupportFragmentManager(), null);
    }

    @Subscribe public void onDialogPositiveBtn(SimpleSettingTextSetEvent event){
        switch (event.settingType) {
            case FRAME_NAME:
                Frame editedFrame = mAdapter.getItem(clickedFramePosition);
                editedFrame.name = event.value;
                mAdapter.notifyItemChanged(clickedFramePosition);
                // TODO: ghaedatan tu response server bayad save she!
                PoinilaNetService.updateFrame(editedFrame);
                break;
            case NEW_FRAME:
                PoinilaNetService.createFrame(event.value);
                break;
        }
    }

    @Subscribe public void onDeleteFrame(DeleteFrameUIEvent event){
        clickedFramePosition = event.adapterPosition;
        new PoinilaAlertDialog.Builder().setMessage(R.string.confirm_delete_frame).
                setPositiveBtnText(getString(R.string.yes)).setNegativeBtnText(getString(R.string.no)). ///*setBody(new DeleteFrameDialog(mAdapter.getItem(event.adapterPosition))).build().*/
                build().show(getSupportFragmentManager(), null);
    }

    @Subscribe
    public void onPositiveDialogButtonClick(PositiveButtonClickedUIEvent event) {
        PoinilaNetService.deleteFrame(mAdapter.getItem(clickedFramePosition));
        mAdapter.removeItem(clickedFramePosition);
    }

    @Subscribe public void onFrameReceived(FrameReceivedEvent event){
        mAdapter.addItem(event.frame, 0);
    }

    @Override
    protected void handleToolbar() {
        super.handleToolbar();
        setTitle(R.string.title_activity_frames_management);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_circle_management;
    }
}
