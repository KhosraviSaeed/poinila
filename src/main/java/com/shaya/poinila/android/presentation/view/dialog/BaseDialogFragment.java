package com.shaya.poinila.android.presentation.view.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.widget.Space;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.util.ResourceUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.shaya.poinila.android.util.ConstantsUtils.NO_RESOURCE;

/**
 * Created by iran on 11/25/2015.
 */
public abstract class BaseDialogFragment extends android.support.v4.app.DialogFragment{
    @Bind(R.id.dialog_title)
    TextView titleView;
    @Bind(R.id.dialog_positive_button)
    Button positiveBtn;
    @Bind(R.id.dialog_negative_button) Button negativeBtn;
    @Bind(R.id.dialog_neutral_button) Button neutralBtn;
    @Bind(R.id.dialog_message) TextView messageView;
    LinearLayout container;
    @Bind(R.id.divider) View divider;
    @Bind(R.id.buttons_space_between1)
    Space spaceView1;
    @Bind(R.id.buttons_space_between2) Space spaceView2;

    protected static final int LAYOUT_NONE = -1;
    protected static final int RESOURCE_NONE = -1;
    protected ViewGroup rootView;

    public abstract @LayoutRes int getLayoutResId();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AppTheme_Dialog);//android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth);//R.style.AppTheme_Dialog);

        // restoring state - reading parameters
        Bundle state = null;
        if (savedInstanceState != null)
            state = savedInstanceState;
        else if (getArguments() != null)
            state = getArguments();
        if (state != null)
            loadStateFromBundle(state);


         /* Preventing loss of data on configuration change(like rotation).
         * May be saving data on a fragment object and retrieving by fragmentManager is a
         * better idea.
         *
         FragmentManager fm = getFragmentManager();
         dataFragment = (DataFragment) fm.findFragmentByTag(“data”);

         // create the fragment and data the first time
         if (dataFragment == null) {
         // add the fragment
         dataFragment = new DataFragment();
         fm.beginTransaction().add(dataFragment, “data”).commit();
         // load the data from the web
         dataFragment.setData(loadMyData());
         }

        //setRetainInstance(true); */
    }
/*
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // restoring state - reading parameters
        Bundle state = null;
        if (savedInstanceState != null)
            state = savedInstanceState;
        else if (getArguments() != null)
            state = getArguments();
        if (state != null)
            loadStateFromBundle(state);
    }*/

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveStateToBundle(outState);
    }

    protected abstract void loadStateFromBundle(Bundle savedInstanceState);

    /**
     * Saves the state of object in the passed bundle
     * @param outState Bundle in which dialog state would be saved
     */
    protected abstract void saveStateToBundle(Bundle outState);

/*    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.dialog_general, container, false);

        if (getLayoutResId() != LAYOUT_NONE) {
            View dialogView = inflater.inflate(getLayoutResId(), rootView, false);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
            lp.weight = 1;

            rootView.addView(dialogView, 2, lp);
        }

        ButterKnife.bind(this, rootView);

        //setDialogProperties();

        initDialogGeneralAttributes();

        initUI(getActivity());

        return rootView;
    }*/



    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Dialog dialog = super.onCreateDialog(savedInstanceState);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        rootView = (ViewGroup) inflater.inflate(R.layout.dialog_general, container, false);
        if (getLayoutResId() != LAYOUT_NONE) {
            View dialogView = inflater.inflate(getLayoutResId(), rootView, false);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(MATCH_PARENT, 0);
            lp.weight = 1;

            rootView.addView(dialogView, 2, lp);
        }
        ButterKnife.bind(this, rootView);
        initUI(getActivity());
        initDialogGeneralAttributes();

        Dialog dialog= new AlertDialog.Builder(getActivity()).setView(rootView).create();

        setDialogProperties(dialog);

        return dialog;
    }

    private void setDialogProperties(Dialog dialog) {
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    private void initDialogGeneralAttributes() {
        GeneralDialogData data = getDialogGeneralAttributes();
        if (data.title != null)
            titleView.setText(data.title);
        else {
            titleView.setVisibility(View.GONE);
            divider.setVisibility(View.GONE);
        }

        if (data.message != null)
            messageView.setText(data.message);
        else
            messageView.setVisibility(View.GONE);

        /*---Buttons----*/

        // pos
        if (data.positiveButtonText != null) {
            positiveBtn.setText(data.positiveButtonText);
        } else{
            positiveBtn.setVisibility(View.GONE);
        }

        // neutral
        if (data.neutralButtonText != null) {
            neutralBtn.setVisibility(View.VISIBLE);
            neutralBtn.setText(data.neutralButtonText);
            spaceView1.setVisibility(View.VISIBLE);
        }

        // neg
        if (data.negativeButtonText!= null)
            negativeBtn.setText(data.negativeButtonText);
        else{
            spaceView2.setVisibility(View.GONE);
            negativeBtn.setVisibility(View.GONE);
        }
    }

    protected abstract GeneralDialogData getDialogGeneralAttributes();

    protected abstract void initUI(Context context);

    @OnClick(R.id.dialog_positive_button) public void onPositiveButton(){
        dismiss();
    }

    @OnClick(R.id.dialog_neutral_button) public void onNeutralButton(){
        dismiss();
    }

    @OnClick(R.id.dialog_negative_button) public void onNegativeButton(){
        dismiss();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    public static class GeneralDialogData {
        public final String title;
        public final String message;
        public final String positiveButtonText;
        public final String negativeButtonText;
        public final String neutralButtonText;

        GeneralDialogData(String title, String message, String positiveButtonText, String negativeButtonText, String neutralButtonText){
            this.title = title;
            this.message = message;
            this.positiveButtonText = positiveButtonText;
            this.negativeButtonText = negativeButtonText;
            this.neutralButtonText = neutralButtonText;
        }

        GeneralDialogData(@StringRes int titleRes, @StringRes int messageRes, @StringRes int positiveButtonTextRes
                , @StringRes int negativeButtonTextRes, @StringRes int neutralButtonTextRes){
            this.title = titleRes != NO_RESOURCE ? ResourceUtils.getString(titleRes) : null;
            this.message = messageRes != NO_RESOURCE ? ResourceUtils.getString(messageRes) : null;
            this.positiveButtonText = positiveButtonTextRes != NO_RESOURCE ? ResourceUtils.getString(positiveButtonTextRes) : null;
            this.negativeButtonText = negativeButtonTextRes != NO_RESOURCE ? ResourceUtils.getString(negativeButtonTextRes) : null;
            this.neutralButtonText = neutralButtonTextRes != NO_RESOURCE ? ResourceUtils.getString(neutralButtonTextRes) : null;
        }
    }
}
