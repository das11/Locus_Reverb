package kdas.i_nterface.locusreverb;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.transitionseverywhere.ArcMotion;
import com.transitionseverywhere.ChangeBounds;
import com.transitionseverywhere.TransitionManager;

public class InitialScreen extends AppCompatActivity {

    ImageButton center, i1, i2, i3, i4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_screen);

        final ViewGroup transitionsContainer = (ViewGroup)findViewById(R.id.activity_initial_screen);

        center = (ImageButton)findViewById(R.id.imageButton);
        i1 = (ImageButton)findViewById(R.id.imageButton2);
        i2 = (ImageButton)findViewById(R.id.imageButton3);
        i3 = (ImageButton)findViewById(R.id.i3);
        i4 = (ImageButton)findViewById(R.id.i4);

        center.setOnClickListener(new View.OnClickListener() {

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
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) center.getLayoutParams();
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
                            i2.setVisibility(View.INVISIBLE);
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


                center.setLayoutParams(params);
                i1.setLayoutParams(params1);
                i2.setLayoutParams(params2);
                i3.setLayoutParams(params3);
                i4.setLayoutParams(params4);
            }
        });
        i1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(InitialScreen.this, Memories.class));
                Log.d("CLICKED", "CLICK");
            }
        });

        new do_stuff().execute("");


    }

    private class do_stuff extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
//            start_service();
            start_notif_service();
            //init();

            return null;
        }
    }

    public void start_service(){
        Intent service = new Intent(this, testService.class);
        startService(service);
    }

    public void start_notif_service(){
        Intent notif_service = new Intent(this, notifService.class);
        startService(notif_service);
    }



    public void init(){
        boolean init = false;

        SharedPreferences pref = getSharedPreferences("PREFS", MODE_PRIVATE);
        init = pref.getBoolean("Initialized", init);
        Log.d("home init", init + "");

//        if (!init){
//            Intent i = new Intent(InitialScreen.this, Init.class);
//            startActivity(i);
//        }

        Intent i = new Intent(InitialScreen.this, Init.class);
            startActivity(i);

//        ScheduledExecutorService scheduleTaskExecutor = Executors.newScheduledThreadPool(5);
//        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
//            public void run() {
//                if (!thread){
//                    check_notif();
//                    Log.d("Keep", "running");
//                }
//            }
//        }, 0, 10, TimeUnit.SECONDS);


    }




}
