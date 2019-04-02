package drj.smsscheduler;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by David on 2016-05-27.
 */

public class AlarmReceiver extends BroadcastReceiver {

    Context context;

    public AlarmReceiver(){
    }

    public AlarmReceiver(Context context){
        this.context = context;
    }



    @Override
    public void onReceive(Context context, Intent intent)
    {

        // here you can start an activity or service depending on your need
        // for ex you can start an activity to vibrate phone or to ring the phone

        ArrayList<String> phoneNumbers = intent.getStringArrayListExtra("Number");
        String message = intent.getStringExtra("Message");
        SendSMS sms = new SendSMS(context);
        for(int i = 0; i < phoneNumbers.size(); i++)
            sms.sendSMS(phoneNumbers.get(i), message);
        //Toast.makeText(context, "Alarm Triggered and SMS Sent", Toast.LENGTH_LONG).show();
    }
}
