package kdas.i_nterface.locusreverb;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import is.arontibo.library.ElasticDownloadView;

public class Init extends AppCompatActivity {

    EditText initEmail, initPass;
    Button done;
    FirebaseAuth fAuth;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);

        fAuth = FirebaseAuth.getInstance();

        initEmail = (EditText)findViewById(R.id.email);
        initPass = (EditText)findViewById(R.id.password);
        done = (Button)findViewById(R.id.fab_init);

        final ElasticDownloadView elasticDownloadView = (ElasticDownloadView)findViewById(R.id.elasticProgress);
        elasticDownloadView.startIntro();

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                elasticDownloadView.setProgress(30);
                String email, pass;

                email = initEmail.getText().toString().trim();
                pass = initPass.getText().toString().trim();

                if (TextUtils.isEmpty(email))
                    Toast.makeText(getApplicationContext(), "Enter email", Toast.LENGTH_LONG).show();

                if (TextUtils.isEmpty(pass))
                    Toast.makeText(getApplicationContext(), "Enter password or i'll beat you up!", Toast.LENGTH_LONG).show();

                sharedPreferences = getSharedPreferences("PREFS", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("Initialized", true);
                editor.commit();

                fAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(Init.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("Done ", "GO HOME");
                        elasticDownloadView.success();

                        if (!task.isSuccessful()) {
                            elasticDownloadView.fail();
                            Toast.makeText(Init.this, "Authentication failed." + task.getResult(), Toast.LENGTH_SHORT).show();
                        }

                        else{
                            Intent i = new Intent(Init.this, InitialScreen.class);
                            startActivity(i);
                        }
                    }

                });
            }
        });
    }
}
