package kdas.i_nterface.locusreverb;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.List;

/**
 * Created by Interface on 22/10/16.
 */

public class PeersAdapter extends RecyclerView.Adapter<PeersAdapter.ViewHolder> {


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView peer_image;
        TextView peer_name;
        CardView peer_row;
        Context context;

        public ViewHolder(Context context, View itemView){
            super(itemView);

            this.peer_image = (ImageView)itemView.findViewById(R.id.imageView8);
            this.peer_name = (TextView)itemView.findViewById(R.id.name_tv);
            this.peer_row = (CardView)itemView.findViewById(R.id.peers_row);
            this.context = context;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Log.d("onClick", "onClick");
        }
    }

    List<Peers> peers;
    Context context;
    public PeersAdapter(Context context, List<Peers> peers){
        this.peers = peers;
        this.context = context;
    }

    private Context getContext(){
        return context;
    }

    @Override
    public PeersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = getContext();

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View row = layoutInflater.inflate(R.layout.peers_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(getContext(), row);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PeersAdapter.ViewHolder holder, int position) {

        final Peers peersData = peers.get(position);

        ColorGenerator colorGenerator = ColorGenerator.MATERIAL;
        int alph_color = colorGenerator.getRandomColor();
        TextDrawable alphabet = TextDrawable.builder().buildRound(peersData.alphabet, alph_color);

        holder.peer_image.setImageDrawable(alphabet);
        holder.peer_name.setText(peersData.name);

        holder.peer_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Hello", peersData.name + "\n");
                Intent i = new Intent(getContext(), PingActivity.class);
                i.putExtra("peer_uid", peersData.uid);
                i.putExtra("peer_name", peersData.name);
                getContext().startActivity(i);
            }
        });


    }

    @Override
    public int getItemCount() {
        return peers.size();
    }
}
