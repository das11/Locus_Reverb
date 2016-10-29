package kdas.i_nterface.locusreverb;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;


/**
 * Created by Interface on 10/10/16.
 */

public class testService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;

    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    Location location;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;


    boolean chk = false;
    boolean pref_boolean = false;
    boolean buildAPI_bool = false;

    long poly_count = 0;

    String uid;

    DatabaseReference dbref = FirebaseDatabase.getInstance().getReference();
    DatabaseReference ref = dbref.child("test/test_");

    @Override
    public void onCreate() {
        // To avoid cpu-blocking, we create a background handler to run our service
        HandlerThread thread = new HandlerThread("testService",
                Process.THREAD_PRIORITY_BACKGROUND);
        // start the new handler thread
        thread.start();

        mServiceLooper = thread.getLooper();
        // start the service using the background handler
        mServiceHandler = new ServiceHandler(mServiceLooper);



    }

    protected synchronized void buildAPI(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();


        if (chk = checkperm()){
            mLocationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(60 * 1000)        // 10 seconds, in milliseconds
                    .setFastestInterval(30 * 1000); // 5 second, in milliseconds
        }else{
            ActivityCompat.requestPermissions((Activity) getApplicationContext(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 12);
            if (checkperm()){
                mLocationRequest = LocationRequest.create()
                        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                        .setInterval(60 * 1000)        // 10 seconds, in milliseconds
                        .setFastestInterval(30 * 1000); // 5 seconds, in milliseconds
            }
        }

        buildAPI_bool = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "onStartCommand", Toast.LENGTH_SHORT).show();
        getPrefs();

        // call a new service handler. The service ID can be used to identify the service
        Message message = mServiceHandler.obtainMessage();
        message.arg1 = startId;
        mServiceHandler.sendMessage(message);



        return START_STICKY;
    }

    protected void showToast(final String msg){
        //gets the main thread
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                // run this code in the main thread
                ref.setValue("01101011");
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("loc","loc");
        if (checkperm()){
            location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }else {

            //denied, cant reach here if denied anyway, so hakuna-matata
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(final Location location){
        Log.d("loc22","loc");

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                // run this code in the main thread
                pushToFirebase(location.getLatitude(), location.getLongitude());
                Log.d("location", location + "");
            }
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult((Activity) getApplicationContext(), CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i("test", "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    private final class ServiceHandler extends Handler {

        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            // Well calling mServiceHandler.sendMessage(message); from onStartCommand,
            // this method will be called.
            boolean run = true;
            int i = 0;

            // cpu-blocking activity here
            try {
                while (run) {
                    Thread.sleep(30 * 1000);
                    if (!buildAPI_bool){
                        buildAPI();
                    }
                    mGoogleApiClient.connect();

                    Log.d("Doing ", i + "");
                    showToast("#" + i +"  Finishing TutorialService, id: " + msg.arg1);
                    ++i;

                    // the msg.arg1 is the startId used in the onStartCommand,
                    // so we can track the running sevice here.
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }


            stopSelf(msg.arg1);
        }
    }

    public boolean checkperm(){
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return false;
        }else{
            return true;
        }
    }

    public void getPrefs(){
        SharedPreferences preferences = getSharedPreferences("PREFS", MODE_PRIVATE);
        uid = preferences.getString("uid", "");
    }

    public void pushToFirebase(double lat, double longi){
        Date today = new Date();
        int date_pos = getposition_from_time(today);
        Log.d("date", date_pos + "");

        final LatLng latLng = new LatLng(lat, longi);

        DatabaseReference ROOT = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference user_node_current = ROOT.child(uid + "/data/" + date_pos + "/polyline");
        Log.d("user_node", user_node_current + "");

//        user_node_current.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                poly_count = dataSnapshot.getChildrenCount();
//                ++poly_count;
//                DatabaseReference polylist = user_node_current.child(poly_count + "");
//                polylist.setValue(latLng);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

        user_node_current.push().setValue(latLng);
    }

    public int getposition_from_time(Date date){

        int pos = 0;
        for (long i = 1451586600000L; i < date.getTime(); i += 86400000){
            ++pos;
        }
        return pos;
    }

}
