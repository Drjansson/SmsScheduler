package drj.smsscheduler;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by David on 2016-06-03.
 */

public class ShowLog extends AppCompatActivity{

    Context context;
    TextView txtLog;

    Log log = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_layout);
        context = this;

        // my_child_toolbar is defined in the layout file
        Toolbar myChildToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myChildToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        txtLog = (TextView) findViewById(R.id.txtLog);

        log = new Log(this);
        txtLog.setText(log.getLogInformation());


    }

    private void clearLog(){
        if(log != null){
            log.clear();
            txtLog.setText("");
            Toast.makeText(this, "Message log cleared.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the main_menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.log_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.actBarClear:
                new AlertDialog.Builder(context)
                        .setTitle("Clear")
                        .setMessage("Are you sure you want to clear the whole log?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                clearLog();
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

        }
        return true;
    }

}
