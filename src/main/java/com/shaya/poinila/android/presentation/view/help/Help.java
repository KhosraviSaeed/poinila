package com.shaya.poinila.android.presentation.view.help;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Rect;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.view.fragments.BaseFragment;
import com.shaya.poinila.android.presentation.view.help.masks.BaseMaskView;
import com.shaya.poinila.android.presentation.view.help.masks.CollectionMaskView;
import com.shaya.poinila.android.presentation.view.help.masks.CreateMaskView;
import com.shaya.poinila.android.presentation.view.help.masks.DashboardMaskView;
import com.shaya.poinila.android.presentation.view.help.masks.PostRelatedPostMaskView;
import com.shaya.poinila.android.presentation.view.help.masks.PostsOfCollectionMaskView;
import com.shaya.poinila.android.presentation.view.help.masks.ProfileMaskView;

/**
 * Created by iran on 5/23/2016.
 */
public class Help {
    private static Help instance = null;
    private boolean showingHelp  = false;
    private BaseFragment mPage;

    private Dialog help;

    private Help(){

    }

    public static Help getInstance(){
        if(instance == null){
            instance = new Help();
        }
        return instance;
    }

    public void showDashboardHelp(Activity activity, View itemView){

        DashboardMaskView helpView = new DashboardMaskView(activity, itemView);
        initDialog(activity, helpView);

    }

    public void showPostRelatedPostsHelp(Activity activity, View itemView){

        PostRelatedPostMaskView helpView = new PostRelatedPostMaskView(activity, itemView);
        initDialog(activity, helpView);

    }

    public void showFollowedCollectionHelp(Activity activity, View itemView){

        CollectionMaskView helpView = new CollectionMaskView(activity, itemView);
        initDialog(activity, helpView);
    }

    public void showProfileHelp(Activity activity, View itemView){

        ProfileMaskView helpView = new ProfileMaskView(activity, itemView);
        initDialog(activity, helpView);

    }

    public void showNewPostHelp(Activity activity, View itemView){

        CreateMaskView helpView = new CreateMaskView(activity, itemView);
        initDialog(activity, helpView);
    }

    public void showPostsOfCollectionHelp(Activity activity, View itemView){

        PostsOfCollectionMaskView helpView = new PostsOfCollectionMaskView(activity, itemView);
        initDialog(activity, helpView);
    }

    private void initDialog(Activity activity, BaseMaskView helpView){
        if(!isShowingHelp()){

            helpView.setStatusBarHeight(getStatusBarHeight(activity.getWindow()));

            help = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
            help.requestWindowFeature(Window.FEATURE_NO_TITLE);
            help.setContentView(helpView);
            help.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            help.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            help.show();

            showingHelp = true;

            helpView.setOnNextBtnListener(new OnNextButtonListener() {
                @Override
                public void onClick(View view) {
                    help.dismiss();
                    showingHelp = false;
                }
            });
        }

    }

    public boolean isShowingHelp(){
        return showingHelp;
    }

    private int getStatusBarHeight(Window window){
        Rect rectangle = new Rect();
        window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
        return rectangle.top;
    }



}
