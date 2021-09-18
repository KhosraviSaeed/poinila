package com.shaya.poinila.android.presentation.view.help.fragments;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.shaya.poinila.android.presentation.R;
import com.shaya.poinila.android.presentation.view.help.masks.CreateMaskView;

/**
 * Created by iran on 5/24/2016.
 */
public class CreateHelpFragment extends DialogFragment {

    private View view;

    public CreateHelpFragment(){

    }

    public static CreateHelpFragment newInstance(View view){
        CreateHelpFragment dialogFragment = new CreateHelpFragment();
        dialogFragment.view = view;
        return dialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.gray_transparent)));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return view;
    }

    public void show(FragmentManager manager) {
//        mDismissed = false;
//        mShownByMe = true;
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, getClass().getName());
        ft.commit();
    }


}
