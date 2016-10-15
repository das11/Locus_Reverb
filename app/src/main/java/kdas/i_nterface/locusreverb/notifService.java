package kdas.i_nterface.locusreverb;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import br.com.goncalves.pugnotification.notification.PugNotification;

/**
 * Created by Interface on 15/10/16.
 */

public class notifService extends Service{

    private  Looper mServiceLooper;
    private ServiceHandler mServiceHandler;

    DatabaseReference ROOT = FirebaseDatabase.getInstance().getReference();

    @Override
    public void onCreate(){
        HandlerThread handler = new HandlerThread("notifService", Process.THREAD_PRIORITY_BACKGROUND);
        handler.start();

        mServiceLooper = handler.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){

        Message msg =  mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);

        return START_STICKY;
    }

    private class ServiceHandler extends Handler{
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg){
            boolean run = true;

            try {
                while (run){

                    Thread.sleep(2000);
                    Log.d("run", "run");
                    checkNotif();


                }
            }catch (InterruptedException e){
                Thread.currentThread().interrupt();
            }

            stopSelf(msg.arg1);

        }
    }

    public void checkNotif(){
        SharedPreferences pref = getSharedPreferences("PREFS", MODE_PRIVATE);
        String uid;
        uid = pref.getString("uid", "");

        final DatabaseReference notif = ROOT.child(uid + "/notif");
        notif.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean val = dataSnapshot.getValue(Boolean.class);
                Log.d("Notif", val + "");

                if (val){
                    run_pug();
                    notif.setValue(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
