package kdas.i_nterface.locusreverb;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.ogaclejapan.arclayout.ArcLayout;
import com.transitionseverywhere.ArcMotion;
import com.transitionseverywhere.ChangeBounds;
import com.transitionseverywhere.TransitionManager;

import java.util.ArrayList;
import java.util.List;

public class InitialScreen extends AppCompatActivity {

    ImageButton center, i1, i2, i3, i4;

    ImageButton fab;
    FrameLayout menuLayout;
    ArcLayout arcLayout;

    FirebaseAuth fAuth = FirebaseAuth.getInstance();

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

        fab = (ImageButton)findViewById(R.id.fab);
        menuLayout = (FrameLayout) findViewById(R.id.menu_layout);
        arcLayout = (ArcLayout) findViewById(R.id.arc_layout);

//        for (int i = 0, size = arcLayout.getChildCount(); i < size; i++) {
//            arcLayout.getChildAt(i).setOnClickListener(this);
//        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFabClick(view);
            }
        });


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
                startActivity(new Intent(InitialScreen.this, ContactsActivity.class));
                Log.d("CLICKED", "CLICK");
            }
        });

        i2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(InitialScreen.this, Memories.class));
                Toast.makeText(getApplicationContext(), "DOEN DONE ", Toast.LENGTH_LONG).show();
                Log.d("Con", "Con");
            }
        });

        i3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(InitialScreen.this, PeersActiviy.class));
            }
        });

        i4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(InitialScreen.this, PingActivity.class));
            }
        });

        new do_stuff().execute("");
    }

    private void onFabClick(View v) {
        if (v.isSelected()) {
            hideMenu();
        } else {
            showMenu();
        }
        v.setSelected(!v.isSelected());
    }

    private void showMenu() {
        menuLayout.setVisibility(View.VISIBLE);

        List<Animator> animList = new ArrayList<>();

        for (int i = 0, len = arcLayout.getChildCount(); i < len; i++) {
            animList.add(createShowItemAnimator(arcLayout.getChildAt(i)));
        }

        AnimatorSet animSet = new AnimatorSet();
        animSet.setDuration(400);
        animSet.setInterpolator(new OvershootInterpolator());
        animSet.playTogether(animList);
        animSet.start();
    }
    private void hideMenu() {

        List<Animator> animList = new ArrayList<>();

        for (int i = arcLayout.getChildCount() - 1; i >= 0; i--) {
            animList.add(createHideItemAnimator(arcLayout.getChildAt(i)));
        }

        AnimatorSet animSet = new AnimatorSet();
        animSet.setDuration(400);
        animSet.setInterpolator(new AnticipateInterpolator());
        animSet.playTogether(animList);
        animSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                menuLayout.setVisibility(View.INVISIBLE);
            }
        });
        animSet.start();

    }

    private Animator createShowItemAnimator(View item) {

        float dx = fab.getX() - item.getX();
        float dy = fab.getY() - item.getY();

        item.setRotation(0f);
        item.setTranslationX(dx);
        item.setTranslationY(dy);

        Animator anim = ObjectAnimator.ofPropertyValuesHolder(
                item,
                AnimatorUtils.rotation(0f, 720f),
                AnimatorUtils.translationX(dx, 0f),
                AnimatorUtils.translationY(dy, 0f)
        );

        return anim;
    }

    private Animator createHideItemAnimator(final View item) {
        float dx = fab.getX() - item.getX();
        float dy = fab.getY() - item.getY();

        Animator anim = ObjectAnimator.ofPropertyValuesHolder(
                item,
                AnimatorUtils.rotation(720f, 0f),
                AnimatorUtils.translationX(0f, dx),
                AnimatorUtils.translationY(0f, dy)
        );

        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                item.setTranslationX(0f);
                item.setTranslationY(0f);
            }
        });

        return anim;
    }

    private class do_stuff extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
//            start_service();
            //start_notif_service();
//            init();

//            Intent i = new Intent(InitialScreen.this, Init.class);
//            startActivity(i);
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
//        init = pref.getBoolean("Initialized", init);
        Log.d("home init", init + "");

        Log.d("current user", fAuth.getCurrentUser() + "");
        if (fAuth.getCurrentUser() != null)
            init = true;

        if (!init){
            Intent i = new Intent(InitialScreen.this, Init.class);
            startActivity(i);
        }

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
