package kdas.i_nterface.locusreverb;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import is.arontibo.library.ElasticDownloadView;

public class Init extends AppCompatActivity {

    EditText initEmail, initPass;
    FloatingActionButton done, backhome;
    CardView card;
    RelativeLayout background;
    FirebaseAuth fAuth;
    FirebaseUser user;

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
        done = (FloatingActionButton) findViewById(R.id.fab_init);
        backhome = (FloatingActionButton)findViewById(R.id.floatingActionButton);

        final ElasticDownloadView elasticDownloadView = (ElasticDownloadView)findViewById(R.id.elasticProgress);
        elasticDownloadView.setVisibility(View.INVISIBLE);
        backhome.setVisibility(View.INVISIBLE);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                background.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.elasticbackground));

                backhome.setVisibility(View.VISIBLE);
                card.setVisibility(View.INVISIBLE);
                elasticDownloadView.setVisibility(View.VISIBLE);
                elasticDownloadView.startIntro();
                elasticDownloadView.setProgress(30);
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

                    fAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(Init.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d("Done ", "GO HOME");

                            if (!task.isSuccessful()) {
                                elasticDownloadView.fail();
                                Toast.makeText(Init.this, "Authentication failed." + task.getException(), Toast.LENGTH_SHORT).show();
                            } else {

                                sharedPreferences = getSharedPreferences("PREFS", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean("Initialized", true);

                                user = fAuth.getCurrentUser();
                                String uid;
                                uid = user.getUid();
                                editor.putString("uid", uid);
                                editor.apply();
                                editor.commit();

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
}
