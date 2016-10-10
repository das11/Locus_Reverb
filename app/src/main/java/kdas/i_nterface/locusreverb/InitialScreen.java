package kdas.i_nterface.locusreverb;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.transitionseverywhere.ArcMotion;
import com.transitionseverywhere.ChangeBounds;
import com.transitionseverywhere.TransitionManager;

public class InitialScreen extends AppCompatActivity {

    ImageButton init, i1, i2, i3, i4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_screen);

        final ViewGroup transitionsContainer = (ViewGroup)findViewById(R.id.activity_initial_screen);

        init = (ImageButton)findViewById(R.id.imageButton);
        i1 = (ImageButton)findViewById(R.id.imageButton2);
        i2 = (ImageButton)findViewById(R.id.imageButton3);
        i3 = (ImageButton)findViewById(R.id.i3);
        i4 = (ImageButton)findViewById(R.id.i4);

        init.setOnClickListener(new View.OnClickListener() {

            boolean mToRightAnimation;


            @Override
            public void onClick(View view) {

                TransitionManager.beginDelayedTransition(transitionsContainer,
                        new ChangeBounds().setPathMotion(new ArcMotion()).setDuration(500));



                i1.setVisibility(View.VISIBLE);
                i2.setVisibility(View.VISIBLE);
                i3.setVisibility(View.VISIBLE);
                i4.setVisibility(View.VISIBLE);

                mToRightAnimation = !mToRightAnimation;
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) init.getLayoutParams();
                params.gravity = mToRightAnimation ? (Gravity.CENTER | Gravity.BOTTOM) :
                        (Gravity.CENTER);

                FrameLayout.LayoutParams params1 = (FrameLayout.LayoutParams) i1.getLayoutParams();
                params1.topMargin = mToRightAnimation ? 150 : 0;
                params1.rightMargin = mToRightAnimation ? 80 : 0;
                params1.gravity = mToRightAnimation ? (Gravity.END | Gravity.TOP) :
                        (Gravity.CENTER);
                if (!mToRightAnimation){
                    i1.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            i1.setVisibility(View.INVISIBLE);
                        }
                    }, 500);
                }

                FrameLayout.LayoutParams params2 = (FrameLayout.LayoutParams) i2.getLayoutParams();
                params2.topMargin = mToRightAnimation ? 150 : 0;
                params2.leftMargin= mToRightAnimation ? 80 : 0;
                params2.gravity = mToRightAnimation ? (Gravity.START | Gravity.TOP) :
                        (Gravity.CENTER);
                if (!mToRightAnimation){
                    i2.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            i1.setVisibility(View.INVISIBLE);
                        }
                    }, 500);
                }

                FrameLayout.LayoutParams params3 = (FrameLayout.LayoutParams) i3.getLayoutParams();
                params3.bottomMargin = mToRightAnimation ? 150 : 0;
                params3.rightMargin= mToRightAnimation ? 80 : 0;
                params3.gravity = mToRightAnimation ? (Gravity.END | Gravity.BOTTOM) :
                        (Gravity.CENTER);
                if (!mToRightAnimation){
                    i3.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            i3.setVisibility(View.INVISIBLE);
                        }
                    }, 400);
                }

                FrameLayout.LayoutParams params4 = (FrameLayout.LayoutParams) i4.getLayoutParams();
                params4.bottomMargin = mToRightAnimation ? 150 : 0;
                params4.leftMargin= mToRightAnimation ? 80 : 0;
                params4.gravity = mToRightAnimation ? (Gravity.START | Gravity.BOTTOM) :
                        (Gravity.CENTER);
                if (!mToRightAnimation){
                    i4.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            i4.setVisibility(View.INVISIBLE);
                        }
                    }, 400);
                }



                init.setLayoutParams(params);
                i1.setLayoutParams(params1);
                i2.setLayoutParams(params2);
                i3.setLayoutParams(params3);
                i4.setLayoutParams(params4);
            }
        });

        Intent service = new Intent(this, testService.class);
        startService(service);

        DatabaseReference dbREF = FirebaseDatabase.getInstance().getReference();

        DatabaseReference ref = dbREF.child("test/test_");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String str = dataSnapshot.getValue(String.class);
                Log.d("STRING", "\n\n" + str);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }




}
