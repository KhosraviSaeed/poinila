package com.shaya.poinila.android.presentation.view.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;

import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.util.Logger;

import data.PoinilaNetService;
import data.model.Collection;

import static com.shaya.poinila.android.util.ConstantsUtils.NO_RESOURCE;

/**
 * Created by iran on 7/5/2016.
 */
public class ReportDialog extends BusDialogFragment {

    private int title;
    private int memberIdOrPostId;


    public ReportDialog(){

    }

    public static ReportDialog newInstance(int title, int memberIdOrPostId){
        ReportDialog fragment = new ReportDialog();
        Bundle args = new Bundle();
        args.putInt("title", title);
        args.putInt("memberIdOrPostId", memberIdOrPostId);
        fragment.setArguments(args);
        return fragment;

    }


    @Override
    protected boolean sendsRequestAutomatically() {
        return false;
    }

    @Override
    protected void requestInitialData() {

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
        return R.layout.dialog_report;
    }

    @Override
    protected void loadStateFromBundle(Bundle savedInstanceState) {

    }

    @Override
    protected void saveStateToBundle(Bundle outState) {

    }

    @Override
    public void onPositiveButton() {
        PoinilaNetService.reportMemberOrPost(memberIdOrPostId);
        onNegativeButton();

        Logger.toast(R.string.report_successful_message);
    }

    @Override
    protected GeneralDialogData getDialogGeneralAttributes() {
        return new GeneralDialogData(
                getArguments().getInt("title"),
                NO_RESOURCE,
                R.string.report,
                R.string.cancel,
                NO_RESOURCE
                );
    }

    @Override
    protected void initUI(Context context) {
        memberIdOrPostId = getArguments().getInt("memberIdOrPostId");
    }

}
