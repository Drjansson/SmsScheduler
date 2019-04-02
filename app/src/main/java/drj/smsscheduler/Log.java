package drj.smsscheduler;

import android.content.Context;
import android.nfc.Tag;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Calendar;

import static android.os.ParcelFileDescriptor.MODE_APPEND;

/**
 * Created by David on 2016-06-01.
 */

public class Log {
    private final String TAG = "Log";
    Context context;
    private final String fileName = "logActions.txt";

    public Log(Context context){
        this.context = context;
    }

    public void logInformation(String number, String message) {
        OutputStreamWriter out;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        StringBuilder logMessage = new StringBuilder("");

        logMessage.append(calendar.get(Calendar.YEAR));
        logMessage.append("-");
        logMessage.append(Utils.translateMonths(calendar.get(Calendar.MONTH)));
        logMessage.append("-");
        logMessage.append(Utils.padZero(calendar.get(Calendar.DAY_OF_MONTH)));
        logMessage.append(" ");
        logMessage.append(Utils.padZero(calendar.get(Calendar.HOUR_OF_DAY)));
        logMessage.append(":");
        logMessage.append(Utils.padZero(calendar.get(Calendar.MINUTE)));
        logMessage.append(" ");
        logMessage.append(number);
        logMessage.append("   ");
        logMessage.append(message);

        try {
            out = new OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_APPEND));
            out.write(logMessage.toString()+'\n');
            out.close();
        }catch (FileNotFoundException e){
            android.util.Log.e(TAG, e.getMessage());
        }catch (IOException e1){
            android.util.Log.e(TAG, e1.getMessage());
        }

    }

    public String getLogInformation(){
        //StringBuilder stringBuilder = new StringBuilder();
        String returnString = "";
        try {
            InputStream inputStream = context.openFileInput(fileName);

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader br = new BufferedReader(inputStreamReader);
                String tmpString = "";


                while ((tmpString = br.readLine()) != null) {
                    returnString = tmpString + "\n" + returnString;
                    //stringBuilder.append(tmpString);
                    //stringBuilder.append('\n');
                }

                inputStream.close();
            }
        }catch (Exception e){
            android.util.Log.e(TAG,e.getMessage());


        }

        return returnString; //stringBuilder.toString();
    }

    public void clear(){
        File file = new File(context.getFilesDir(), fileName);
        file.delete();
    }
}
