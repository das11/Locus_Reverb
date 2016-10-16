package kdas.i_nterface.locusreverb;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Interface on 30/08/16.
 */
public class test_timeline_adpater extends RecyclerView.Adapter<test_timeline_adpater.ViewHolder>{

    int high_pos;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView note;

        private Context context;


        public ViewHolder(Context context, View itemView) {
            super(itemView);
            this.context = context;
            note = (TextView)itemView.findViewById(R.id.textView4);

            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            int pos = getLayoutPosition();
            Toast.makeText(context, pos + "", Toast.LENGTH_SHORT).show();



        }
    }

    private java.util.List<timeline_day_data> mevents;
    private Context mcontext;

    public test_timeline_adpater(Context context, java.util.List<timeline_day_data> m_events){
        mevents = m_events;
        mcontext = context;
    }

    private Context getContext(){
        return mcontext;
    }


    @Override
    public test_timeline_adpater.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = getContext();

        LayoutInflater inflator = LayoutInflater.from(context);
        View row = inflator.inflate(R.layout.test_timline_row, parent, false);
        ViewHolder viewholder = new ViewHolder(getContext(), row);

        return viewholder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        timeline_day_data pos_data = mevents.get(position);

        holder.note.setText(pos_data.temp);

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
