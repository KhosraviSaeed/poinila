package com.shaya.poinila.android.presentation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;

import com.shaya.poinila.android.presentation.uievent.SmsReceivedEvent;
import com.shaya.poinila.android.util.BusProvider;
import com.shaya.poinila.android.util.Logger;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import manager.DataRepository;

/**
 * Created by iran on 12/8/2015.
 */
public class SmsReceiver extends BroadcastReceiver {
    final SmsManager sms = SmsManager.getDefault();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (BuildConfig.DEBUG) {
            Logger.toast("Sms Received");
        }
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {

            Bundle bundle = intent.getExtras();
            SmsMessage[] msgs;
            if (bundle != null) {
                try {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];
                    String wholeString = "";
                    for (int i = 0; i < msgs.length; i++) {
                        msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        if(isPonilaNumber(msgs[i].getOriginatingAddress())){
                            wholeString += msgs[i].getMessageBody();
                        }
                    }

                    if(!TextUtils.isEmpty(wholeString)){
//                        Log.i(getClass().getName(), "wholeString = " + wholeString);
                        Pattern pattern = Pattern.compile("[0-9]+");
                        final Matcher matcher = pattern.matcher(wholeString);
                        if (matcher.find()) {
//                            Log.i(getClass().getName(), "matcher.group(0) = " + matcher.group(0));
                            String str = matcher.group(0);
                            if (str.length() >= 3) {
                                BusProvider.getBus().post(new SmsReceivedEvent(str));
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean isPonilaNumber(String address){
        List<String> numbers = DataRepository.getSMSProviderNumbers();
        for(String number : numbers){
            if(number.contains(address))
                return true;
        }
        return false;

    }
}
