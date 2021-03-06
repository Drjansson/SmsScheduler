package drj.smsscheduler;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

//TODO: Show which contacts that has been selected. (And will receive a message if it's sent)
//TODO: have the loading of the contacts happen in a separate thread.
//TODO: Enable notifications, when sms sent or failed if I can detect that.
//TODO: Have a list of scheduled SMS, and be able to cancel a scheduled SMS. 

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    final private int REQUEST_CODE_ASK_PERMISSIONS = 51263;
    final public int PICK_CONTACT_REQUEST = 1;
    public static int GET_CONTACT_REQUEST = 2;
    public static int QUICK_MSG_CONTACT_REQUEST = 3;
    private boolean SHOW_CLEAR_BUTTON = false;

    private Context context;

    private EditText txtMain;
    private TextView txtDate;
    private TextView txtFabMenu1, txtFabMenu2;
    FloatingActionButton btnFloat, fabMenu1, fabMenu2;
    private Animation fabAnimOpen, fabAnimClose, fabanimClock, fabAnimAntiClock;
    private ArrayList<String> numbers = new ArrayList<>();
    private ArrayList<String> names = new ArrayList<>();
    private String omwName = "";

    private AlarmManager alarmManager;
    private Calendar calendar;
    private PendingIntent pendingIntentAlarm;

    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;

    private Boolean fabIsOpen = false;

    private boolean timeChoosen = false;
    private boolean contactsChoosen = false;

    //private boolean testBoolean = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        Toolbar mToolBar = findViewById(R.id.my_toolbar);
        setSupportActionBar(mToolBar);
        context = this;

        mToolBar.setBackgroundColor(ContextCompat.getColor(this,android.R.color.holo_blue_light));

        txtDate = findViewById(R.id.txtDate);
        Button btnNumber = findViewById(R.id.btnNumber);
        txtMain = findViewById(R.id.txtMain);

        InitializeFAB();

        txtMain.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(txtMain.getText().length() > 0 && !SHOW_CLEAR_BUTTON){
                    SHOW_CLEAR_BUTTON = true;
                    invalidateOptionsMenu();
                }else if(s.length() <= 0 && !contactsChoosen && !timeChoosen){
                    SHOW_CLEAR_BUTTON = false;
                    invalidateOptionsMenu();
                }
            }
        });

        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
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
                contactIntent.putExtra("requestCode", PICK_CONTACT_REQUEST);
                startActivityForResult(contactIntent, PICK_CONTACT_REQUEST);
            }
        });

    }

    @Override
    protected void onResume(){
        super.onResume();

        refreshFAB();
    }

    private void InitializeFAB() {

        btnFloat = findViewById(R.id.btnFloat);
        fabMenu1 = findViewById(R.id.fabMenu1);

        fabAnimOpen = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fabAnimClose = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        fabAnimAntiClock = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_rotate_anticlock);
        fabanimClock = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_rotate_clock);
        txtFabMenu1 = findViewById(R.id.txtOMW);


       btnFloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (fabIsOpen) {
                    txtFabMenu1.setVisibility(View.INVISIBLE);
                    fabMenu1.startAnimation(fabAnimClose);
                    btnFloat.startAnimation(fabAnimAntiClock);
                    fabMenu1.setClickable(false);
                    fabIsOpen = false;
                } else {
                    txtFabMenu1.setVisibility(View.VISIBLE);
                    fabMenu1.startAnimation(fabAnimOpen);
                    btnFloat.startAnimation(fabanimClock);
                    fabMenu1.setClickable(true);
                    fabIsOpen = true;
                }

            }
        });

        fabMenu1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent contactIntent = new Intent(MainActivity.this, Contacts.class);
                contactIntent.putExtra("requestCode", GET_CONTACT_REQUEST);
                if(omwName.isEmpty())
                    Toast.makeText(MainActivity.this, "No contact choosen, select one in the settings.", Toast.LENGTH_LONG).show();
                else {
                    contactIntent.putExtra("selection", omwName);
                    startActivityForResult(contactIntent, GET_CONTACT_REQUEST);
                }

            }
        });

        refreshFAB();

    }

    private void refreshFAB(){
        SharedPreferences settings = getSharedPreferences("prefs", MODE_PRIVATE);
        omwName = settings.getString("quick_msg_name", "");
        String number = settings.getString("quick_msg_number", "");
        int numType = settings.getInt("quick_msg_numType", -1);

        txtFabMenu1.setText("OMW " + omwName);

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
        SHOW_CLEAR_BUTTON = true;
        invalidateOptionsMenu();
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
        SHOW_CLEAR_BUTTON = true;
        invalidateOptionsMenu();
    }

    public void scheduleAlarm(String message) {

        if(numbers.isEmpty()) {
            Toast.makeText(this, "No contacts has been selected. SMS not sent.", Toast.LENGTH_SHORT).show();
            return;
        }

        //TODO: Create a function that can send sms directly? Without the AlarmReceiver.
        Intent intentAlarm = new Intent(this, AlarmReceiver.class);
        intentAlarm.putStringArrayListExtra("Number", numbers); //txtNumber.getText().toString().isEmpty() ? "" : txtNumber.getText().toString() );
        intentAlarm.putExtra("Message", message.equalsIgnoreCase("")
                    ? txtMain.getText().toString() : message);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CODE_ASK_PERMISSIONS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntentAlarm);
            } else {
                // Permission Denied
                Toast.makeText(this, "EXTERNAL_STORAGE Denied", Toast.LENGTH_SHORT).show();
            }
        } else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the main_menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        if(!SHOW_CLEAR_BUTTON) {
            MenuItem itemClear = menu.findItem(R.id.actBarClear);
            itemClear.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actBarSend:
                scheduleAlarm("");
                return true;

            case R.id.actBarLog:
                Intent logIntent = new Intent(this, ShowLog.class);
                startActivity(logIntent);
                return true;

            case R.id.actBarClear:

                new AlertDialog.Builder(context)
                        .setTitle("Clear")
                        .setMessage("Are you sure you want to clear?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                txtMain.setText("");
                                clearTime();
                                numbers.clear();
                                names.clear();
                                contactsChoosen = false;
                                SHOW_CLEAR_BUTTON = false;
                                invalidateOptionsMenu();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

                return true;

            case R.id.actBarSettings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);

                startActivity(settingsIntent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        // Check which request we're responding to
        if (requestCode == PICK_CONTACT_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.
                numbers = data.getStringArrayListExtra("Number");
                names = data.getStringArrayListExtra("Names");

                if(!(numbers.isEmpty() && names.isEmpty())){
                    SHOW_CLEAR_BUTTON = true;
                    invalidateOptionsMenu();
                }
                contactsChoosen = true;
            }
        } else if(requestCode == GET_CONTACT_REQUEST){
            if(resultCode == RESULT_OK){
                numbers = data.getStringArrayListExtra("Number");
                names = data.getStringArrayListExtra("Names");

                Random rand = new Random();
                scheduleAlarm(Utils.omwMessages[rand.nextInt(Utils.omwMessages.length)]);

                numbers.clear();
                names.clear();

            }
        }
    }

}
