package us.master.acmeexplorer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import us.master.acmeexplorer.dto.UserDTO;
import us.master.acmeexplorer.entity.User;
import us.master.acmeexplorer.resttype.WeatherResponse;
import us.master.acmeexplorer.resttype.WeatherRetrofitInterface;

public class LocationActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int PERMISSION_REQUEST_CODE_LOCATION = 0x123;
    public static String USER_PRINCIPAL = "userPrincipal";
    private User userPrincipal;
    private FirebaseDatabaseService firebaseDatabaseService;
    private GoogleMap googleMap;
    private User mostCloseUser = new User();
    private SupportMapFragment supportMapFragment;
    private TextView location;
    private TextView weatherLocation;
    private TextView locationAddress;
    private Retrofit retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        location = findViewById(R.id.location);
        weatherLocation = findViewById(R.id.weather_location);
        locationAddress = findViewById(R.id.location_address);
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.location_map);
        userPrincipal = getIntent().getParcelableExtra(USER_PRINCIPAL);
        firebaseDatabaseService = FirebaseDatabaseService.getSeriveInstance();

        retrofit = new Retrofit.Builder().baseUrl("https://api.openweathermap.org/").addConverterFactory(GsonConverterFactory.create()).build();

        String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        if (ContextCompat.checkSelfPermission(this, permissions[0]) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                Snackbar.make(location, R.string.location_rationale, Snackbar.LENGTH_LONG).setAction(R.string.location_rationale_ok, view ->
                        ActivityCompat.requestPermissions(LocationActivity.this, permissions, PERMISSION_REQUEST_CODE_LOCATION)
                ).show();
            } else {
                ActivityCompat.requestPermissions(LocationActivity.this, permissions, PERMISSION_REQUEST_CODE_LOCATION);
            }
        } else {
            startLocationService();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResult) {
        if (requestCode == PERMISSION_REQUEST_CODE_LOCATION) {
            if (grantResult.length > 0 && grantResult[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationService();
            } else {
                Toast.makeText(this, R.string.location_cancel, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void startLocationService() {
        // Ubicación puntual: sólo en este momento.
        FusedLocationProviderClient locationServices = LocationServices.getFusedLocationProviderClient(this);
        locationServices.getLastLocation().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Location location = task.getResult();
                List<User> usuarios = new ArrayList<>();
                saveUserLocation(location);

                firebaseDatabaseService.getAllUsers().addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot userDS : dataSnapshot.getChildren()) {
                                UserDTO userDTO = userDS.getValue(UserDTO.class);
                                if (userDTO != null) {
                                    User user = new User(userDTO);
                                    usuarios.add(user);
                                }
                            }
                            Float distanceInMetersRecord = null;

                            for (User u : usuarios) {
                                if (!u.getEmail().equals(userPrincipal.getEmail()) && u.getLatitude() != null && u.getLongitude() != null) {
                                    Location targetLocation = new Location("");//provider name is unnecessary
                                    targetLocation.setLatitude(u.getLatitude());
                                    targetLocation.setLongitude(u.getLongitude());

                                    float distanceInMeters = targetLocation.distanceTo(location);

                                    if (distanceInMetersRecord == null || distanceInMeters < distanceInMetersRecord) {
                                        distanceInMetersRecord = distanceInMeters;
                                        mostCloseUser.setLatitude(u.getLatitude());
                                        mostCloseUser.setLongitude(u.getLongitude());
                                        mostCloseUser.setName(u.getName());
                                        mostCloseUser.setSurname(u.getSurname());
                                    }
                                }
                            }
                            supportMapFragment.getMapAsync(LocationActivity.this);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("The read failed: ", databaseError.getMessage());
                    }
                });
            }
        });
    }

    private void saveUserLocation(Location location) {
        userPrincipal.setLatitude(location.getLatitude());
        userPrincipal.setLongitude(location.getLongitude());

        firebaseDatabaseService.saveUser(userPrincipal, ((databaseError, databaseReference) -> {
            if (databaseError == null) {
                Log.i("AcmeExplorer", "El usuario se ha actualizado correctamente: " + userPrincipal.getId());
            } else {
                Log.i("AcmeExplorer", "Error al actualizar el usuario: " + databaseError.getMessage());
            }
        }));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        LatLng locationUserPrincipal = new LatLng(userPrincipal.getLatitude(), userPrincipal.getLongitude());
        googleMap.addMarker(new MarkerOptions().title("Mi posición").position(locationUserPrincipal));
        float latitude;
        float longitude;

        if (mostCloseUser.getLatitude() == null || mostCloseUser.getLongitude() == null) {
            // No existe usuario más cercano
            location.setText("No hay usuarios cercanos. Mostrando su posición.");
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locationUserPrincipal, 8));
            latitude = mostCloseUser.getLatitude().floatValue();
            longitude = mostCloseUser.getLongitude().floatValue();

        } else {
            Location targetLocation = new Location("");
            Location userPrincipalLocation = new Location("");
            targetLocation.setLatitude(mostCloseUser.getLatitude());
            targetLocation.setLongitude(mostCloseUser.getLongitude());
            userPrincipalLocation.setLatitude(userPrincipal.getLatitude());
            userPrincipalLocation.setLongitude(userPrincipal.getLongitude());
            float distancia = targetLocation.distanceTo(userPrincipalLocation);
            latitude = mostCloseUser.getLatitude().floatValue();
            longitude = mostCloseUser.getLongitude().floatValue();

            LatLng locationTarget = new LatLng(mostCloseUser.getLatitude(), mostCloseUser.getLongitude());
            googleMap.addMarker(new MarkerOptions().title("Usuario más cercano: " + mostCloseUser.getName()).position(locationTarget));
            location.setText("El usuario más cercano es " + mostCloseUser.getName() + " " +
                    mostCloseUser.getSurname() + ". Se encuentra a una distancia de " + distancia + " metros.");
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locationTarget, 8));

        }

        WeatherRetrofitInterface service = retrofit.create(WeatherRetrofitInterface.class);
        Call<WeatherResponse> response = service.getCurrentWeather(latitude, longitude,
                getString(R.string.open_weather_map_api_key), "metric");

        response.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse wr = response.body();
                    String weatherLocationText = "La temperatura en " + wr.getName() + " es de " + wr.getMain().getTemp() + " ºC.";
                    weatherLocation.setText(weatherLocationText);
                    Log.i("AcmeExplorer", "La temperatura actual es " + wr.getMain().getTemp());
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Log.i("AcmeExplorer", "REST: error en la llamada. " + t.getMessage());
            }
        });

        getAddress(latitude, longitude);

    }

    private void getAddress(float latitude, float longitude) {
        try {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());

            addresses = geocoder.getFromLocation(latitude, longitude, 1);

            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

            locationAddress.setText("Se encuentra en la calle " + address);
        } catch (Exception e) {
            Log.e("AcmeExplorer", "Geocoder error: " + e.getMessage());
        }

    }

}
