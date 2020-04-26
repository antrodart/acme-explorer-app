package us.master.acmeexplorer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import us.master.acmeexplorer.adapter.TripAdapter;
import us.master.acmeexplorer.entity.Trip;

public class TripActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Switch switchColumns;
    private ImageView star;
    private TripAdapter tripAdapter;
    private GridLayoutManager gridLayoutManager;
    private SharedPreferences sharedPreferencesFilters;
    List<Trip> trips;
    List<Trip> filteredTrips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);
        recyclerView = findViewById(R.id.recyclerView);
        switchColumns = findViewById(R.id.switch_columns);
        //star = findViewById(R.id.image_star);

        // Recuperamos el filtro
        sharedPreferencesFilters = getSharedPreferences(Constants.filtroPreferences, MODE_PRIVATE);

        if(Constants.TRIPS == null){
            Constants.TRIPS = Trip.generateTrips(20);
        }
        trips = Constants.TRIPS;
        // Filtramos directamente aunque no sea lo más adecuado
        filteredTrips = comprobacionFiltros(trips);
        showResultToast(filteredTrips);

        tripAdapter = new TripAdapter(filteredTrips, this, true);

        switchColumns.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int spanCount = isChecked ? 2 : 1;
                gridLayoutManager.setSpanCount(spanCount);
            }
        });
        gridLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(tripAdapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                int idTrip = data.getIntExtra("TripId", 0);
                boolean selectedTrip = data.getBooleanExtra("TripSelected", false);
                if (idTrip != 0) {
                    this.trips.get(idTrip - 1).setSelected(selectedTrip);
                    this.filteredTrips.stream().filter(t -> t.getId() == idTrip).collect(Collectors.toList()).get(0).setSelected(selectedTrip);
                    tripAdapter.notifyDataSetChanged();
                }
            }
        } else if(requestCode == 2) {
            if(resultCode == RESULT_OK) {
                boolean newFilter = data.getBooleanExtra("NewFilter", false);
                if(newFilter) {
                    filteredTrips = comprobacionFiltros(trips);
                    showResultToast(filteredTrips);
                    tripAdapter = new TripAdapter(filteredTrips, this, true);
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
        //startActivity(new Intent( TripActivity.this, FilterTripActivity.class));
    }

    private void showResultToast(List<Trip> filteredTrips) {
        if(filteredTrips.size() == 0){
            Toast.makeText(this, "No hay viajes con las condiciones de filtro", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this, "Hay " + filteredTrips.size() + " viajes con las condiciones de filtro", Toast.LENGTH_LONG).show();
        }
    }
}
