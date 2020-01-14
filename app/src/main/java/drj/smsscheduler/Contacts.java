package drj.smsscheduler;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by David on 2016-06-04.
 */

public class Contacts extends AppCompatActivity {
    final private int REQUEST_CODE_ASK_PERMISSIONS = 11163;

    Context context;

    ArrayList<ListItem> listItems = new ArrayList<>();
    ArrayList<ListItem> allContacts = new ArrayList<>();
    ArrayList<ListItem> selectedItems = new ArrayList<>();
    ArrayList<ListItem> alreadySelectedItems = new ArrayList<>();
    myArrayAdapter adapter;
    ListView listView;
    ProgressDialog progress;

    private ArrayList<String> receivedNumbers = new ArrayList<>();
    private ArrayList<String> receivedNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contacts_layout);
        Toolbar mToolBar = findViewById(R.id.my_toolbar);
        setSupportActionBar(mToolBar);

        if (getIntent().getExtras().getInt("requestCode", 0) == MainActivity.GET_CONTACT_REQUEST) {
            String searchString = getIntent().getStringExtra("selection");
            String selection = ContactsContract.Contacts.DISPLAY_NAME + " LIKE \"%" + searchString + "%\"";

            Pair<String, String> contact = getContact(selection, ContactsContract.CommonDataKinds.Phone.TYPE_MAIN);

            //TODO error checking..
            Intent intent = new Intent();
            ArrayList<String> numbers = new ArrayList<>();
            numbers.add(contact.second);
            ArrayList<String> names = new ArrayList<>();
            names.add(contact.first);

            intent.putStringArrayListExtra("Number", numbers);
            intent.putStringArrayListExtra("Names", names);

            setResult(RESULT_OK, intent);
            finish();
        }

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        context = this;
        Intent informationIntent = getIntent();
        if (informationIntent.getStringArrayListExtra("Names") != null && informationIntent.getStringArrayListExtra("Numbers") != null) {
            receivedNames = informationIntent.getStringArrayListExtra("Names");
            receivedNumbers = informationIntent.getStringArrayListExtra("Numbers");
        }
        //fill in the alreadySelectedItems array with the data from the intent.
        if (receivedNumbers.size() > 0 && receivedNumbers.size() == receivedNames.size()) {
            for (int i = 0; i < receivedNumbers.size(); i++) {
                //android.util.Log.e("D", "Adding: "+ receivedNames.get(i)+ receivedNumbers.get(i));
                alreadySelectedItems.add(new ListItem(receivedNames.get(i), receivedNumbers.get(i)));
            }
        }


        listView = findViewById(R.id.contactList);
        listView.setBackgroundColor(ContextCompat.getColor(context, R.color.listItemBackground));

        adapter = new myArrayAdapter(this, listItems);
        listView.setAdapter(adapter);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        listView.setItemsCanFocus(false);

        // progress = new ProgressDialog(this);

        int hasWriteContactsPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
        // Should we show an explanation?
        //if (ActivityCompat.shouldShowRequestPermissionRationale(activity,Manifest.permission.READ_EXTERNAL_STORAGE)) {
        // Explain to the user why we need to read the storage
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS},
                    REQUEST_CODE_ASK_PERMISSIONS);

        } else {

            new runAsyncTask().execute(alreadySelectedItems);
            //populateContacts();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListItem item = (ListItem) listView.getItemAtPosition(position);
                if (selectedItems.contains(item)) {
                    item.setSelected(false);
                    selectedItems.remove(item);
                } else {
                    item.setSelected(true);
                    selectedItems.add(item);
                }

                adapter.notifyDataSetChanged();
            }
        });

    }

    private void updateList(ArrayList<ListItem> listToDisplay) {
        listItems.addAll(listToDisplay);
        allContacts.addAll(listToDisplay);
        Collections.sort(listItems);
        Collections.sort(allContacts);
        adapter.notifyDataSetChanged();
    }

    private void updateList(ListItem listItem) {
        listItems.add(listItem);
        allContacts.add(listItem);
        Collections.sort(listItems);
        Collections.sort(allContacts);
        adapter.notifyDataSetChanged();
    }

    //Populates the contact list.
    // TODO: Make this return a small number each time so I can remove the progressDialog.

    private class runAsyncTask extends AsyncTask<ArrayList<ListItem>, ListItem, ArrayList<ListItem>> {

        protected ArrayList<ListItem> doInBackground(ArrayList<ListItem>... params) {
            ArrayList<ListItem> contactsList = new ArrayList<>();
            ContentResolver cr = getContentResolver();
            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                    null, null, null, null);


            if (cur.getCount() > 0) {
                while (cur.moveToNext()) {
                    String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    if (Integer.parseInt(cur.getString(
                            cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                        Cursor pCur = cr.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[]{id}, null);
                        while (pCur.moveToNext()) {
                            String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            //android.util.Log.i("Contacts", "Name: " + name + ", Phone No: " + phoneNo);
                            ListItem item = new ListItem(name, phoneNo);
                            for (int i = 0; i < params[0].size(); i++) {
                                if (item.equals(params[0].get(i))) {
                                    item.setSelected(true);
                                    selectedItems.add(item);
                                }
                            }
                            publishProgress(item);
                            contactsList.add(item);
                        }
                        pCur.close();
                    }

                }
            }
            cur.close();
            return contactsList;
        }

        @Override
        protected void onPreExecute() {
            /*progress.setTitle("Loading");
            progress.setMessage("Wait while fetching contacts...");
            progress.setCancelable(false);
            progress.show();*/
        }

        protected void onProgressUpdate(ArrayList<ListItem>... progress) {
            updateList(progress[0]);
        }

        protected void onProgressUpdate(ListItem... progress) {
            updateList(progress[0]);
        }

        protected void onPostExecute(ArrayList<ListItem> result) {
            //updateList(result);
            // progress.dismiss();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT)
                            .show();
                    new runAsyncTask().execute(alreadySelectedItems);
                    //populateContacts();
                } else {
                    // Permission Denied
                    Toast.makeText(this, "READ CONTACTS permission Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contacts_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            public boolean onQueryTextChange(String newText) {

                listItems.clear();
                for (int i = 0; i < allContacts.size(); i++) {
                    if (allContacts.get(i).getName().toLowerCase().contains(newText.toLowerCase())) {
                        listItems.add(allContacts.get(i));
                    }
                }
                adapter.notifyDataSetChanged();

                return true;
            }

            public boolean onQueryTextSubmit(String query) {

                return true;
            }
        };
        searchView.setOnQueryTextListener(queryTextListener);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actBarDone:
            case android.R.id.home:
                Intent intent = new Intent();
                ArrayList<String> numbers = new ArrayList<>();
                ArrayList<String> names = new ArrayList<>();
                for (int i = 0; i < selectedItems.size(); i++) {
                    numbers.add(selectedItems.get(i).getNumber());
                    names.add(selectedItems.get(i).getName());
                }

                intent.putStringArrayListExtra("Number", numbers);
                intent.putStringArrayListExtra("Names", names);

                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.actBarClear:
                for (int i = 0; i < selectedItems.size(); i++) {
                    selectedItems.get(i).setSelected(false);
                }
                selectedItems.clear();

                adapter.notifyDataSetChanged();

        }
        return true;
    }


    private Pair<String, String> getContact(String selection, int selectedType) {
        String nameResult = "";
        String noResult = "";
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, selection, null, null);

        try {
            if (cur.getCount() > 0) {
                while (cur.moveToNext()) {
                    String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    if (Integer.parseInt(cur.getString(
                            cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {

                        Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[]{id}, null);
                        if (pCur != null) {
                            nameResult = name;
                            ArrayList<String> tmpNums = new ArrayList<>();
                            while (pCur.moveToNext()) {
                                String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                                if (pCur.getInt(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE)) == selectedType) {
                                    pCur.close();
                                    cur.close();
                                    return new Pair<>(nameResult, phoneNo);
                                } else
                                    tmpNums.add(phoneNo);
                            }
                            pCur.close();
                            noResult = tmpNums.get(0);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        cur.close();
        return new Pair<>(nameResult, noResult);
    }

}


    //This function was used before the asyncTask was added.

/*    private void populateContacts(){
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        ArrayList<ListItem> alreadySelectedItems = new ArrayList<ListItem>();
        if(receivedNumbers.size() > 0 && receivedNumbers.size() == receivedNames.size()){
            for(int i = 0; i<receivedNumbers.size(); i++){
                android.util.Log.e("D", "Adding: "+ receivedNames.get(i)+ receivedNumbers.get(i));
                alreadySelectedItems.add(new ListItem(receivedNames.get(i), receivedNumbers.get(i)));
            }
        }
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (Integer.parseInt(cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        //android.util.Log.i("Contacts", "Name: " + name + ", Phone No: " + phoneNo);
                        ListItem item = new ListItem(name, phoneNo);
                        for(int i = 0; i< alreadySelectedItems.size(); i++){
                            if(item.equals(alreadySelectedItems.get(i))){
                                item.setSelected(true);
                                selectedItems.add(item);
                            }
                        }
                        allContacts.add(item);
                        listItems.add(item);
                    }
                    pCur.close();
                }
            }
        }
        Collections.sort(listItems);
        adapter.notifyDataSetChanged();
        Collections.sort(allContacts);
    } */


