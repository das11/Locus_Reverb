package kdas.i_nterface.locusreverb;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

/**
 * Created by Interface on 15/08/16.
 */
public class Contact_adapter extends RecyclerView.Adapter<Contact_adapter.ViewHolder> {

    DatabaseReference ROOT = FirebaseDatabase.getInstance().getReference();
    DatabaseReference peer, peer_s;

    String uid, friend_uid, friend_name;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView cname, cnum;
        public ImageView conimage;
        public FloatingActionButton fab1, fab2;
        public CoordinatorLayout rv_a;
        private Context context;

        public ViewHolder(Context context, View itemView) {
            super(itemView);


            this.rv_a = (CoordinatorLayout) itemView.findViewById(R.id.base_coord);

            this.cname = (TextView)itemView.findViewById(R.id.tv1);
            this.cnum = (TextView)itemView.findViewById(R.id.tv2);
            this.conimage = (ImageView)itemView.findViewById(R.id.con_image);
            this.fab1 = (FloatingActionButton)itemView.findViewById(R.id.fab);
            this.fab2 = (FloatingActionButton)itemView.findViewById(R.id.fab2);
            this.context = context;
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            int pos = getLayoutPosition();
            //Toast.makeText(context, "" + cardtext.getText() + pos + "", Toast.LENGTH_SHORT).show();

            if (pos == 0) {
                Intent i = new Intent(context, InitialScreen.class);
                context.startActivity(i);
            }

        }
    }



    private List<Contacts> contacts;
    private Context mcontext;

    public Contact_adapter(Context context, List<Contacts> mContacts){
        contacts = mContacts;
        mcontext = context;
    }

    private Context getContext(){
        return mcontext;
    }


    @Override
    public Contact_adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = getContext();

        LayoutInflater inflater = LayoutInflater.from(context);
        View row = inflater.inflate(R.layout.contact_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(getContext(), row);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final Contact_adapter.ViewHolder holder, int position) {

        final Contacts condata = contacts.get(position);

        TextView con_name = holder.cname;
        TextView con_num = holder.cnum;
        ImageView image_con = holder.conimage;
        FloatingActionButton f1,f2;
        f1 = holder.fab1; // fab1 -> ADD button
        f2 = holder.fab2;

        /**
         *
         * update CONDRAW
         * update Square text drawable
         *
         */


//        TextDrawable condraw = TextDrawable.builder().buildRoundRect(condata.contact_alph, R.color.grey, 15);

        con_name.setText(condata.contact_name);
        con_num.setText(condata.contact_num);
//        image_con.setImageDrawable(condraw);




        f1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Snackbar.make(view, condata.contact_name + "\n" + condata.contact_num, Snackbar.LENGTH_LONG).show();
                friend_name = condata.contact_name;
                peer = ROOT.child("/users/" + condata.contact_num);
                Log.d("peer", peer.toString());
                peer.addValueEventListener(new ValueEventListener() {
                    boolean found = false;
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d("log", dataSnapshot.getValue(String.class) + "");
                        if (dataSnapshot.exists()){
                            Log.d("exists", dataSnapshot.getValue(String.class));
                            found = true;
                            friend_uid = dataSnapshot.getValue(String.class);
                            addFriend();
                        }
                        if (!found)
                        Log.d("Error"," with number : not found");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });

        f2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public void addFriend(){
        SharedPreferences preferences = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        uid = preferences.getString("uid", "");

        peer_s = ROOT.child(uid + "/friends");
        peer_s.child(friend_uid).setValue(friend_name);

        peer_s.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("COUNT", dataSnapshot.getChildrenCount() + "");
                Toast.makeText(getContext(), dataSnapshot.getChildrenCount() + "", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
