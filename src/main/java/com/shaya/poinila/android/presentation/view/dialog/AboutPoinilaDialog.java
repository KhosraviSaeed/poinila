package com.shaya.poinila.android.presentation.view.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.shaya.poinila.android.presentation.BuildConfig;
import com.shaya.poinila.android.presentation.R;

/**
 * Created by iran on 2015-11-04.
 */
public class AboutPoinilaDialog extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        PackageInfo packageInfo = null;
        try {
            packageInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        builder.setPositiveButton(R.string.ok,null)
                .setTitle(R.string.about_poinila)
                .setMessage(Html.fromHtml(getString(R.string.about_version_contact, BuildConfig.VERSION_NAME)))
                .setCancelable(false);
                //.setIcon(R.drawable.logo_full);

        Dialog d = builder.create();
        d.setCancelable(true);
        d.setCanceledOnTouchOutside(false);
        return d;
    }

    @Override
    public void onStart() {
        super.onStart();
        ((TextView) getDialog().findViewById(android.R.id.message))
                .setMovementMethod(LinkMovementMethod.getInstance());
    }
}
