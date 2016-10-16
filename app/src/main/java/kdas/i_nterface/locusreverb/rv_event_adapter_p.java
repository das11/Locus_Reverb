package kdas.i_nterface.locusreverb;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Interface on 18/08/16.
 */
public class rv_event_adapter_p extends RecyclerView.Adapter<rv_event_adapter_p.ViewHolder>{

    String furl, gist_note, user_root;
    DatabaseReference ROOT = FirebaseDatabase.getInstance().getReference();
    DatabaseReference note, user_day_check;

    int high_pos;
    int count = 0;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public com.github.pavlospt.CircleView pre1, pre2, pre3;
    public com.mikhaellopez.circularimageview.CircularImageView pro;
    public TextView day_month, day_week, runtv, lifetv, notetv, gist_note;
    public View line;


    private Context context;

    public ViewHolder(Context context, View itemView) {
        super(itemView);

        this.pro = (com.mikhaellopez.circularimageview.CircularImageView)itemView.findViewById(R.id.pro);
        this.pre1 = (com.github.pavlospt.CircleView)itemView.findViewById(R.id.cir2);
        this.pre2 = (com.github.pavlospt.CircleView)itemView.findViewById(R.id.cir3);
        this.pre3 = (com.github.pavlospt.CircleView)itemView.findViewById(R.id.cir4);
        this.day_month = (TextView)itemView.findViewById(R.id.day_month);
        this.day_week = (TextView)itemView .findViewById(R.id.day_week);
        this.line = (View)itemView.findViewById(R.id.line);
        this.runtv = (TextView)itemView.findViewById(R.id.run_tv);
        this.lifetv = (TextView)itemView.findViewById(R.id.life_tv);
        this.notetv = (TextView)itemView.findViewById(R.id.note_tv);
        this.gist_note = (TextView)itemView.findViewById(R.id.mini_data_card_text);
        this.context = context;

        itemView.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        int pos = getLayoutPosition();
        Toast.makeText(context, pos + "", Toast.LENGTH_SHORT).show();

            Intent i = new Intent(context, InitialScreen.class);
            i.putExtra("day", pos + 1);
            context.startActivity(i);

    }
}

    private java.util.List<events> mevents;
    private Context mcontext;

    public rv_event_adapter_p(Context context, java.util.List<events> m_events){
        mevents = m_events;
        mcontext = context;
    }

    private Context getContext(){
        return mcontext;
    }


    @Override
    public rv_event_adapter_p.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = getContext();

        LayoutInflater inflator = LayoutInflater.from(context);
        View row = inflator.inflate(R.layout.event_row, parent, false);
        ViewHolder viewholder = new ViewHolder(getContext(), row);

        return viewholder;
    }

    @Override
    public void onBindViewHolder(final rv_event_adapter_p.ViewHolder holder, int position) {

        events events_data = mevents.get(position);
        Log.d("day  :::::::: ", position + "");

        SharedPreferences pref = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        String uid = pref.getString("uid", "");

        final Date da = new Date();
        //int furl_x = getposition_from_time(da);


//        int furl_x = position + 1;
//
//        user_root = "https://wifiap-1361.firebaseio.com/"+ uid +"/data/" + furl_x;
//
//        furl = "https://wifiap-1361.firebaseio.com/"+ uid +"/data/" + furl_x + "/gist_note";
//        final  String furl_alt = "https://wifiap-1361.firebaseio.com/"+ uid +"/data/247/gist_note";
//        Log.d("furl_x", furl);
//
//        user_day_check = new Firebase(user_root);
//        Log.d(user_day_check + "", (user_day_check.child(furl_x + "") + " :::: null"));
//        user_day_check.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (!dataSnapshot.exists()){
//                    Log.d("null ::::::::: ", dataSnapshot.exists()  + " null");
//                    holder.gist_note.setText("Your Notes");
//                }
//                else{
//
//                    Log.d("TRUE ::::::::", "true");
////                    note = new Firebase(furl);
////
////                    note.addValueEventListener(new ValueEventListener() {
////                        @Override
////                        public void onDataChange(DataSnapshot dataSnapshot) {
////                            gist_note = dataSnapshot.getValue(String.class);
////                            holder.gist_note.setText(gist_note);
////                            Log.d("gist_ada" + count, gist_note);
////                            ++count;
////
////                        }
////
////                        @Override
////                        public void onCancelled(FirebaseError firebaseError) {
////
////                        }
////                    });
//
//                }
//            }
//
//            @Override
//            public void onCancelled(FirebaseError firebaseError) {
//
//            }
//        });

        //Calendar date = getdate_from_pos(position);

        Date date = getdate_from_pos(position);

        String day, month, day_week;

        day = (String) android.text.format.DateFormat.format("dd", date);
        month = (String) android.text.format.DateFormat.format("MMM", date);
        day_week = (String) android.text.format.DateFormat.format("EEEE", date);

        holder.day_month.setText(day);
        holder.day_week.setText(day_week);
        holder.gist_note.setText("NOTES"); // ## DEFAULT backport

        int note_pos = position+1;
//        furl = "https://wifiap-1361.firebaseio.com/" + uid + "/data/" + note_pos;
//        Log.d("furl_note", furl);

        note = ROOT.child(uid + "/data/" + note_pos + "/points_data/professional");
        Log.d(note + "", "");
        note.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                holder.gist_note.setText(dataSnapshot.child("gist").getValue(String.class));
                   //Log.d("NOTE", dataSnapshot.child("gist").getValue(String.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        high_pos = return_present_pos();
        for (int i = 0; i < 1; ++i){

            if (position == high_pos){

                holder.day_month.setTextColor(ContextCompat.getColor(mcontext, R.color.some_accent));
                holder.day_week.setTextColor(ContextCompat.getColor(mcontext, R.color.some_accent));
                holder.line.setBackgroundColor(ContextCompat.getColor(mcontext, R.color.some_accent));
                Log.d("COLOR", position + "");
            }

        }
    }

    @Override
    public int getItemCount() {
        return mevents.size();
    }

    public int return_present_pos(){
        Date current = new Date();
        Log.d("present pos date", current + "");
        int pos = getposition_from_time(current);
        Log.d("present pos", pos + "");

        return pos - 1; // POS FETCHING TRUE_VALUE + 1, SO NEEDED A QUICK FIX
    }

    public Date getdate_from_pos(int pos){

        Date start = new Date(1451586600000L);
        int count = 0;
        for (int i = 0; i < pos; ++i){
            ++count;
        }
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(start);
        cal.add(Calendar.DATE, count);
        //Date date = cal.getTime();

        return cal.getTime();
    }

    public int getposition_from_time(Date date){

        int pos = 0;
        for (long i = 1451586600000L; i < date.getTime(); i += 86400000){
            ++pos;
        }
        return pos;
    }
}
