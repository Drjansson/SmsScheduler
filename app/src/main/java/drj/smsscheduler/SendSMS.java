package drj.smsscheduler;

import android.content.Context;
import android.telephony.SmsManager;
import android.widget.Toast;

/**
 * Created by David on 2016-05-28.
 */

public class SendSMS {

    Context context;

    public SendSMS(Context context){
        this.context = context;
    }


    public void sendSMS(String number, String message){
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(number, null, message, null, null);

        Log log = new Log(context);
        log.logInformation(number, message);

        Toast.makeText(context, "SMS Sent to " + number, Toast.LENGTH_LONG).show();
    }

}
