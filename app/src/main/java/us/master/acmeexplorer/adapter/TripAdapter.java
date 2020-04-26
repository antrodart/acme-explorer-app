package us.master.acmeexplorer.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import us.master.acmeexplorer.Constants;
import us.master.acmeexplorer.R;
import us.master.acmeexplorer.TripDetailActivity;
import us.master.acmeexplorer.Util;
import us.master.acmeexplorer.entity.Trip;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {

    private List<Trip> nTrips;
    private Context context;
    private boolean showSelectSwitch;

    public TripAdapter(List<Trip> nTrips,  Context context, boolean showSelectSwitch) {
        this.nTrips = nTrips;
        this.context = context;
        this.showSelectSwitch = showSelectSwitch;
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i){
        View view = LayoutInflater.from(context).inflate(R.layout.trip_item, viewGroup, false);
        return new TripViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder tripViewHolder, int i) {
        final Trip trip = nTrips.get(i);
        String startDate = Util.formateaFecha(trip.getStartDate());
        String endDate = Util.formateaFecha(trip.getEndDate());

        tripViewHolder.city.setText(trip.getEndPLace());
        tripViewHolder.description.setText(trip.getDescription());
        tripViewHolder.startDate.setText(startDate);
        tripViewHolder.endDate.setText(endDate);
        tripViewHolder.price.setText(trip.getPrice()+"€");

        if(!trip.getUrl().isEmpty()) {
            Picasso.get()
                    .load(trip.getUrl())
                    .placeholder(android.R.drawable.ic_menu_myplaces)
                    .error(android.R.drawable.ic_menu_myplaces)
                    .into(tripViewHolder.picture);
        }

        if(trip.isSelected()) {
            tripViewHolder.star.setImageResource(android.R.drawable.star_on);
        }else{
            tripViewHolder.star.setImageResource(android.R.drawable.star_off);
        }

        tripViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, TripDetailActivity.class);
                intent.putExtra(Constants.IntentViaje, trip);
                intent.putExtra("showSelectSwitch", showSelectSwitch);
                ((Activity) context).startActivityForResult(intent, 1);
            }
        });

    }



    @Override
    public int getItemCount() {
        return nTrips.size();
    }

    public class TripViewHolder extends RecyclerView.ViewHolder {
        private ImageView picture;
        private ImageView star;
        private TextView city;
        private TextView description;
        private TextView price;
        private TextView startDate;
        private TextView endDate;
        private CardView cardView;

        public TripViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.trip_cardview);
            picture = itemView.findViewById(R.id.trip_picture);
            star = itemView.findViewById(R.id.trip_star);
            city = itemView.findViewById(R.id.trip_city);
            price = itemView.findViewById(R.id.trip_price);
            description = itemView.findViewById(R.id.trip_description);
            startDate = itemView.findViewById(R.id.trip_startdate);
            endDate = itemView.findViewById(R.id.trip_enddate);
        }
    }
}
