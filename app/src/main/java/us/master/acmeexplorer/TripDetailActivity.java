package us.master.acmeexplorer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import us.master.acmeexplorer.entity.Trip;
import us.master.acmeexplorer.entity.User;

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
    private Boolean selected;
    private Button buyButton;
    private static Context context;
    private User userPrincipal;

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

        FirebaseDatabaseService firebaseDatabaseService = FirebaseDatabaseService.getSeriveInstance();

        try{
            showSelectSwitch = getIntent().getBooleanExtra("showSelectSwitch",true);
            trip = getIntent().getParcelableExtra(Constants.IntentViaje);
            userPrincipal = getIntent().getParcelableExtra(Constants.USER_PRINCIPAL);
            selected = getIntent().getBooleanExtra("selected", false);
            if(trip != null) {

                if (showSelectSwitch){
                    selected_trip_switch.setChecked(selected);
                    buyButton.setVisibility(View.GONE);
                }else {
                    selected_trip_switch.setVisibility(View.GONE);
                }
                detail_trip_city.setText(trip.getEndPlace());
                detail_trip_description.setText(trip.getDescription());
                city_textview_detail.setText(trip.getEndPlace());
                price_textview_detail.setText(trip.getPrice().toString()+"€");
                startdate_textview_detail.setText(Util.dateFormatSpanish(trip.getStartDate()));
                enddate_textview_detail.setText(Util.dateFormatSpanish(trip.getEndDate()));
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
                    if (isChecked) {
                        firebaseDatabaseService.setTripAsSelected(userPrincipal.getId(), trip.getId(), (databaseError, databaseReference) -> {
                            if (databaseError == null) {
                                userPrincipal.getSelectedTrips().put(trip.getId(), trip.getId());
                                Log.i("AcmeExplorer", "El viaje se ha establecido como seleccionado satisfactoriamente");
                            } else {
                                Log.i("AcmeExplorer", "Ha habido un error al establecer el trip como seleccionado: " + databaseError.getMessage());
                            }
                        });
                    } else {
                        firebaseDatabaseService.setTripAsNotSelected(userPrincipal.getId(), trip.getId(), (databaseError, databaseReference) -> {
                            if (databaseError == null) {
                                userPrincipal.getSelectedTrips().remove(trip.getId());
                                Log.i("AcmeExplorer", "El viaje se ha quitado como seleccionado satisfactoriamente");
                            } else {
                                Log.i("AcmeExplorer", "Ha habido un error al quitar el trip como seleccionado: " + databaseError.getMessage());
                            }
                        });

                    }
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
        intent.putExtra(Constants.USER_PRINCIPAL, userPrincipal);
        setResult(RESULT_OK, intent);
        finish();
    }
}
