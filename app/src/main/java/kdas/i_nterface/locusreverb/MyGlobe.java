package kdas.i_nterface.locusreverb;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static kdas.i_nterface.locusreverb.R.id.map;


public class MyGlobe extends FragmentActivity implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    private UiSettings mUisettings;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LatLng latlng;
    private final static int CONNECTION_FALIURE_RESOLUTION_REQUEST = 9000;
    private LocationRequest mLocationRequest;
    private Marker mmarker, startdis, enddis;
    private static final int MY_LOCATION_PERMISSION_REQUESST_CODE = 1;

    private boolean mLocationPermissionDenied = false;
    boolean chk, once = false;
    boolean met = false;
    List<String> friends_list = new ArrayList<>(); // stores UIDs
    List<Double> lats = new ArrayList<>();
    List<Double> longs = new ArrayList<>();
    List<String> accuracy = new ArrayList<>();
    List<String> altitude = new ArrayList<>();
    List<String> speed = new ArrayList<>();

    List<MarkerOptions> testM = new ArrayList<>();
    List<Marker> startM = new ArrayList<>();
    int len;

    DatabaseReference ROOT = FirebaseDatabase.getInstance().getReference();
    DatabaseReference friend_node, friends;
    DatabaseReference friend_node_lat, friend_node_longs, friend_node_accuracy, friend_node_altitude, friend_node_speed;



    Location friend_location,frd_loca,friendLoca,loca_test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myglobe);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        if (chk = checkperm()){
            mLocationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                    .setFastestInterval(4 * 1000); // 1 second, in milliseconds
        }else{
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 12);
            if (checkperm()){
                mLocationRequest = LocationRequest.create()
                        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                        .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                        .setFastestInterval(4 * 1000); // 4 seconds, in milliseconds
            }
        }

    }
    public boolean checkperm(){
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return false;
        }else{
            return true;
        }
    }


    @Override
    protected void onResume() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if(checkperm()) {

            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }else {

            //denied, cant reach here if denied anyway, so hakuna-matata
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        handleLocation(location);

    }

    private void handleLocation(Location location) {

        double currentlat = location.getLatitude();
        double currentlong = location.getLongitude();
        double currentalti = location.getAltitude();
        double curraccuracy = location.getAccuracy();

        if(mmarker != null) {
            mmarker.remove();
        }

        LatLng latlong = new LatLng(currentlat,currentlong);
        MarkerOptions moptions = new MarkerOptions().position(latlong);
        mmarker = mMap.addMarker(moptions);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latlong)
                .tilt(60)
                .zoom(16)
                .build();

        if (!once){
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2000, null);
            once = true;
        }
        getFriendsGlobe(location);
    }

    private void getFriendsGlobe(Location location) {

        friends = ROOT.child("friends");
        friends.addValueEventListener(new ValueEventListener() {
            @Override

            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    parseMap((Map<String, String>) dataSnapshot.getValue());

                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        for (int j = 0; j < len; ++j) {
            LatLng abc = new LatLng(lats.get(j), longs.get(j));
            MarkerOptions temp = new MarkerOptions().position(abc)
                    .title(friends_list.get(j))
                    .snippet("test 123");
            if(startM.get(j) != null){
                startM.get(j).remove();
            }
            testM.add(temp);
            startM.add(mMap.addMarker(temp));
        }


    }


    public void parseMap(Map<String, String> map){
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String uid_value, uid_name;
            uid_value = entry.getKey();
            uid_name = entry.getValue();// can use for something

            friends_list.add(uid_value);

        }

        do_stuff();

    }
    public void do_stuff(){

        len = friends_list.size();
        for(int i = 0; i < len; ++i){
            friend_node = ROOT.child(friends_list.get(i)); // test this index
            friend_node_lat = friend_node.child("location/latitude");
            friend_node_longs = friend_node.child("location/longitude");
            friend_node_accuracy = friend_node.child("location/accuracy");
            friend_node_altitude = friend_node.child("location/altitude");
            friend_node_speed = friend_node.child("location/speed");
            friend_node_lat.addValueEventListener(new ValueEventListener(){
                @Override
                public void onDataChange(DataSnapshot dataSnapshot){
                    Double temp_lat;
                    temp_lat = (Double) dataSnapshot.getValue();
                    lats.add(temp_lat);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            friend_node_longs.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Double temp_long;
                    temp_long = (Double) dataSnapshot.getValue();
                    longs.add(temp_long);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }


    }







    private double calculateDistance(LatLng StartP, LatLng EndP) {

        int Radius=6371;
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2-lat1);
        double dLon = Math.toRadians(lon2-lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult= Radius*c;
        double km=valueResult/1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec =  Integer.valueOf(newFormat.format(km));
        double meter=valueResult%1000;
        int  meterInDec= Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value",""+valueResult+"   KM  "+kmInDec+" Meter   "+meterInDec);

        return Radius * c;

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        if(checkperm()) {
            mMap.setMyLocationEnabled(true);
        }
        mUisettings = mMap.getUiSettings();


    }
}
