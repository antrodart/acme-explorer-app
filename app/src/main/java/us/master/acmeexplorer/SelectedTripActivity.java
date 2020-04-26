package us.master.acmeexplorer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import us.master.acmeexplorer.adapter.TripAdapter;
import us.master.acmeexplorer.entity.Trip;

public class SelectedTripActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TripAdapter tripAdapter;
    private TextView textViewNumberTripsSelected;
    private GridLayoutManager gridLayoutManager;
    private List<Trip> selectedTrips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_trip);
        textViewNumberTripsSelected = findViewById(R.id.textView_number_trips_selected);

        if (Constants.TRIPS == null || Constants.TRIPS.isEmpty()){
            selectedTrips = new ArrayList<>();
            textViewNumberTripsSelected.setText("No has seleccionado ningún viaje todavía");
        } else {
            selectedTrips = Constants.TRIPS.stream().filter(Trip::isSelected).collect(Collectors.toList());
            if(selectedTrips.size() > 1){
                textViewNumberTripsSelected.setText("Has seleccionado " + selectedTrips.size() + " viajes");
            } else if(selectedTrips.size() == 1){
                textViewNumberTripsSelected.setText("Has seleccionado 1 viaje");
            } else {
                textViewNumberTripsSelected.setText("No has seleccionado ningún viaje todavía");
            }
        }

        recyclerView = findViewById(R.id.recyckerView_selected_trip);
        tripAdapter = new TripAdapter(selectedTrips, this, false);

        gridLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(tripAdapter);
    }
}
