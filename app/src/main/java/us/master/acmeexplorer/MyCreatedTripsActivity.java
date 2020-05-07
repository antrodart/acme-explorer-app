package us.master.acmeexplorer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

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

public class MyCreatedTripsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TripAdapter tripAdapter;
    private GridLayoutManager gridLayoutManager;
    private Button createNewTripButton;
    private User user;
    private List<Trip> myCreatedTrips = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_created_trips);

        recyclerView = findViewById(R.id.recyclerView_created_trips);
        createNewTripButton = findViewById(R.id.create_trip_button);

        user = getIntent().getParcelableExtra(MainActivity.USER_PRINCIPAL);

        FirebaseDatabaseService firebaseDatabaseService = FirebaseDatabaseService.getSeriveInstance();

        firebaseDatabaseService.getTripsFromCreator(user.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot tripDS : dataSnapshot.getChildren()) {
                        TripDTO tripDTO = tripDS.getValue(TripDTO.class);
                        if (tripDTO != null) {
                            Trip trip = new Trip(tripDTO);
                            myCreatedTrips.add(trip);
                        }
                    }
                    tripAdapter = new TripAdapter(myCreatedTrips, MyCreatedTripsActivity.this, false);
                    gridLayoutManager = new GridLayoutManager(MyCreatedTripsActivity.this, 1);
                    recyclerView.setLayoutManager(gridLayoutManager);
                    recyclerView.setAdapter(tripAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("The read failed: ", databaseError.getMessage());
            }
        });

        createNewTripButton.setOnClickListener(v -> redirectCreateNewTrip());

    }

    private void redirectCreateNewTrip() {
        Intent intent = new Intent(this, TripCreateActivity.class);
        intent.putExtra(Constants.USER_PRINCIPAL, user);
        startActivityForResult(intent, 3);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 3) {
            if (resultCode == RESULT_OK) {
                Trip newTrip = (Trip) data.getSerializableExtra("NewTrip");
                if (newTrip != null) {
                    myCreatedTrips.add(newTrip);
                    tripAdapter = new TripAdapter(myCreatedTrips, this, false);
                    recyclerView.swapAdapter(tripAdapter, false);
                }
            }
        }
    }
}
