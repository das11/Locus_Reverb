package kdas.i_nterface.locusreverb;

import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class ContactsActivity extends AppCompatActivity {

    boolean read_done = false;
    boolean contacts_perm = false;

    java.util.List<Contacts> contact = new ArrayList<>();
    ArrayList<String> contact_name = new ArrayList<>();
    ArrayList<String> contact_num = new ArrayList<>();

    Contact_adapter adapter;

    DatabaseReference ROOT = FirebaseDatabase.getInstance().getReference();
    DatabaseReference user_node, friends;

    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.contacts_recylerView);

        askForPermission(android.Manifest.permission.READ_CONTACTS, 11);
        if (contacts_perm)
            new read_async().execute("");
        else
            askForPermission(android.Manifest.permission.READ_CONTACTS, 11);




        adapter = new Contact_adapter(this, contact);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter.notifyDataSetChanged();

    }

    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(ContactsActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(ContactsActivity.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(ContactsActivity.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(ContactsActivity.this, new String[]{permission}, requestCode);
            }
        } else {
            contacts_perm = true;
            Toast.makeText(this, "" + permission + " is already granted. :: " + contacts_perm, Toast.LENGTH_SHORT).show();
            //new read_async().execute("");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED){
            switch (requestCode) {
                //Location
                case 11:
                    //askForGPS();
                    new read_async().execute("");
                    contacts_perm = true;
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                    break;
            }
        }else{
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    private class read_async extends AsyncTask<String, Void, String> {

        String uid;

        @Override
        protected void onPreExecute(){

        }

        @Override
        protected String doInBackground(String... strings) {

            while (!read_done){
                readContacts();
                SharedPreferences preferences = getSharedPreferences("PREFS", MODE_PRIVATE);
                uid = preferences.getString("uid","");

                user_node = ROOT.child(uid);
                friends = user_node.child("friends");
            }

            return null;
        }

        // @Override
        protected void onPostExecute(String temp){

            write_file(contact_name, contact_num);

            File file = new File(Environment.getExternalStorageDirectory(), "text_con.txt");
            if (file.length() != 0)
            {

            }

            Log.d("Hash :", contact_name.size() + "");
            for (int i = 0; i < contact_num.size(); ++i){
                if (!contact_name.get(i).equals("") || !contact_num.get(i).equals("")){
                    contact.add(new Contacts(contact_name.get(i), contact_num.get(i)));
                }
            }
            adapter.notifyDataSetChanged();


        }
    }

    public void readContacts(){
        String phnum = null;

        Uri content_uri = ContactsContract.Contacts.CONTENT_URI;
        String id = ContactsContract.Contacts._ID;
        String display_name = ContactsContract.Contacts.DISPLAY_NAME;
        String hasphnum = ContactsContract.Contacts.HAS_PHONE_NUMBER;
        Uri phone_content_uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String phone_contact_id = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String number = ContactsContract.CommonDataKinds.Phone.NUMBER;

        StringBuffer output, output2;
        String temp_n;
        String temp_num;
        ContentResolver contentResolver = getContentResolver();
        cursor = contentResolver.query(content_uri, null, null, null, null);

        if(cursor.getCount() > 0){
            int counter = 0;
            while(cursor.moveToNext()){
                output = new StringBuffer();
                output2 = new StringBuffer();
                String contact_id = cursor.getString(cursor.getColumnIndex(id));
                String name = cursor.getString(cursor.getColumnIndex(display_name));

                //Boolean hasnum = Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(hasphnum)));
                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(hasphnum)));
                if(hasPhoneNumber > 0){
                    output.append(name);

                    temp_n = name;
                    Log.d("CON ",output.toString());

                    Cursor phcursor = contentResolver.query(phone_content_uri, null, phone_contact_id + " = ?", new String[] {contact_id}, null);
                    while (phcursor.moveToNext()){
                        phnum = phcursor.getString(phcursor.getColumnIndex(number));
                        output2.append("\nNumber " + phnum);
                        temp_num = phnum;
                        //contact_num.add(temp_num.toString());
                    }
                    phcursor.close();
                }


                contact_name.add(output.toString());
                contact_num.add(output2.toString());

                //contact.add(new Contacts(output.toString(), output2.toString()));
                //Log.d("on", "on");
            }
            cursor.close();
            Log.d("Map :: ", contact_name.toString());
            Log.d("Map :: ", contact_num.toString());
            Log.d("size :: ", contact_name.size() + "");
            //inf();
            //writef(contact);

//            ContactsActivity.this.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Toast.makeText(getApplicationContext(), "Hello, from the thread ++ " + contact_name.size(), Toast.LENGTH_LONG).show();
//                }
//            });

            read_done = true;
            //thread_kill = true;


        }

    }

