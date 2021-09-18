package com.shaya.poinila.android.utils;

import android.content.Context;
import android.graphics.Typeface;

import com.shaya.poinila.android.presentation.PoinilaApplication;

import java.lang.reflect.Field;

/**
 * Created by iran on 2015-06-03.
 * @author Alireza Farahani
 *
 * Change the entire Application by editting {@link Typeface} class using
 * reflection.
 */
public class TypefaceUtil {

    private static Typeface iransans;

    static {
        iransans = Typeface.createFromAsset(PoinilaApplication.getAppContext().getAssets(), "fonts/iransans.ttf");
    }

    public static Typeface getIranSansFont(){
        return iransans;
    }


}
