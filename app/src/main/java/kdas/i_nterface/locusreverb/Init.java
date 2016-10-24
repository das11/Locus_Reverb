package kdas.i_nterface.locusreverb;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import is.arontibo.library.ElasticDownloadView;

public class Init extends AppCompatActivity {

    String uid;

    DatabaseReference ROOT;

    EditText initEmail, initPass;
    FloatingActionButton done, backhome;
    CardView card;
    RelativeLayout background;
    FirebaseAuth fAuth;
    FirebaseUser user;

    FloatingActionButton reg;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);

        fAuth = FirebaseAuth.getInstance();

        background = (RelativeLayout)findViewById(R.id.activity_init);
        card = (CardView)findViewById(R.id.init_card);
        initEmail = (EditText)findViewById(R.id.email);
        initPass = (EditText)findViewById(R.id.password);
        done = (FloatingActionButton) findViewById(R.id.signup);
        backhome = (FloatingActionButton)findViewById(R.id.floatingActionButton);

        final ElasticDownloadView elasticDownloadView = (ElasticDownloadView)findViewById(R.id.elasticProgress);
        elasticDownloadView.setVisibility(View.INVISIBLE);
        backhome.setVisibility(View.INVISIBLE);

        done.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Init.this, done, done.getTransitionName());
                String email, pass;

                email = initEmail.getText().toString().trim();
                pass = initPass.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email", Toast.LENGTH_LONG).show();
                    elasticDownloadView.fail();
                }

                if (TextUtils.isEmpty(pass)) {
                    Toast.makeText(getApplicationContext(), "Enter password or i'll beat you up!", Toast.LENGTH_LONG).show();
                    elasticDownloadView.fail();
                }
                else {
                    background.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.elasticbackground));

                    backhome.setVisibility(View.VISIBLE);
                    card.setVisibility(View.INVISIBLE);
                    elasticDownloadView.setVisibility(View.VISIBLE);
                    elasticDownloadView.startIntro();
                    elasticDownloadView.setProgress(30);

                    fAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(Init.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                elasticDownloadView.fail();
                                Toast.makeText(Init.this, "Authentication failed." + task.getException(), Toast.LENGTH_SHORT).show();
                                Log.d("Firebase Auth ::", task.getException() + "");
                            } else {
                                Log.d("Done ", "GO HOME");

                                sharedPreferences = getSharedPreferences("PREFS", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean("Initialized", true);

                                user = fAuth.getCurrentUser();
                                uid = user.getUid();
                                editor.putString("uid", uid);
                                editor.apply();
                                editor.commit();

                                inflate_firebase();

                                Log.d("UID", uid + "");
                                elasticDownloadView.success();

                            }
                        }

                    });
                }
            }
        });

        backhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Init.this, InitialScreen.class);
                startActivity(i);
            }
        });
    }

    public void inflate_firebase(){
        ROOT = FirebaseDatabase.getInstance().getReference();

        SharedPreferences pref = getSharedPreferences("PREFS",MODE_PRIVATE);
        uid = pref.getString("uid", "");

        DatabaseReference user_node = ROOT.child(uid);
        DatabaseReference data = user_node.child("data");
        DatabaseReference location = user_node.child("location");
        DatabaseReference notif = user_node.child("notif");
        DatabaseReference pinged = user_node.child("pinged");
        DatabaseReference pinged_by = user_node.child("pinged_by");
        DatabaseReference contact_num = user_node.child("contact");
        DatabaseReference peers = user_node.child("friends");

        DatabaseReference user_append = ROOT.child("users/" + uid);
        user_append.setValue(uid);

        location.setValue("null");
        notif.setValue("null");
        pinged.setValue("null");
        pinged_by.setValue("null");
        contact_num.setValue(0);

        DatabaseReference days;
        for(int i = 1; i < 366; ++i){
            days = data.child(i + "");
            DatabaseReference points_f = days.child("points_f");
            DatabaseReference points_p = days.child("points_p");
            DatabaseReference points_family = days.child("points_family");

            points_f.setValue(1);
            points_p.setValue(1);
            points_family.setValue(1);

            DatabaseReference points_data = days.child("points_data");
            DatabaseReference friends = points_data.child("friends");
            DatabaseReference professional = points_data.child("professional");
            DatabaseReference family = points_data.child("family");

            DatabaseReference nested_node_f = friends.child(0 + "");
            DatabaseReference nested_node_p = professional.child(0 + "");
            DatabaseReference nested_node_family = family.child(0 + "");
            DatabaseReference gist_f = friends.child("gist");
            DatabaseReference gist_p = professional.child("gist");
            DatabaseReference gist_family = family.child("gist");

            gist_f.setValue("nothing till now");
            gist_p.setValue("nothing till now :p");
            gist_family.setValue("nothing till now :f");

            /** value of j accounts for the nodes of the sub groups
             0 -> friends
             1 -> professional
             2 -> family
             **/
            for (int j = 0; j < 3; ++j){
                DatabaseReference str;
                if (j == 0)
                    str = nested_node_f;
                else if (j == 1)
                    str = nested_node_p;
                else
                    str = nested_node_family;

                DatabaseReference point_uid = str.child("uid");
                DatabaseReference start = str.child("start");
                DatabaseReference end = str.child("end");
                DatabaseReference point_location = str.child("location");
                DatabaseReference notes = str.child("notes");

                point_uid.setValue("null");
                start.setValue("null");
                end.setValue("null");
                point_location.setValue("null");
                notes.setValue("null");
            }
        }

    }


}
