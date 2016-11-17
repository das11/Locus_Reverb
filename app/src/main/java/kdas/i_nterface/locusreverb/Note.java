package kdas.i_nterface.locusreverb;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class Note extends AppCompatActivity {

    EditText noteET;
    FloatingActionButton done;

    String note_str = "";
    int cat = 0;

    int childCount = 0;

    /**
     * cat -> 0 == friends
     *     -> 1 == professional
     *     -> 2 == family
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        noteET = (EditText)findViewById(R.id.note);

        done = (FloatingActionButton)findViewById(R.id.note_done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                note_str = noteET.getText().toString();
                pushToFirebase(note_str);
            }
        });
    }

    private void pushToFirebase(String str){

        SharedPreferences preferences = getSharedPreferences("PREFS", MODE_PRIVATE);
        String uid = preferences.getString("uid", "");

        DatabaseReference ROOT = FirebaseDatabase.getInstance().getReference();
        DatabaseReference user_node = ROOT.child(uid);
        DatabaseReference note;
        final DatabaseReference points_data_counter;

        Date today = new Date();
        final int day = getposition_from_time(today);

        switch (cat){
            case 0 : {
                note = user_node.child("data/" + day + "/points_data/friends/" + childCount + "notes");
                points_data_counter = user_node.child("data/" + day + "points_f");
                break;
            }
            case 1 : {
                note = user_node.child("data/" + day + "/points_data/professional/" + childCount + "notes");
                points_data_counter = user_node.child("data/" + day + "points_family");
                break;
            }
            case 2 : {
                note = user_node.child("data/" + day + "/points_data/family/" + childCount + "notes");
                points_data_counter = user_node.child("data/" + day + "points_p");
                break;
            }

            default: {
                note = user_node.child("data/" + day + "/points_data/friends/" + childCount + "notes");
                points_data_counter = user_node.child("data/" + day + "points_f");
            }
        }

        note.setValue(str);
        points_data_counter.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                childCount = dataSnapshot.getValue(int.class);
                points_data_counter.setValue(++childCount);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public int getposition_from_time(Date date){

        int pos = 0;
        for (long i = 1451586600000L; i < date.getTime(); i += 86400000){
            ++pos;
        }
        return pos;
    }
}
