package com.shaya.poinila.android.presentation.view.costom_view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageButton;

import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.uievent.ImageClickedUIEvent;
import com.shaya.poinila.android.presentation.uievent.PermissionEvent;
import com.shaya.poinila.android.presentation.view.ViewUtils;
import com.shaya.poinila.android.presentation.view.dialog.DialogLauncher;
import com.shaya.poinila.android.util.BusProvider;
import com.shaya.poinila.android.util.Logger;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnLongClick;

/**
 * Created by AlirezaF on 12/24/2015.
 */
public class EditCollectionImagePickerView extends GalleryCameraImagePickerView{
    @Bind(R.id.cover_from_posts) ImageButton pickCoverFromPostBtn;

    public EditCollectionImagePickerView(Context context) {
        this(context, null);
    }

    public EditCollectionImagePickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EditCollectionImagePickerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @OnLongClick public boolean showPickFromPostsHint(){
        Logger.toast(R.string.hint_coverFromPosts);
        return true;
    }

    @OnClick(R.id.cover_from_posts) public void onPickCoverFromPosts(){
        if (listener != null) listener.onPickCoverFromPosts();
    }


    @Override
    protected void init() {
        ViewUtils.setViewsVisibilityToVisible(findViewById(R.id.pick_from_posts_left_divider), pickCoverFromPostBtn);
        super.init();
    }

    OnPickCoverFromPostsListener listener;
    public interface OnPickCoverFromPostsListener{
        void onPickCoverFromPosts();
    }

    public void setOnPickCoverFromPostsListener(OnPickCoverFromPostsListener listener){
        this.listener = listener;
    }

}
