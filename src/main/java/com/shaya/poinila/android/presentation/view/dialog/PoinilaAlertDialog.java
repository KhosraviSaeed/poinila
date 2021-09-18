package com.shaya.poinila.android.presentation.view.dialog;

import android.content.Context;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.view.View;

import com.shaya.poinila.android.presentation.uievent.NeutralDialogButtonClickedUIEvent;
import com.shaya.poinila.android.presentation.uievent.PositiveButtonClickedUIEvent;
import com.shaya.poinila.android.util.BusProvider;
import com.shaya.poinila.android.util.ResourceUtils;

/**
 * Created by iran on 12/22/2015.
 */
public abstract class PoinilaAlertDialog extends BaseDialogFragment{


    private View.OnClickListener positiveBtnClickListener;

    @Override
    public int getLayoutResId() {
        return LAYOUT_NONE;
    }

    @Override
    protected void loadStateFromBundle(Bundle savedInstanceState) {

    }

    @Override
    protected void saveStateToBundle(Bundle outState) {

    }

    @Override
    public void onPositiveButton() {
        if (positiveBtnClickListener == null)
            BusProvider.getBus().post(new PositiveButtonClickedUIEvent());
        else
            positiveBtnClickListener.onClick(null);
        super.onPositiveButton();
    }

    @Override
    public void onNeutralButton() {
        BusProvider.getBus().post(new NeutralDialogButtonClickedUIEvent());
        super.onNeutralButton();
    }

    @Override
    protected void initUI(Context context) {

    }

    public void setOnPositiveClickListener(View.OnClickListener onPositiveClickListener) {
        this.positiveBtnClickListener = onPositiveClickListener;
    }

    public static class Builder{
        String title;
        String message;
        String positiveBtnText;
        String negativeBtnText;
        String neutralBtnText;
        View.OnClickListener positiveBtnClickListener;

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setPositiveBtnText(String positiveBtnText) {
            this.positiveBtnText = positiveBtnText;
            return this;
        }

        public Builder setNegativeBtnText(String negativeBtnText) {
            this.negativeBtnText = negativeBtnText;
            return this;
        }

        public Builder setNeutralBtnText(String neutralBtnText) {
            this.neutralBtnText = neutralBtnText;
            return this;
        }

        public Builder setTitle(@StringRes int titleRes) {
            return setTitle(ResourceUtils.getString(titleRes));
        }

        public Builder setMessage(@StringRes int messageRes) {
            return setMessage(ResourceUtils.getString(messageRes));
        }

        public Builder setPositiveBtnText(@StringRes int positiveBtnText) {
            return setPositiveBtnText(ResourceUtils.getString(positiveBtnText));
        }

        public Builder setNegativeBtnText(@StringRes int negativeBtnText) {
            return setNegativeBtnText(ResourceUtils.getString(negativeBtnText));
        }

        public Builder setNeutralBtnText(@StringRes int neutralBtnText) {
            return setNeutralBtnText(ResourceUtils.getString(neutralBtnText));
        }

        public Builder setPositiveBtnClickListener(View.OnClickListener listener){
            this.positiveBtnClickListener = listener;
            return this;
        }

        public PoinilaAlertDialog build() {
            PoinilaAlertDialog pad = CustomPonilaAlertDialog.newInstance(title, message, positiveBtnText, negativeBtnText, neutralBtnText, positiveBtnClickListener);
            pad.setOnPositiveClickListener(positiveBtnClickListener);
            return pad;
        }
    }


    public static class CustomPonilaAlertDialog extends PoinilaAlertDialog{

        String title;
        String message;
        String positiveBtnText;
        String negativeBtnText;
        String neutralBtnText;
        View.OnClickListener positiveBtnClickListener;

        public static CustomPonilaAlertDialog newInstance(String title,
                                                          String message,
                                                          String positiveBtnText,
                                                          String negativeBtnText,
                                                          String neutralBtnText,
                                                          View.OnClickListener positiveBtnClickListener){

            CustomPonilaAlertDialog fragment = new CustomPonilaAlertDialog();

            fragment.title = title;
            fragment.message = message;
            fragment.positiveBtnText = positiveBtnText;
            fragment.negativeBtnText = negativeBtnText;
            fragment.neutralBtnText = neutralBtnText;
            fragment.positiveBtnClickListener = positiveBtnClickListener;


            return fragment;

        }

        @Override
        protected GeneralDialogData getDialogGeneralAttributes() {
            return new GeneralDialogData(title, message, positiveBtnText, negativeBtnText, neutralBtnText);
        }
    }
}
