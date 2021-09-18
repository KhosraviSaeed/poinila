package com.shaya.poinila.android.presentation.viewholder;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.uievent.DeleteFrameUIEvent;
import com.shaya.poinila.android.presentation.uievent.EditFrameNameUIEvent;
import com.shaya.poinila.android.presentation.uievent.ViewFrameMembersUIEvent;
import com.shaya.poinila.android.util.BusProvider;

import butterknife.Bind;
import butterknife.OnClick;
import data.model.Frame;

import static com.shaya.poinila.android.presentation.view.ViewUtils.setText;

/**
 * Created by iran on 2015-07-28.
 */
public class FrameEditViewHolder extends BaseViewHolder<Frame>{
    @Bind(R.id.view_members)
    public ImageButton viewFrameCollectionsBtn;
    @Bind(R.id.edit_name)
    public ImageButton editCircleNameBtn;
    @Bind(R.id.delete)
    public ImageButton deleteCircleBtn;
    @Bind(R.id.name)
    public TextView frameNameView;

    public FrameEditViewHolder(View view) {
        super(view);
        viewFrameCollectionsBtn.setImageResource(R.drawable.collection_white);
    }

    @Override
    public void fill(Frame frame) {
        setText(frameNameView, frame.name);
    }

    @OnClick(R.id.view_members) public void onViewCollections(){
        BusProvider.getBus().post(new ViewFrameMembersUIEvent(getAdapterPosition()));
    }

    @OnClick(R.id.edit_name) public void onEditFrameName(){
        BusProvider.getBus().post(new EditFrameNameUIEvent(getAdapterPosition()));
    }

    @OnClick(R.id.delete) public void onDeleteFrame(){
        BusProvider.getBus().post(new DeleteFrameUIEvent(getAdapterPosition()));
    }
}