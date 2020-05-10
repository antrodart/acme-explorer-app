package us.master.acmeexplorer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import us.master.acmeexplorer.adapter.TripAdapter;
import us.master.acmeexplorer.dto.TripDTO;
import us.master.acmeexplorer.dto.UserDTO;
import us.master.acmeexplorer.entity.Trip;
import us.master.acmeexplorer.entity.User;

public class TripActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Switch switchColumns;
    private TripAdapter tripAdapter;
    private GridLayoutManager gridLayoutManager;
    private SharedPreferences sharedPreferencesFilters;
    List<Trip> trips = new ArrayList<>();
    List<Trip> filteredTrips = new ArrayList<>();
    private User userPrincipal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);
        recyclerView = findViewById(R.id.recyclerView);
        switchColumns = findViewById(R.id.switch_columns);
        // Recuperamos el filtro
        sharedPreferencesFilters = getSharedPreferences(Constants.filtroPreferences, MODE_PRIVATE);
        FirebaseDatabaseService firebaseDatabaseService = FirebaseDatabaseService.getSeriveInstance();

        userPrincipal = getIntent().getParcelableExtra(MainActivity.USER_PRINCIPAL);
        firebaseDatabaseService.getUser(userPrincipal.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    UserDTO userDTO = dataSnapshot.getValue(UserDTO.class);
                    if (userDTO != null) {
                        userPrincipal = new User(userDTO);
                        getTrips();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("The read failed: ", databaseError.getMessage());
            }
        });
    }

    private void getTrips() {
        FirebaseDatabaseService firebaseDatabaseService = FirebaseDatabaseService.getSeriveInstance();
        firebaseDatabaseService.getAllTrips().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    trips.clear();
                    for (DataSnapshot tripDS : dataSnapshot.getChildren()) {
                        TripDTO tripDTO = tripDS.getValue(TripDTO.class);
                        if (tripDTO != null) {
                            Trip trip = new Trip(tripDTO);
                            trip.setSelected(userPrincipal.getSelectedTrips().containsValue(trip.getId()));
                            trips.add(trip);
                        }
                    }
                    filteredTrips = comprobacionFiltros(trips);
                    showResultToast(filteredTrips);

                    tripAdapter = new TripAdapter(filteredTrips, TripActivity.this, true, userPrincipal);
                    switchColumns.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            int spanCount = isChecked ? 2 : 1;
                            gridLayoutManager.setSpanCount(spanCount);
                        }
                    });
                    gridLayoutManager = new GridLayoutManager(TripActivity.this, 1);
                    recyclerView.setLayoutManager(gridLayoutManager);
                    recyclerView.setAdapter(tripAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("The read failed: ", databaseError.getMessage());
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                userPrincipal = data.getParcelableExtra(Constants.USER_PRINCIPAL);
                getTrips();
                tripAdapter.notifyDataSetChanged();
            }
        } else if(requestCode == 2) {
            if (resultCode == RESULT_OK) {
                boolean newFilter = data.getBooleanExtra("NewFilter", false);
                if(newFilter) {
                    filteredTrips = comprobacionFiltros(trips);
                    showResultToast(filteredTrips);
                    tripAdapter = new TripAdapter(filteredTrips, this, true, userPrincipal);
                    recyclerView.swapAdapter(tripAdapter, false);
                }
            }
        }
    }

    private List<Trip> comprobacionFiltros(List<Trip> trips) {
        List<Trip> filteredTripsCheck = null;
        long fechaInicioSPLong = sharedPreferencesFilters.getLong(Constants.fechaInicio,0);
        long fechaFinSPLong = sharedPreferencesFilters.getLong(Constants.fechaFin, 0);
        String maxPriceString = sharedPreferencesFilters.getString(Constants.maxPrice, "");
        String minPriceString = sharedPreferencesFilters.getString(Constants.minPrice, "");
        Calendar fechaInicioSP = Calendar.getInstance();
        Calendar fechaFinSP = Calendar.getInstance();
        int maxPriceSP;
        int minPriceSP;

        fechaInicioSP.setTimeInMillis(fechaInicioSPLong * 1000);
        fechaFinSP.setTimeInMillis(fechaFinSPLong * 1000);

        if(fechaInicioSPLong != 0 && fechaFinSPLong == 0){ // Se ha filtrado por fecha inicio pero no de fin
            filteredTripsCheck = trips.stream().filter(t -> t.getStartDate().after(fechaInicioSP)).collect(Collectors.toList());
        }else if(fechaInicioSPLong == 0 && fechaFinSPLong != 0) { // Se ha filtrado por fecha fin pero no de inicio
            filteredTripsCheck = trips.stream().filter(t -> t.getEndDate().before(fechaFinSP)).collect(Collectors.toList());
        } else if(fechaInicioSPLong != 0) { // Se ha filtrado por las 2 fechas
            filteredTripsCheck = trips.stream().filter(t -> t.getStartDate().after(fechaInicioSP) &&
                    t.getEndDate().before(fechaFinSP)).collect(Collectors.toList());
        } else{ // No se ha filtrado por ninguna fecha
            filteredTripsCheck = trips;
        }

        if(!maxPriceString.equals("") && minPriceString.equals("")){ // Se ha filtrado por precio máximo pero no mínimo
            maxPriceSP = Integer.parseInt(maxPriceString);
            filteredTripsCheck = filteredTripsCheck.stream().filter(t -> t.getPrice() < maxPriceSP).collect(Collectors.toList());
        }else if(maxPriceString.equals("") && !minPriceString.equals("")) { // Se ha filtrado por precio mínimo pero no máximo
            minPriceSP = Integer.parseInt(minPriceString);
            filteredTripsCheck = filteredTripsCheck.stream().filter(t -> t.getPrice() > minPriceSP).collect(Collectors.toList());
        } else if(!maxPriceString.equals("")) { // Se ha filtrado por los 2 precios
            maxPriceSP = Integer.parseInt(maxPriceString);
            minPriceSP = Integer.parseInt(minPriceString);
            filteredTripsCheck = filteredTripsCheck.stream().filter(t -> t.getPrice() > minPriceSP &&
                    t.getPrice() < maxPriceSP).collect(Collectors.toList());
        }

        return filteredTripsCheck;
    }

    public void openFilterActivity(View view) {
        Intent intent = new Intent(TripActivity.this, FilterTripActivity.class);
        intent.putExtra("NewFilter", false);
        startActivityForResult(intent, 2);
    }

    private void showResultToast(List<Trip> filteredTrips) {
        if(filteredTrips.size() == 0){
            Toast.makeText(this, "No hay viajes con las condiciones de filtro", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this, "Hay " + filteredTrips.size() + " viajes con las condiciones de filtro", Toast.LENGTH_LONG).show();
        }
    }
}
