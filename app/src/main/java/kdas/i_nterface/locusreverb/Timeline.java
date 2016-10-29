package kdas.i_nterface.locusreverb;

import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Timeline extends AppCompatActivity implements OnMapReadyCallback {

    RecyclerView timeline;
    List<timeline_day_data> data = new ArrayList<>();

    java.util.List<events> mevents = new ArrayList<>();
    java.util.List<String> count = new ArrayList<>();
    List<List<String>> point_list = new ArrayList<java.util.List<String>>();
    List<String> point_data_list = new ArrayList<>();
    List<String> check_ins = new ArrayList<>();

    test_timeline_adpater adapater;

    int day, points = 0, c = 0, cat;
    String user;
    String furl_it;
    String check_in_count, start_time, end_time, notes;
    Location point_location;

    DatabaseReference ffurl, ffurl_it, ffurl_points, uid, uid_points;
    DatabaseReference ROOT = FirebaseDatabase.getInstance().getReference();

    String tempFire, polyFire;

    GoogleMap googleMap;

    boolean flip = false;
    Double lat, longi;
    LatLng latLngtemp;
    PolylineOptions p = new PolylineOptions();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        Toolbar toolbar_bottomsheet = (Toolbar)findViewById(R.id.view6);

        SupportMapFragment supportMapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.mapTimeline);
        supportMapFragment.getMapAsync(this);

        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.activity_timeline);
        // The View with the BottomSheetBehavior
        View bottomSheet = coordinatorLayout.findViewById(R.id.bottom_sheet);
        final BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                // React to state change
                Log.d("onStateChanged", "onStateChanged:" + newState);
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    //fab.setVisibility(View.GONE);
                } else {
                    //fab.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // React to dragging events
                //Log.d("onSlide", "onSlide");
            }
        });
        behavior.setPeekHeight(130);

        Bundle bundle = getIntent().getExtras();
        day = bundle.getInt("day");
        cat = bundle.getInt("cat");
        Log.d("Day ::", day + "");

        SharedPreferences pref = getSharedPreferences("PREFS", MODE_PRIVATE);
        user = pref.getString("uid", "");

        switch (cat){
            case 1 : {
                tempFire = user + "/data/" + day + "/points_data/friends";
                uid = ROOT.child(user + "/data/" + day + "/points_data/friends");
                uid_points = ROOT.child(user + "/data/" + day + "/points_f");
//                furl_it = furl + "/points_data/professional/";
                break;
            }
            case 2 : {
                tempFire = user + "/data/" + day + "/points_data/professional";
                uid = ROOT.child(user + "/data/" + day + "/points_data/professional");
                uid_points = ROOT.child(user + "/data/" + day + "/points_p");
//                furl_it = furl + "/points_data/friends/";
                break;
            }
            case 3 : {
                tempFire = user + "/data/" + day + "/points_data/family";
                uid = ROOT.child(user + "/data/" + day + "/points_data/family");
                uid_points = ROOT.child(user + "/data/" + day + "/points_family");
//                furl_it = furl + /"/points_data/all/";
                break;
            }
        }

        fetchPoly();

        longLog(uid_points + "");
        uid_points.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                points = dataSnapshot.getValue(int.class);
                point();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        timeline = (RecyclerView)findViewById(R.id.botttom_sheet_rv);

        adapater = new test_timeline_adpater(this, data);
        timeline.setAdapter(adapater);
        timeline.setLayoutManager(new LinearLayoutManager(this));
        adapater.notifyDataSetChanged();
    }

    public static void longLog(String str) {
        if (str.length() > 4000) {
            Log.d("LOG", str.substring(0, 4000));
            longLog(str.substring(4000));
        } else
            Log.d("LOG2", str);
    }

    public void point() {

        boolean done = false;
        for (int i = 0; i < points; ++i){
            DatabaseReference point = uid.child(i + "");
//            Toast.makeText(getApplicationContext(), point + "", Toast.LENGTH_LONG).show();

            longLog(point + "");
            point.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d("1","2");
                    check_in_count = dataSnapshot.child("check_in_count").getValue(String.class);
                    start_time = dataSnapshot.child("start_time").getValue(String.class);
                    end_time = dataSnapshot.child("end_time").getValue(String.class);
                    notes = dataSnapshot.child("notes").getValue(String.class);

                    String lat, longi;

                    if (dataSnapshot.child("location").getValue(String.class).equals("null")){
                        lat = "21";
                        longi = "91";
                    }else {
                        lat = dataSnapshot.child("location").child("latitude").getValue().toString();
                        longi = dataSnapshot.child("location").child("longitude").getValue().toString();
                    }


                    GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<java.util.List<String>>() {};
                    check_ins = dataSnapshot.child("check_ins").getValue(t);

                    point_location = new Location("");
                    point_location.setLatitude(Double.parseDouble(lat));
                    point_location.setLongitude(Double.parseDouble(longi));

                    point_data_list.add(check_in_count);
                    point_data_list.add(start_time);
                    point_data_list.add(end_time);
                    point_data_list.add(notes);

                    point_list.add(point_data_list);


                    /**
                     *
                     *
                     * point_list has data :: check-in count, starttime, endtime, notes
                     *
                     * location has well :: point locations
                     *
                     * checkins has data :: check in locations ////// NOT USED RN, Location object isnt managed for chkin in the backend yet
                     *
                     *
                     *
                     */
                    data.add(new timeline_day_data(point_data_list, check_ins, point_location));
                    adapater.notifyDataSetChanged();


                    Log.d("data single", point_data_list.toString());
                    Log.d("::::::: ", check_in_count + start_time + end_time + notes + point_location + check_ins + point_list);
                    Log.d("c", point_list.get(c).toString());
                    Log.d("size", point_list.size() + "");
                    ++c;
                    point_data_list.clear();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            done = true;

        }
    }

    public void fetchPoly(){

        final DatabaseReference poly = ROOT.child(user + "/data/" + day + "/polyline");
        Log.d("poly", poly + "");
        String temp = poly.getKey();
        Log.d("TEMP", temp + "");

        final List<LatLng> polyList = new ArrayList<>();


        poly.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    @SuppressWarnings("unchecked")
                    Map<String, Map<String, Double>> value = (Map<String, Map<String, Double>>)dataSnapshot.getValue();

                    for (Map.Entry<String, Map<String, Double>> l1 : value.entrySet()){
                        for (Map.Entry<String, Double> temp : l1.getValue().entrySet()){
                            Log.d("temp \n", temp + "");
                            if (!flip) {
                                lat = temp.getValue();
                                Log.d("LAT", String.valueOf(lat) + "");
                                flip = true;
                            }else {
                                longi = temp.getValue();
                            }


                        }

                        latLngtemp = new LatLng(lat, longi);
                        Log.d("LTN itera", "\n\n" + latLngtemp);

                        p.add(latLngtemp);
                        p.width(8);
                        p.visible(true);
                        googleMap.addPolyline(p);

                        movecamera(latLngtemp);

                        polyList.add(new LatLng(lat,longi));
                        flip = false;
                        Log.d("\n\n", "\n\n");
                    }
                    Log.d("MAP", value + "\n");
                    Log.d("LIST", polyList + "");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void drawPoly(List<LatLng> list){
        googleMap.addPolyline(new PolylineOptions().addAll(list).color(ContextCompat.getColor(getApplicationContext(),R.color.some_accent)));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

    }

    public void movecamera(LatLng latlong){
        Log.d("move","");
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latlong)
                .tilt(60)
                .zoom(16)
                .build();


//        PolylineOptions p = new PolylineOptions();
//        p.add(new LatLng(26.1857749,91.7538019));
//        p.add(new LatLng(26.1858131,91.7534262));
//        p.add(new LatLng(26.1857986,91.7537651));
//        p.width(8);
//        p.visible(true);
//
//        googleMap.addPolyline(p);

        googleMap.addMarker(new MarkerOptions().position(latlong));
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2000, null);
    }
}
