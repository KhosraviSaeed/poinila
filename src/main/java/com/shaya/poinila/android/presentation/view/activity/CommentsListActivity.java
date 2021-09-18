package com.shaya.poinila.android.presentation.view.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import com.shaya.poinila.android.presentation.PageChanger;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.presenter.RecyclerViewAdapter;
import com.shaya.poinila.android.presentation.presenter.RecyclerViewProvider;
import com.shaya.poinila.android.presentation.uievent.CommentLongClickUIEvent;
import com.shaya.poinila.android.presentation.uievent.MemberClickedUIEvent;
import com.shaya.poinila.android.presentation.uievent.RemoveItemUIEvent;
import com.shaya.poinila.android.presentation.uievent.UpdateUICommentEvent;
import com.shaya.poinila.android.presentation.viewholder.BaseViewHolder;
import com.shaya.poinila.android.presentation.viewholder.CommentViewHolder;
import com.shaya.poinila.android.util.BusProvider;
import com.shaya.poinila.android.util.Logger;
import com.squareup.otto.Subscribe;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import butterknife.Bind;
import butterknife.OnClick;
import data.PoinilaNetService;
import data.event.CommentReceivedEvent;
import data.event.CommentsReceivedEvent;
import data.model.Comment;
import data.model.Loading;
import manager.DBFacade;
import manager.DataRepository;

import static com.shaya.poinila.android.presentation.uievent.UpdateUICommentEvent.DECREMENT_COMMENTS;
import static com.shaya.poinila.android.presentation.uievent.UpdateUICommentEvent.INCREMENT_COMMENTS;
import static com.shaya.poinila.android.presentation.view.ViewUtils.setText;
import static com.shaya.poinila.android.presentation.view.ViewUtils.validateInputs;
import static com.shaya.poinila.android.util.ConstantsUtils.KEY_ITEM_COUNT;
import static com.shaya.poinila.android.util.ConstantsUtils.KEY_POST_ID;
import static com.shaya.poinila.android.util.ConstantsUtils.max_length_comment;
import static com.shaya.poinila.android.util.ConstantsUtils.min_length_comment;

// TODO: change to fragment host activity
public class CommentsListActivity extends ToolbarActivity {

    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerListView;

    @Bind(R.id.item_count)
    TextView mItemCountView;

    @Bind(R.id.comment_field)
    EditText commentInputView;

    @Bind(R.id.send) ImageButton sendCommentBtn;

    private String postID;
    private String bookmark;
    private int itemCount;

    protected boolean hasLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //if (savedInstanceState == null) {
        postID = getIntent().getStringExtra(KEY_POST_ID);
        /*}else{
            postID = savedInstanceState.getString(KEY_POST_ID);
        }*/
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(KEY_POST_ID, postID);
        super.onSaveInstanceState(outState);
    }


    @Subscribe public void onCommentsReceived(CommentsReceivedEvent event){
        getRecyclerViewAdapter().addItems(event.data);

        bookmark = event.bookmark;
        requestOnFirstTime = false;
    }

    public void setLoading(Loading loading){
        hasLoading = true;
        getRecyclerViewAdapter().setLoading(loading);
    }

    public void removeLoading(){
        if(hasLoading) {
            hasLoading = false;
            getRecyclerViewAdapter().removeLoading();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(requestOnFirstTime){
            DataRepository.getInstance().getPostComments(postID, bookmark);
        }
    }

    @Override
    protected void initUI() {
        updateCountView(itemCount);
        mRecyclerListView = new RecyclerViewProvider(mRecyclerListView).
                setLinearLayoutManager(LinearLayoutManager.VERTICAL).
                setAdapter(new RecyclerViewAdapter(getActivity(), R.layout.comment_large) {
                    @Override
                    protected BaseViewHolder getProperViewHolder(View v, int viewType) {

                        if(viewType == VIEW_TYPE_LOAD_PROGRESS){
                            return new BaseViewHolder.EmptyViewHolder(v);
                        }

                        return new CommentViewHolder(v);
                    }
                }).bindViewToAdapter();
        mRecyclerListView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity())
                .marginResId(R.dimen.margin_lvl1)
                .build());
        commentInputView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                sendCommentBtn.setEnabled(!s.toString().trim().isEmpty());
            }
        });
    }

    @OnClick(R.id.send) public void sendComment(){
        if (DataRepository.isUserAnonymous()){
            Logger.toastError(R.string.error_guest_action);
            return;
        }

        EditText[] commentEditTextArr = new EditText[]{commentInputView};
        if (validateInputs(commentEditTextArr, new int[min_length_comment], commentEditTextArr, new int[]{max_length_comment})) {
            PoinilaNetService.commentOnPost(postID, commentInputView.getText().toString());
            commentInputView.setText("");
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(commentInputView.getWindowToken(), 0);
        }
    }

    @Subscribe public void onCommentSucceeded(CommentReceivedEvent event){
//        Log.i(getClass().getName(), "commenter = " + DBFacade.getCachedMyInfo());
        event.comment.commenter = DBFacade.getCachedMyInfo();
        getRecyclerViewAdapter().addItem(event.comment);
        updateCountView(++itemCount);
        BusProvider.getBus().post(new UpdateUICommentEvent(INCREMENT_COMMENTS, postID));
        BusProvider.getSyncUIBus().post(new UpdateUICommentEvent(INCREMENT_COMMENTS, postID));
    }

    @Subscribe public void onCommentLongClick(final CommentLongClickUIEvent event){
        CommentAction.newInstance(event).show(getSupportFragmentManager(), null);
    }

    @Subscribe public void onDeleteComment(RemoveItemUIEvent event){
        PoinilaNetService.deleteComment(((Comment)getRecyclerViewAdapter().getItem(event.adapterPosition)).getId());
        getRecyclerViewAdapter().removeItem(event.adapterPosition);
        updateCountView(--itemCount);
        BusProvider.getBus().post(new UpdateUICommentEvent(DECREMENT_COMMENTS, postID));
        BusProvider.getSyncUIBus().post(new UpdateUICommentEvent(DECREMENT_COMMENTS, postID));

    }

    @Subscribe public void onGoingToProfile(MemberClickedUIEvent event){
        PageChanger.goToProfile(getActivity(),
                ((Comment)getRecyclerViewAdapter().getItem(event.adapterPosition)).commenter);
    }


    @Override
    protected void handleToolbar() {
        super.handleToolbar();
        setTitle(R.string.title_activity_comments_list);
    }

    @Override
    protected void handleIntentExtras() {
        itemCount = getIntent().getIntExtra(KEY_ITEM_COUNT, 0);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_comments;
    }

    public RecyclerViewAdapter getRecyclerViewAdapter() {
        return ((RecyclerViewAdapter) mRecyclerListView.getAdapter());
    }

    private void updateCountView(int itemCount) {
        setText(mItemCountView, getString(R.string.comments_formatted, itemCount));
    }



    public static class CommentAction extends DialogFragment{

        CommentLongClickUIEvent event;

        public static CommentAction newInstance(CommentLongClickUIEvent event){
            CommentAction fragment = new CommentAction();
            fragment.event = event;

            return fragment;
        }


        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setItems(getActivity().getResources().getStringArray(R.array.comment_options),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // The 'which' argument contains the index position
                            // of the selected item
                            BusProvider.getBus().post(new RemoveItemUIEvent(event.adapterPosition));
                        }
                    });
            return builder.create();
        }
    }
}
