package us.master.acmeexplorer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import us.master.acmeexplorer.entity.Trip;

public class TripDetailActivity extends AppCompatActivity  {

    private Trip trip;
    private TextView detail_trip_city;
    private ImageView detail_trip_picture;
    private TextView detail_trip_description;
    private TextView city_textview_detail;
    private TextView price_textview_detail;
    private TextView startdate_textview_detail;
    private TextView enddate_textview_detail;
    private TextView exit_place_textview_detail;
    private Switch selected_trip_switch;
    private boolean showSelectSwitch;
    private Button buyButton;
    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_detail);

        TripDetailActivity.context = getApplicationContext();
        detail_trip_city = findViewById(R.id.detail_trip_city);
        detail_trip_picture = findViewById(R.id.detail_trip_picture);
        detail_trip_description = findViewById(R.id.detail_trip_description);
        city_textview_detail = findViewById(R.id.city_textview_detail);
        price_textview_detail = findViewById(R.id.price_textview_detail);
        startdate_textview_detail = findViewById(R.id.startdate_textview_detail);
        enddate_textview_detail = findViewById(R.id.enddate_textview_detail);
        exit_place_textview_detail = findViewById(R.id.exit_place_textview_detail);
        selected_trip_switch = findViewById(R.id.selected_trip_switch);
        buyButton = findViewById(R.id.buy_button);

        try{
            showSelectSwitch = getIntent().getBooleanExtra("showSelectSwitch",true);
            trip = (Trip) getIntent().getSerializableExtra(Constants.IntentViaje);
            if(trip != null) {

                if (showSelectSwitch){
                    selected_trip_switch.setChecked(trip.isSelected());
                    buyButton.setVisibility(View.GONE);
                }else {
                    selected_trip_switch.setVisibility(View.GONE);
                }
                detail_trip_city.setText(trip.getEndPLace());
                detail_trip_description.setText(trip.getDescription());
                city_textview_detail.setText(trip.getEndPLace());
                price_textview_detail.setText(trip.getPrice()+"€");
                startdate_textview_detail.setText(Util.formateaFecha(trip.getStartDate()));
                enddate_textview_detail.setText(Util.formateaFecha(trip.getEndDate()));
                exit_place_textview_detail.setText(trip.getStartPlace());
                if(!trip.getUrl().isEmpty()) {
                    Picasso.get()
                            .load(trip.getUrl())
                            .placeholder(android.R.drawable.ic_menu_myplaces)
                            .error(android.R.drawable.ic_menu_myplaces)
                            .into(detail_trip_picture);
                }
            }

            selected_trip_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    trip.setSelected(isChecked);
                    Constants.TRIPS.get(trip.getId()-1).setSelected(isChecked);
                }
            });

            buyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(TripDetailActivity.context,
                            "Funcionalidad de comprar viaje no implementada todavía",
                            Toast.LENGTH_LONG).show();
                }
            });

        }catch (Exception e){
            Toast.makeText(this, "Se ha producido un error.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("TripId",trip.getId());
        intent.putExtra("TripSelected", trip.isSelected());
        setResult(RESULT_OK, intent);
        finish();
    }
}
