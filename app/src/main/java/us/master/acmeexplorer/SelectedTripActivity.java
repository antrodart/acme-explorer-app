package us.master.acmeexplorer;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import us.master.acmeexplorer.adapter.TripAdapter;
import us.master.acmeexplorer.dto.TripDTO;
import us.master.acmeexplorer.entity.Trip;
import us.master.acmeexplorer.entity.User;

public class SelectedTripActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TripAdapter tripAdapter;
    private TextView textViewNumberTripsSelected;
    private GridLayoutManager gridLayoutManager;
    private List<Trip> selectedTrips = new ArrayList<>();
    private User userPrincipal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_trip);
        textViewNumberTripsSelected = findViewById(R.id.textView_number_trips_selected);
        userPrincipal = getIntent().getParcelableExtra(MainActivity.USER_PRINCIPAL);

        getSelectedUserTrips();
    }

    private void getSelectedUserTrips() {
        FirebaseDatabaseService firebaseDatabaseService = FirebaseDatabaseService.getSeriveInstance();
        firebaseDatabaseService.getSelectedTripsFromUser(userPrincipal.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Long idCount = dataSnapshot.getChildrenCount();
                    for (DataSnapshot tripIdDS : dataSnapshot.getChildren()) {
                        String tripId = tripIdDS.getValue(String.class);
                        if (tripId != null && !tripId.isEmpty()) {

                            firebaseDatabaseService.getTrip(tripId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                                    if (dataSnapshot2.exists() && dataSnapshot2.getValue() != null) {
                                        TripDTO tripDTO = dataSnapshot2.getValue(TripDTO.class);
                                        if (tripDTO != null) {
                                            Trip trip = new Trip(tripDTO);
                                            trip.setSelected(true); // So the star is ON
                                            selectedTrips.add(trip);
                                        }
                                    }

                                    if (selectedTrips.size() == (idCount.intValue())) {
                                        mountRecyclerViewList();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError2) {
                                    Log.e("The second read failed: ", databaseError2.getMessage());
                                }
                            });

                        }
                    }
                } else {
                    mountRecyclerViewList();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("The read failed: ", databaseError.getMessage());
            }
        });
    }

    private void mountRecyclerViewList() {
        if (selectedTrips.size() > 1) {
            textViewNumberTripsSelected.setText("Has seleccionado " + selectedTrips.size() + " viajes");
        } else if (selectedTrips.size() == 1) {
            textViewNumberTripsSelected.setText("Has seleccionado 1 viaje");
        } else {
            textViewNumberTripsSelected.setText("No has seleccionado ningún viaje todavía");
        }

        recyclerView = findViewById(R.id.recyclerView_selected_trip);
        tripAdapter = new TripAdapter(selectedTrips, this, false, userPrincipal);

        gridLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(tripAdapter);
    }
}
