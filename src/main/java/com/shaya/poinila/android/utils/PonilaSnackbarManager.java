package com.shaya.poinila.android.utils;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.view.dialog.DialogLauncher;
import com.shaya.poinila.android.util.ConstantsUtils;
import com.shaya.poinila.android.util.Logger;

import static com.shaya.poinila.android.util.ResourceUtils.*;

/**
 * Created by iran on 7/17/2016.
 */
public class PonilaSnackbarManager {

    private static PonilaSnackbarManager instance;

    public static final int PONILA_SNACKBAR_DEFAULT = 1;
    public static final int PONILA_SNACKBAR_WITH_BUTTON = 2;

    private PonilaSnackbarManager(){

    }

    public static PonilaSnackbarManager getInstance(){
        if(instance == null)
            instance = new PonilaSnackbarManager();

        return instance;
    }

    public void showVerifySnackbar(View parentView, final FragmentActivity activity){

        Snackbar snackbar = Snackbar
                .make(parentView, R.string.snackbar_verify_message, Snackbar.LENGTH_INDEFINITE)
                .setActionTextColor(activity.getResources().getColor(R.color.white))
                .setAction(R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DialogLauncher.launchInputVerificationCodeDialog(activity.getSupportFragmentManager(), "", false);
                    }
                });

        Snackbar.SnackbarLayout rootView = (Snackbar.SnackbarLayout)snackbar.getView();

        rootView.setGravity(Gravity.CENTER);

        Button button = (Button)rootView.findViewById(android.support.design.R.id.snackbar_action);

        button.setBackgroundResource(R.drawable.snackbar_action);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                (int)activity.getResources().getDimension(R.dimen.snackbar_action_height));

        button.setLayoutParams(layoutParams);
        button.setPadding(0, 0, 0, 0);

        snackbar.show();

    }

    public void showChangeUserPassSnackBar(View parentView, final FragmentActivity activity){

        Snackbar snackbar = Snackbar
                .make(parentView, R.string.change_user_pass_title, Snackbar.LENGTH_INDEFINITE)
                .setActionTextColor(activity.getResources().getColor(R.color.white))
                .setAction(R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DialogLauncher.launchSetUsernamePasswordDialog(activity.getSupportFragmentManager());                    }
                });

        Snackbar.SnackbarLayout rootView = (Snackbar.SnackbarLayout)snackbar.getView();

        rootView.setGravity(Gravity.CENTER);

        Button button = (Button)rootView.findViewById(android.support.design.R.id.snackbar_action);

        button.setBackgroundResource(R.drawable.snackbar_action);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                (int)activity.getResources().getDimension(R.dimen.snackbar_action_height));

        button.setLayoutParams(layoutParams);
        button.setPadding(0, 0, 0, 0);

        snackbar.show();
    }
//
//    private View customSnackBar(Context context, int type, int message, int btnText){
//        LinearLayout snackBarView = (LinearLayout)LayoutInflater.from(context).inflate(R.layout.ponila_snackbar, null);
//        Button btn = (Button)snackBarView.findViewById(R.id.ponila_snackbar_btn);
//        TextView textView = (TextView)snackBarView.findViewById(R.id.ponila_snackbar_text);
//
//        switch (type){
//            case PONILA_SNACKBAR_DEFAULT:
//                btn.setVisibility(View.GONE);
//                break;
//            case PONILA_SNACKBAR_WITH_BUTTON:
//                btn.setVisibility(View.VISIBLE);
//                break;
//        }
//
//        btn.setText(btnText != ConstantsUtils.NO_RESOURCE ? getString(btnText) : "");
//        textView.setText(message != ConstantsUtils.NO_RESOURCE ? getString(message) : "");
//
//
//        return snackBarView;
//    }

}
