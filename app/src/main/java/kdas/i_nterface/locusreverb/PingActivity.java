package kdas.i_nterface.locusreverb;

import android.Manifest;
import android.app.Activity;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PingActivity extends FragmentActivity implements OnMapReadyCallback,
                                                                GoogleApiClient.ConnectionCallbacks,
                                                                GoogleApiClient.OnConnectionFailedListener,
                                                                LocationListener,
                                                                ActivityCompat.OnRequestPermissionsResultCallback{

    private GoogleMap mMap;
    protected GoogleApiClient googleApiClient;
    protected LocationRequest locationRequest;
    protected Location currentLocation, acc_location;
    boolean permission_boolean = false;
    boolean acc_location_bool = false;

    DatabaseReference ROOT = FirebaseDatabase.getInstance().getReference();
    Location peer_location = new Location("");

    String peer_uid, peer_name;
    String Dir_Query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ping);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        getPeerInitialData();

        buildGoogleApiClient();
        //gps();
//        askForPermission(Manifest.permission.ACCESS_FINE_LOCATION, 110);

    }

    private void getPeerInitialData() {
        Bundle extras = getIntent().getExtras();
        if (extras != null){
            peer_uid = extras.getString("peer_uid");
            peer_name = extras.getString("peer_name");

            Log.d("PEERS", peer_name + "\n" + peer_uid);

            getPeerLocation();
        }
    }

    private void getPeerLocation(){
        DatabaseReference peer_uidF = ROOT.child(peer_uid);

        Log.d("peer call", "");
        DatabaseReference peer_locationF = peer_uidF.child("location");
        Log.d("PEER", peer_locationF + "");
        peer_locationF.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                double lat = dataSnapshot.child("latitude").getValue(double.class);
                double longi = dataSnapshot.child("longitude").getValue(double.class);

                Log.d("LATLNG", lat + "\n" + longi);
                peer_location.setLatitude(lat);
                peer_location.setLongitude(longi);
                if (acc_location_bool)
                    Dir_Query = build_query(acc_location, peer_location);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        googleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (checkPermission())
            mMap.setMyLocationEnabled(true);
    }

    protected synchronized void buildGoogleApiClient(){
        Log.d("Building API client","\n");
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        createLocationRequest();
    }

    protected void createLocationRequest(){
        if (checkPermission()){
            locationRequest = new LocationRequest();
            locationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(5000)
                    .setFastestInterval(2000);

            gps(googleApiClient, locationRequest);
        }
    }

    protected void startLocationUpdates(){
//        askForPermission(Manifest.permission.ACCESS_FINE_LOCATION, 11);
//        askForPermission(Manifest.permission.ACCESS_FINE_LOCATION, 12);
        if (checkPermission()){
            permission_boolean = true;
            currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest,this);
            Log.d("Started ", "\n\n\n");
        }
    }
    protected void stopLocationUpdates(){
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        googleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (checkPermission()){
            currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        handleLocation(location);
    }

    public void handleLocation(Location location){
        currentLocation = location;
        Log.d("Location ", location + "\n");

        float accuracy = currentLocation.getAccuracy();
        if (accuracy < 20){
            acc_location = currentLocation;
            acc_location_bool = true;
            Log.d("ACC LOCATION", acc_location + "");
        }

    }

    @Override
    public void onConnectionSuspended(int cause) {
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult((Activity) getApplicationContext(), 10009);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i("test", "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }


    public void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(PingActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(PingActivity.this, permission)) {
                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(PingActivity.this, new String[]{permission}, requestCode);
            } else {
                ActivityCompat.requestPermissions(PingActivity.this, new String[]{permission}, requestCode);
            }
        } else {
            switch (requestCode) {
                case 110 :{
                    Log.d("TOAST", "\n\n\n\n\n");
                }
                /**
                 * last location perm
                 */
                case 11:{
                    permission_boolean = true;
                    currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                    break;
                }
                /**
                 * location updates perm
                 */
                case 12 : {
                    permission_boolean = true;
                    LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest,this);
                    Log.d("LOg Update", "\n\n\n\n");
                    break;
                }
                /**
                 * location layer enabled perm
                 */
                case 10 : {
                    Log.d("LAYER", "");
                    permission_boolean = true;
                    mMap.setMyLocationEnabled(true);
                    break;
                }
            }
        }
    }

    public boolean checkPermission(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(PingActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(PingActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 11);
                return false;
            } else {
                ActivityCompat.requestPermissions(PingActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 11);
                return false;
            }
        }else
            return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED){
            switch (requestCode) {
                /**
                 * last location perm
                 */
                case 11:{
                    permission_boolean = true;
                    currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                    LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest,this);
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                    break;
                }
                /**
                 * location updates perm
                 */
                case 12 : {
                    permission_boolean = true;
                    LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest,this);
                    break;
                }
                /**
                 * location layer enabled perm
                 */
                case 10 : {
                    Log.d("LAYER", "");
                    permission_boolean = true;
                    mMap.setMyLocationEnabled(true);
                    break;
                }
            }
        }else{
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    public void gps(GoogleApiClient googleApiClient, LocationRequest locationRequest){
        Log.d("GPS", "\n\n");
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates locationSettingsStates = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(PingActivity.this,1000);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;

                    case  LocationSettingsStatusCodes.CANCELED:
                        Toast.makeText(getApplicationContext(), "We don't have a Warp Drive, so turn the GPS ON", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public String build_query(Location origin, Location dest){
        double origin_lat, origin_long, dest_lat, dest_long;
        StringBuilder query = new StringBuilder();
        query.append("http://maps.googleapis.com/maps/api/directions/json?origin=");

        origin_lat = origin.getLatitude();
        origin_long = origin.getLongitude();
        dest_lat = dest.getLatitude();
        dest_long = dest.getLongitude();

        query.append(origin_lat + ",");
        query.append(origin_long + "&destination=");
        query.append(dest_lat + ",");
        query.append(dest_long);
        query.append("&key=");
        query.append("AIzaSyBz6_XNrqpOKJWvXZxOYH3fmlPuO7h4MDE");

        Log.d("BUILD Q", query.toString());

        return query.toString();
    }
}
