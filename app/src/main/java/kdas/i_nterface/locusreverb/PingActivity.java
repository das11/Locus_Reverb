package kdas.i_nterface.locusreverb;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import br.com.goncalves.pugnotification.notification.PugNotification;

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
    boolean acc_location_bool_fetch_once = false;
    boolean  peer_fetch_location_bool = false;
    boolean build_query_bool = false;
    boolean camera_move_initial_bool = false;
    boolean isPermission_boolean = false;
    boolean MET = false;

    DatabaseReference ROOT = FirebaseDatabase.getInstance().getReference();
    Location peer_location = new Location("");

    String peer_uid, peer_name;
    String Dir_Query;
    String poly_String;
    List<LatLng>polyline_list = new ArrayList<>();
    String directionResponse;

    Polyline polyline;

    com.wang.avi.AVLoadingIndicatorView loader;
    TextView loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ping);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        loader = (com.wang.avi.AVLoadingIndicatorView)findViewById(R.id.avli);
        loading = (TextView)findViewById(R.id.textView3);
        loader.smoothToShow();

        checkPermission();

        getPeerInitialData();

        buildGoogleApiClient();
        //gps();
        //askForPermission(Manifest.permission.ACCESS_FINE_LOCATION, 110);

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
                if (dataSnapshot.getChildrenCount() > 2) {
                    double lat = dataSnapshot.child("latitude").getValue(double.class);
                    double longi = dataSnapshot.child("longitude").getValue(double.class);

                    Log.d("Peer LATLNG ", lat + "\n" + longi);
                    peer_location.setLatitude(lat);
                    peer_location.setLongitude(longi);

                    if (!peer_fetch_location_bool) {
                        peer_fetch_location_bool = true;
                        build_query_bool = false;
                        Log.d("peer", "_fetch_location_bool " + peer_fetch_location_bool + "\n\n");
                    }
                }else
                    Toast.makeText(getApplicationContext(), "Into the night", Toast.LENGTH_LONG).show();
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
        if (isPermission_boolean)
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
        if (isPermission_boolean){
            locationRequest = new LocationRequest();
            locationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(5000)
                    .setFastestInterval(2000);

            gps(googleApiClient, locationRequest);
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (isPermission_boolean){
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


        /** waiting for accurate location and pushing to firebase */
        float accuracy = currentLocation.getAccuracy();
        if (accuracy < 20 && !acc_location_bool_fetch_once){
            acc_location = currentLocation;
            push_acc_location(acc_location);
            acc_location_bool_fetch_once = true;
            Log.d("ACC LOCATION", acc_location + "");
        }

        checkIfMet(currentLocation, peer_location);

        /** building direction query and requesting IFF self and peer locations are fetched */
        if (acc_location_bool_fetch_once && peer_fetch_location_bool && !build_query_bool){
            Log.d("DIR QUERY", "\n\n");
            Dir_Query = build_query(acc_location, peer_location);
            Log.d("DISTANCE", CalculationByDistance(acc_location, peer_location) + "\n\n");
            build_query_bool = true;
            new getDirection().execute("");
        }

        /** one time camera pan */
        if (!camera_move_initial_bool){
            movecamera(currentLocation);
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
    public void push_acc_location(Location accLocation){
        Log.d("CALL ::", "push_acc_location");
        SharedPreferences preferences = getSharedPreferences("PREFS", MODE_PRIVATE);
        String uid = preferences.getString("uid", "");

        DatabaseReference user_node = ROOT.child(uid);
        DatabaseReference user_locationF = user_node.child("location");
        user_locationF.setValue(accLocation);
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
                Log.d("PERM", "1");
                ActivityCompat.requestPermissions(PingActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 11);
                return false;
            } else {
                Log.d("PERM", "2");
                ActivityCompat.requestPermissions(PingActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 11);
                return false;
            }
        }else{
            isPermission_boolean = true;
            return true;
        }
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if(ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED){
//            switch (requestCode) {
//                /**
//                 * last location perm
//                 */
//                case 11:{
//                    permission_boolean = true;
//                    currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
//                    LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest,this);
//                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
//                    break;
//                }
//                /**
//                 * location updates perm
//                 */
//                case 12 : {
//                    permission_boolean = true;
//                    LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest,this);
//                    break;
//                }
//                /**
//                 * location layer enabled perm
//                 */
//                case 10 : {
//                    Log.d("LAYER", "");
//                    permission_boolean = true;
//                    mMap.setMyLocationEnabled(true);
//                    break;
//                }
//            }
//        }else{
//            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
//        }
//    }

    public void checkIfMet(Location currentLocation, Location peer_location){
        double curve = CalculationByDistance(currentLocation, peer_location);
        Log.d("CURVE", curve + "");

        if (curve < .08){
            MET = true;
        }

        if (MET && curve > .1){
            //DO STUFF
            run_pug();
        }
    }

    public double CalculationByDistance(Location StartP, Location EndP) {

        int Radius=6371;//radius of earth Km
        double lat1 = StartP.getLatitude();
        double lat2 = EndP.getLatitude();
        double lon1 = StartP.getLongitude();
        double lon2 = EndP.getLongitude();
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
        query.append("https://maps.googleapis.com/maps/api/directions/json?origin=");

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

    private class getDirection extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            HttpHandler httpHandler = new HttpHandler();
            directionResponse = httpHandler.GetHTTPData(Dir_Query);
            Log.d("RESP", directionResponse + "");
            return directionResponse;
        }

        @Override
        protected void onPostExecute(String directionResponse){
            if(directionResponse != null){
                try {
                    JSONObject root = new JSONObject(directionResponse);

                    JSONArray routes = root.getJSONArray("routes");
                    for(int i = 0; i < routes.length(); ++i)
                    {
                        JSONObject poly_j = routes.getJSONObject(i);
                        JSONObject point = poly_j.getJSONObject("overview_polyline");
                        poly_String = point.getString("points");

                        Log.d("poly",poly_String);

                        if (polyline != null){
                            polyline.remove();
                            polyline_list.clear();
                        }

                        polyline_list = decodePoly(poly_String);

                        LatLng start, end;
                        start = polyline_list.get(0);
                        end = polyline_list.get(polyline_list.size() - 1);

                        Bitmap marker = getBMP();

                        mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(marker)).position(start));
                        mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(marker)).position(end));

                        polyline = mMap.addPolyline(new PolylineOptions().addAll(polyline_list).color(ContextCompat.getColor(getApplicationContext(),R.color.some_accent)));

                        loader.smoothToHide();
                        loading.setVisibility(View.GONE);
                        Log.d("\n\n::::::: ADDED :::::::", "POLY");
                        for (int j = 0; j < polyline_list.size(); ++j){
                            Log.d("Poly List  ", polyline_list.get(j).toString());
                        }


                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
            else{
                //TOAST
            }
        }
    }

    public Bitmap getBMP(){

        BitmapDrawable bitmapDrawable = (BitmapDrawable)ContextCompat.getDrawable(getApplicationContext(), R.drawable.marker);
        Bitmap bitmap = bitmapDrawable.getBitmap();
        Bitmap marker = Bitmap.createScaledBitmap(bitmap, 70, 70, false);

        return marker;

    }

    private List<LatLng> decodePoly(String encoded) {

        java.util.List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }

    public void movecamera(Location tempLocation){
        double lat = tempLocation.getLatitude();
        double longi = tempLocation.getLongitude();

        LatLng latLng = new LatLng(lat, longi);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)
                .tilt(60)
                .zoom(16)
                .build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2000, null);
        camera_move_initial_bool = true;
    }

    public void run_pug(){
        PugNotification.with(getApplicationContext())
                .load()
                .title("Hey !")
                .message("You just met someone, Click!")
                .bigTextStyle("You just met someone, spare a moment buddy!")
                .smallIcon(R.drawable.notif_small)
                .largeIcon(R.drawable.notif_large)
                .flags(Notification.DEFAULT_ALL)
//                .click(null)
//                .dismiss(null)
                .simple()
                .build();
    }
}
