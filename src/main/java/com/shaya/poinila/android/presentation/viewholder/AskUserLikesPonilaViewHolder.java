package com.shaya.poinila.android.presentation.viewholder;

import android.view.View;
import android.widget.Button;

import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.view.fragments.DashboardFragment.DashboardRecyclerViewAdapter.AskIfUserLikesPonila;

import butterknife.Bind;

/**
 * Created by iran on 1/20/2016.
 */
public class AskUserLikesPonilaViewHolder extends BaseViewHolder<AskIfUserLikesPonila> {
    @Bind(R.id.positive_button)
    public Button positiveButton;
    @Bind(R.id.negative_button)
    public Button negativeButton;
    @Bind(R.id.dont_know_button)
    public Button dontKnowButton;

    public AskUserLikesPonilaViewHolder(View view) {
        super(view);
    }


    @Override
    public void fill(AskIfUserLikesPonila askIfUserLikesPonila) {

    }


}
