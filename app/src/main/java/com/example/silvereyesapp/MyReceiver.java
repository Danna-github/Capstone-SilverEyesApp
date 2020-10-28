package com.example.silvereyesapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.TextView;

public class MyReceiver extends BroadcastReceiver {

    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private static final String TAG = "SmsBroadcastReceiver";
    String msg, phoneNo = "";

    @Override
    public void onReceive(Context context, Intent intent) {
        //retrieves the general action to be performed and display on log
        Log.i(TAG, "Intent Received: "+intent.getAction());
        if(intent.getAction() == SMS_RECEIVED){
            //retrieves a map of extended data from the intent
            Bundle dataBundle = intent.getExtras();
            if (dataBundle != null){
                //creating PDU(Protocol Data Unit) object which is a protocol for transferring message
                Object[] mypdu = (Object[])dataBundle.get("pdus");
                final SmsMessage[] messages = new SmsMessage[mypdu.length];

                for (int i=0; i<mypdu.length; i++)
                {
                    //for build version >= API Level 23
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    {
                        String format = dataBundle.getString("format");
                        //Form PDU we get all object and SmsMessage Object using following line of code
                        messages[i] = SmsMessage.createFromPdu((byte[])mypdu[i], format);
                    } else {
                        //<<API level 23
                        messages[i] = SmsMessage.createFromPdu((byte[]) mypdu[i]);
                    }
                    msg = messages[i].getMessageBody();
                    phoneNo = messages[i].getOriginatingAddress();
                }
                TextView tv = MessageFragment.messageTV;
                TextView numtv = MessageFragment.numberTV;
                tv.setText(msg);
                numtv.setText(phoneNo);

                //Toast.makeText(context, "Message: "+msg+"\nNumber: "+phoneNo,Toast.LENGTH_LONG).show();
            }
        }
    }
}
