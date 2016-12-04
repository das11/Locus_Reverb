package kdas.i_nterface.locusreverb;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PeersActiviy extends AppCompatActivity {

    List<Peers> peersList = new ArrayList<>();
    PeersAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peers_activiy);

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.peers_recyclerView);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setHasFixedSize(true);

//        for (int i = 0; i < 20; ++i){
//            peersList.add(new Peers("User #" + i, "8612324213"));
//        }

        getPeers();

//        peersList.add(new Peers("User #", "8612324213"));
//        peersList.add(new Peers("User #User #", "8612324213"));
//        peersList.add(new Peers("User #User #User #User #User #User #User #User #", "8612324213"));
//        peersList.add(new Peers("User #User #User #", "8612324213"));
//        peersList.add(new Peers("User #User #User #User #User #", "8612324213"));
//        peersList.add(new Peers("User #User #User #User #User #User #", "8612324213"));


        adapter = new PeersAdapter(this, peersList);
        recyclerView.setAdapter(adapter);

    }

    public void getPeers(){
        DatabaseReference ROOT = FirebaseDatabase.getInstance().getReference();

        String uid;
        SharedPreferences preferences = getSharedPreferences("PREFS", MODE_PRIVATE);
        uid = preferences.getString("uid", "");
        Log.d("Peer_uid", " from PeersActivty " + uid);
        DatabaseReference user = ROOT.child(uid);

        DatabaseReference peers = user.child("friends");
        Log.d("peers  ", peers + "");
        peers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                parseMap((Map<String, String>) dataSnapshot.getValue());
                Log.d("map", dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void parseMap(Map<String, String> map){

        ArrayList<String> peer_uid = new ArrayList<>();
        for (Map.Entry<String, String> entry : map.entrySet()){
            String uid_value, uid_name;
            uid_value = entry.getKey();
            uid_name = entry.getValue();

            peersList.add(new Peers(uid_name, uid_value,"0000000000"));
            adapter.notifyDataSetChanged();

            Log.d("log", uid_value + " " + uid_name);
        }
    }
}