//    public void readContacts(){
//        String phnum = null;
//
//        Uri content_uri = ContactsContract.Contacts.CONTENT_URI;
//        String id = ContactsContract.Contacts._ID;
//        String display_name = ContactsContract.Contacts.DISPLAY_NAME;
//        String hasphnum = ContactsContract.Contacts.HAS_PHONE_NUMBER;
//        Uri phone_content_uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
//        String phone_contact_id = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
//        String number = ContactsContract.CommonDataKinds.Phone.NUMBER;
//
//        StringBuffer output, output2;
//        String temp_n;
//        String temp_num;
//        ContentResolver contentResolver = getContentResolver();
//        cursor = contentResolver.query(content_uri, null, null, null, null);
//
//        if(cursor.getCount() > 0){
//            int counter = 0;
//            while(cursor.moveToNext()){
//                output = new StringBuffer();
//                output2 = new StringBuffer();
//                String contact_id = cursor.getString(cursor.getColumnIndex(id));
//                String name = cursor.getString(cursor.getColumnIndex(display_name));
//
//                //Boolean hasnum = Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(hasphnum)));
//                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(hasphnum)));
//                if(hasPhoneNumber > 0){
//                    output.append(name);
//
//                    temp_n = name;
//                    Log.d("CON ",output.toString());
//
//                    Cursor phcursor = contentResolver.query(phone_content_uri, null, phone_contact_id + " = ?", new String[] {contact_id}, null);
//                    while (phcursor.moveToNext()){
//                        phnum = phcursor.getString(phcursor.getColumnIndex(number));
//                        output2.append(phnum + "");
//                        temp_num = phnum;
//                        //contact_num.add(temp_num.toString());
//                    }
//                    phcursor.close();
//                }
//
//
//                contact_name.add(output.toString());
//                contact_num.add(output2.toString());
//
//                //contact.add(new ContactsActivity(output.toString(), output2.toString()));
//                //Log.d("on", "on");
//            }
//            Log.d("Map :: ", contact_name.toString());
//            Log.d("Map :: ", contact_num.toString() + "\n");
//            Log.d("size :: ", contact_name.size() + "");
//            //inf();
//            //writef(contact);
//
//            ContactsActivity.this.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Toast.makeText(getApplicationContext(), "Hello, from the thread ++ " + contact_name.size(), Toast.LENGTH_LONG).show();
//
//                }
//            });
//            read_done = true;
//            //thread_kill = true;
//
//        }else {
//        }
//    }

    public void write_file(ArrayList<String> name, ArrayList<String> num){
        try{
            File file = new File(Environment.getExternalStorageDirectory(), "text_con.txt");
            FileOutputStream fout = new FileOutputStream(file);

            //FileOutputStream fout = openFileOutput("contacts_kd.txt", MODE_PRIVATE);
            OutputStreamWriter streamWriter = new OutputStreamWriter(fout);

            for (int i = 0; i < name.size(); ++i){
                if (!name.get(i).equals("") || !num.get(i).equals("")){
                    streamWriter.write("#" + i + "\n" + name.get(i) + " " + num.get(i) + "\n\n");
                }
            }

            streamWriter.close();
        }catch (IOException e){
            //
        }
    }

    public void inf(){
        for (int i = 0; i < 10; ++i){
            contact.add(new Contacts(contact_name.get(i), contact_num.get(i)));
        }
    }
}
