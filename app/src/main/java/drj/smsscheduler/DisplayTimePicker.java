package drj.smsscheduler;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by David on 2016-05-29.
 */

public class DisplayTimePicker extends DialogFragment {



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), (MainActivity)getActivity(), hour, minute, DateFormat.is24HourFormat(getActivity()));
    }



    /*Button btnSetDate;
    DatePicker datePicker;
    TimePicker timePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.time_chooser);

        this.btnSetDate = (Button) findViewById(R.id.btnSetDate);
        this.datePicker = (DatePicker) findViewById(R.id.datePicker);
        this.timePicker = (TimePicker) findViewById(R.id.timePicker);

        btnSetDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Date: " +datePicker.getYear() + " "+
                datePicker.getMonth() +" "+
                datePicker.getDayOfMonth());
                Intent intent = new Intent();
                intent.putExtra("Year", datePicker.getYear());
                intent.putExtra("Month", datePicker.getMonth());
                intent.putExtra("Day", datePicker.getDayOfMonth());

                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }*/
}
