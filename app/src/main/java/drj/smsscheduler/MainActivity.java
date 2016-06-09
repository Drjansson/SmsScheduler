package drj.smsscheduler;

import android.Manifest;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    final private int REQUEST_CODE_ASK_PERMISSIONS = 51263;
    final private int PICK_CONTACT_REQUEST = 1;

    private EditText txtMain;
    private TextView txtDate;
    private ArrayList<String> numbers = new ArrayList<String>();
    private ArrayList<String> names = new ArrayList<String>();

    private AlarmManager alarmManager;
    private Calendar calendar;
    private PendingIntent pendingIntentAlarm;

    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;

    private boolean timeChoosen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        Toolbar mToolBar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(mToolBar);

        mToolBar.setBackgroundColor(ContextCompat.getColor(this,android.R.color.holo_blue_light));

        txtDate = (TextView) findViewById(R.id.txtDate);
        Button btnNumber = (Button) findViewById(R.id.btnNumber);


        txtMain = (EditText) findViewById(R.id.txtMain);

        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String formattedDate = df.format(calendar.getTime());
        // Now we display formattedDate value in TextView
        if(timeChoosen) {
            txtDate.setText("Scheduled time to send SMS: : " + formattedDate);
        }else{
            txtDate.setText("The SMS will be send directly, click to schedule.");
        }
        txtDate.setGravity(Gravity.CENTER);
        txtDate.setTextSize(20);
        //setContentView(txtView);

        txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeChoosen = true;
                showDatePickerDialog();

            }
        });

        btnNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent contactIntent = new Intent(MainActivity.this, Contacts.class);
                contactIntent.putStringArrayListExtra("Numbers", numbers);
                contactIntent.putStringArrayListExtra("Names", names);
                startActivityForResult(contactIntent, PICK_CONTACT_REQUEST);
            }
        });

    }


    private void showTimePickerDialog() {
        DialogFragment newFragment = new DisplayTimePicker();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    private void showDatePickerDialog() {
        DialogFragment newFragment = new DisplayDatePicker();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String formattedDate = df.format(calendar.getTime());
        txtDate.setText("Scheduled time to send SMS: " + formattedDate);
        showTimePickerDialog();
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        this.hour = hourOfDay;
        this.minute = minute;
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String formattedDate = df.format(calendar.getTime());
        txtDate.setText("Scheduled time to send SMS: " + formattedDate);
    }

    public void scheduleAlarm() {

        Intent intentAlarm = new Intent(this, AlarmReceiver.class);
        intentAlarm.putStringArrayListExtra("Number", numbers); //txtNumber.getText().toString().isEmpty() ? "0708808523" : txtNumber.getText().toString() );
        intentAlarm.putExtra("Message", txtMain.getText().toString());
        pendingIntentAlarm = PendingIntent.getBroadcast(this, 0, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        int hasWriteContactsPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        // Should we show an explanation?
        //if (ActivityCompat.shouldShowRequestPermissionRationale(activity,Manifest.permission.READ_EXTERNAL_STORAGE)) {
        // Explain to the user why we need to read the storage
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS},
                    REQUEST_CODE_ASK_PERMISSIONS);

        }else{
            alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntentAlarm);
        }


        if(timeChoosen)
            Toast.makeText(this, "SMS Scheduled for: " +year +"-"+ Utils.translateMonths(month) +"-"+
                    Utils.padZero(day) +" "+
                    Utils.padZero(hour) +":"+ Utils.padZero(minute),
                    Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this, "SMS sent directly", Toast.LENGTH_SHORT).show();

    }

    public void clearTime(){
        timeChoosen = false;
        txtDate.setText("The SMS will be send directly, click to schedule.");
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT)
                            .show();
                    alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntentAlarm);
                } else {
                    // Permission Denied
                    Toast.makeText(this, "EXTERNAL_STORAGE Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the main_menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actBarSend:
                scheduleAlarm();
                return true;

            case R.id.actBarLog:
                Intent logIntent = new Intent(this, ShowLog.class);
                startActivity(logIntent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == PICK_CONTACT_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.
                numbers = data.getStringArrayListExtra("Number");
                names = data.getStringArrayListExtra("Names");

                // Do something with the contact here (bigger example below)
            }
        }
    }

}
