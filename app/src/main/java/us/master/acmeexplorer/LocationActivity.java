package us.master.acmeexplorer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.snackbar.Snackbar;

public class LocationActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE_LOCATION = 0x123;
    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null || locationResult.getLastLocation() == null || !locationResult.getLastLocation().hasAccuracy()) {
                return;
            }
            Location location = locationResult.getLastLocation();
            Log.i("AcmeExplorer", "Location: " + location.getLatitude() + ", " + location.getLongitude() + ", " + location.getAccuracy());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        TextView location = findViewById(R.id.location);

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
                /*locationServices.getLastLocation().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        Location location = task.getResult();
                        //location.getLatitude(); location.getLongitude();
                        Log.i("AcmeExplorer", "Location: " + location.getLatitude() + ", " + location.getLongitude() + ", " + location.getAccuracy());
                    }
                });*/
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        //locationRequest.setSmallestDisplacement(10); Informa cuando el usuario se desplaza 10 metros
        locationServices.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    /**
     * De esta forma liberamos al sistema de tener que consultar la localización constantemente
     */
    public void stopService() {
        LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(locationCallback);
    }
}
