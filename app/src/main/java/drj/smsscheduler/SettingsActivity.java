package drj.smsscheduler;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsActivity extends AppCompatActivity {

    static Preference pref = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment(this))
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        WeakReference<SettingsActivity> ref;
        public SettingsFragment(SettingsActivity act){
            ref = new WeakReference<>(act);
        }
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            SettingsActivity activity = ref.get();
            pref = findPreference("quickMsgPerson");
            SharedPreferences settings = activity.getSharedPreferences("prefs", Context.MODE_PRIVATE);
            pref.setSummary(settings.getString("quick_msg_name", ""));
            pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent contactIntent = new Intent();
                    contactIntent.setPackage("drj.smsscheduler"); //, "contacts");
                    contactIntent.setAction("contacts");
//                    contactIntent.setClass(SettingsActivity.this, Contacts.class);
                    contactIntent.putExtra("requestCode", MainActivity.QUICK_MSG_CONTACT_REQUEST);
                    startActivityForResult(contactIntent, MainActivity.QUICK_MSG_CONTACT_REQUEST);
                    return true;
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //if(requestCode == MainActivity.QUICK_MSG_CONTACT_REQUEST){
            if(resultCode == RESULT_OK){
                ArrayList<String> numbers = data.getStringArrayListExtra("Number");
                ArrayList<String> names = data.getStringArrayListExtra("Names");
                int numType = data.getIntExtra("phoneNumberType", -1);


                SharedPreferences settings = getSharedPreferences("prefs", Context.MODE_PRIVATE);
                //SharedPreferences settings = getPreferences((SettingsActivity.this);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("quick_msg_name", names.get(0));
                editor.putString("quick_msg_number", numbers.get(0));
                editor.putInt("quick_msg_numType", numType);
                editor.putString(pref.getKey(), names.get(0));
                editor.apply();
                pref.setSummary(names.get(0));

            }

        //}
    }
}