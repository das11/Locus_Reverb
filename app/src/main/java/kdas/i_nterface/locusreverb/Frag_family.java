package kdas.i_nterface.locusreverb;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Frag_family.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Frag_family#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Frag_family extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    TextView month_name;
    ImageView down;
    Animation slide_down, fade_in, slide_up, slide_down_2, slide_up_2, rotate;

    DatabaseReference ROOT = FirebaseDatabase.getInstance().getReference();
    DatabaseReference user;

    String uid;
    List<String> count = new ArrayList<>();
    java.util.List<events> mevents = new ArrayList<>();

    RecyclerView recyclerView;
    rv_event_adapter_family adapter;

    private OnFragmentInteractionListener mListener;

    public Frag_family() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Frag_family.
     */
    // TODO: Rename and change types and number of parameters
    public static Frag_family newInstance(String param1, String param2) {
        Frag_family fragment = new Frag_family();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public int getposition_from_time(Date date){

        int pos = 0;
        for (long i = 1451586600000L; i < date.getTime(); i += 86400000){
            ++pos;
        }
        return pos;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View baseView = inflater.inflate(R.layout.fragment_frag_family, container, false);

        slide_down = AnimationUtils.loadAnimation(getContext(), R.anim.slide_down_calendar);
        slide_down_2 = AnimationUtils.loadAnimation(getContext(), R.anim.slide_down_calendar);
        fade_in = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        slide_up = AnimationUtils.loadAnimation(getContext(), R.anim.slide_up);
        slide_up_2 = AnimationUtils.loadAnimation(getContext(), R.anim.slide_up);
        rotate = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_180);

        SharedPreferences pref = getActivity().getSharedPreferences("PREFS", MODE_PRIVATE);
        uid = pref.getString("uid","");

        recyclerView = (RecyclerView)baseView.findViewById(R.id.rv_schedule_family);
        down = (ImageView)baseView.findViewById(R.id.down_family);
        final com.github.sundeepk.compactcalendarview.CompactCalendarView month_view = (com.github.sundeepk.compactcalendarview.CompactCalendarView)baseView.findViewById(R.id.compactcalendar_view);

        for (int i = 0; i < 3; ++i){
            //count[i] = true;
            count.add(i, "true");
        }

        Date da = new Date();
        int furl_x = getposition_from_time(da);
        Log.d("REAL DAU", furl_x + "");

        month_name = (TextView)baseView.findViewById(R.id.month_name_family);
        Date temp_month = new Date();
        month_name.setText(android.text.format.DateFormat.format("MMM", temp_month).toString());

        user = ROOT.child(uid);

        for (int i = 0; i < 365; ++i){
            mevents.add(new events(count, 365));
        }
        adapter = new rv_event_adapter_family(getContext(), mevents);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter.notifyDataSetChanged();

        Toast.makeText(getActivity(), 365 + " RV rows drawn, dayummm!", Toast.LENGTH_SHORT).show();

        if (down != null){
            down.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (month_view != null){
                        if (month_view.getVisibility() == View.INVISIBLE){
                            month_view.setVisibility(View.VISIBLE);
                            month_name.setVisibility(View.VISIBLE);
                            month_view.startAnimation(slide_down);
                            month_name.startAnimation(slide_down_2);
                            down.startAnimation(rotate);
                        }else if (month_view.getVisibility() == View.VISIBLE){
                            month_view.startAnimation(slide_up);
                            month_name.startAnimation(slide_up_2);
                            month_view.setVisibility(View.INVISIBLE);
                            month_name.setVisibility(View.INVISIBLE);
                            down.startAnimation(rotate);
                        }
                    }

                    //((LinearLayoutManager) events_rv.getLayoutManager()).scrollToPositionWithOffset(10, 0);

                }
            });
        }

        String d = "01-01-2016";
        SimpleDateFormat parser = new SimpleDateFormat("MM-dd-yyyy");
        Date test = new Date();
        try {
            test = parser.parse(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Event test_ev = new Event(R.color.some_teal2, test.getTime(), "This is a test event, which will later track the av. activity fetched from Firebase !");
        if (month_view != null)
            month_view.addEvent(test_ev);

        //#########
        if (month_view != null){
            month_view.setListener(new CompactCalendarView.CompactCalendarViewListener() {
                @Override
                public void onDayClick(Date dateClicked) {
//                    Calendar cal = Calendar.getInstance();
//                    int month = cal.get(Calendar.MONTH);
//                    int day = cal.get(Calendar.DAY_OF_MONTH);
//                    int hour = cal.get(Calendar.HOUR_OF_DAY);
//                    int min = cal.get(Calendar.MINUTE);
//                    int sec = cal.get(Calendar.SECOND);
//
//                    cal.set(month,day,hour,min,sec);
//                    Log.d("CAL ", cal + "");
                    //################################################### TODO
                    // current date object - dateClicked doesnt fetch time

                    Log.d("Date clicked", dateClicked + "");

                    Event cev = new Event(R.color.some_teal2, dateClicked.getTime(), "" );
                    month_view.addEvent(cev, true);
                    java.util.List<Event> ev = month_view.getEvents(dateClicked);
                    ((LinearLayoutManager) recyclerView.getLayoutManager()).scrollToPositionWithOffset(getposition_from_time(dateClicked), 0);


                    Toast.makeText(getActivity(), ev + "", Toast.LENGTH_LONG).show();
                    Log.d("cev :: ", dateClicked + " " + cev);
                    Log.d("prev_ev ::", ev + "");
                    Log.d("hash", dateClicked.hashCode() + "");
                    Log.d("POS_date", getposition_from_time(dateClicked) + "");
                    Log.d("Date_pos", getdate_from_pos(getposition_from_time(dateClicked)) + "");
                }

                @Override
                public void onMonthScroll(Date firstDayOfNewMonth) {
                    String month = (String) android.text.format.DateFormat.format("MMM", firstDayOfNewMonth);
                    month_name.setText(month);
                    Log.d("MONTH :::::", month);

                }
            });
        }


        //##################### initial offset
        ((LinearLayoutManager) recyclerView.getLayoutManager()).scrollToPositionWithOffset(adapter.return_present_pos(), 0);

        // Inflate the layout for this fragment
        return baseView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
