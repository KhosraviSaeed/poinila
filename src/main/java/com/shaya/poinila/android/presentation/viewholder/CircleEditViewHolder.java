package com.shaya.poinila.android.presentation.viewholder;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.uievent.DeleteCircleUIEvent;
import com.shaya.poinila.android.presentation.uievent.EditCircleNameUIEvent;
import com.shaya.poinila.android.presentation.uievent.ViewCircleMembersUIEvent;
import com.shaya.poinila.android.presentation.view.ViewUtils;
import com.shaya.poinila.android.util.BusProvider;

import butterknife.Bind;
import butterknife.OnClick;
import data.model.Circle;

/**
 * Created by iran on 2015-07-28.
 */
public class CircleEditViewHolder extends BaseViewHolder<Circle>{
    @Bind(R.id.view_members)
    public ImageButton viewMembersBtn;
    @Bind(R.id.edit_name)
    public ImageButton editCircleNameBtn;
    @Bind(R.id.delete)
    public ImageButton deleteCircleBtn;
    @Bind(R.id.name)
    public TextView circleNameView;

    public CircleEditViewHolder(View view) {
        super(view);
    }

    @Override
    public void fill(Circle circle) {
        ViewUtils.setText(circleNameView, circle.name);
    }

    @OnClick(R.id.view_members) public void onViewMembers(){
        BusProvider.getBus().post(new ViewCircleMembersUIEvent(getAdapterPosition()));
    }

    @OnClick(R.id.edit_name) public void onEditCircleName(){
        BusProvider.getBus().post(new EditCircleNameUIEvent(getAdapterPosition()));
    }

    @OnClick(R.id.delete) public void onDeleteCircle(){
        BusProvider.getBus().post(new DeleteCircleUIEvent(getAdapterPosition()));
    }
}
