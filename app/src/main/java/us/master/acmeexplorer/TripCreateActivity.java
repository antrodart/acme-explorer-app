package us.master.acmeexplorer;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;

import us.master.acmeexplorer.entity.Trip;
import us.master.acmeexplorer.entity.User;

public class TripCreateActivity extends AppCompatActivity {

    private Calendar startDatePicked;
    private Calendar endDatePicked;
    private AutoCompleteTextView trip_create_startPlace_tv;
    private AutoCompleteTextView trip_create_endPlace_tv;
    private AutoCompleteTextView trip_create_description_tv;
    private AutoCompleteTextView trip_create_price_tv;
    private AutoCompleteTextView trip_create_picture_tv;
    private TextInputLayout trip_create_startPlace;
    private TextInputLayout trip_create_endPlace;
    private TextInputLayout trip_create_description;
    private TextInputLayout trip_create_price;
    private TextInputLayout trip_create_picture;
    private TextView startDateTV;
    private TextView endDateTV;
    private Button trip_save_button;
    private ProgressBar progressBar;
    private User user;
    private Trip trip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_create);

        FirebaseDatabaseService firebaseDatabaseService = FirebaseDatabaseService.getSeriveInstance();

        user = getIntent().getParcelableExtra(Constants.USER_PRINCIPAL);
        trip_create_startPlace_tv = findViewById(R.id.trip_create_startPlace_tv);
        trip_create_endPlace_tv = findViewById(R.id.trip_create_endPlace_tv);
        trip_create_description_tv = findViewById(R.id.trip_create_description_tv);
        trip_create_price_tv = findViewById(R.id.trip_create_price_tv);
        trip_create_picture_tv = findViewById(R.id.trip_create_picture_tv);
        startDateTV = findViewById(R.id.trip_create_startDate_tv);
        endDateTV = findViewById(R.id.trip_create_endDate_tv);

        trip_create_startPlace = findViewById(R.id.trip_create_startPlace);
        trip_create_endPlace = findViewById(R.id.trip_create_endPlace);
        trip_create_description = findViewById(R.id.trip_create_description);
        trip_create_price = findViewById(R.id.trip_create_price);
        trip_create_picture = findViewById(R.id.trip_create_picture);

        trip_save_button = findViewById(R.id.trip_create_save_button);
        progressBar = findViewById(R.id.user_edit_progress);

        progressBar.setVisibility(View.GONE);

        trip_save_button.setOnClickListener(v -> {
            if (!existErrorInFields()) {
                progressBar.setVisibility(View.VISIBLE);
                createTrip();
                firebaseDatabaseService.saveTrip(trip, (databaseError, databaseReference) -> {
                    if (databaseError == null) {
                        Log.i("AcmeExplorer", "El viaje se ha creado correctamente");
                        redirectMyCreatedTripsActivity();
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Log.i("AcmeExplorer", "Error al crear el viaje: " + databaseError.getMessage());
                    }
                });
            }
        });
    }

    private void redirectMyCreatedTripsActivity() {
        progressBar.setVisibility(View.GONE);
        Intent intent = new Intent();
        intent.putExtra("NewTrip", trip);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void createTrip() {
        trip = new Trip();
        trip.setStartPlace(trip_create_startPlace_tv.getText().toString());
        trip.setEndPlace(trip_create_endPlace_tv.getText().toString());
        trip.setDescription(trip_create_description_tv.getText().toString());
        trip.setPrice(Long.valueOf(trip_create_price_tv.getText().toString()));
        trip.setStartDate(startDatePicked);
        trip.setEndDate(endDatePicked);
        trip.setCreator(user);
        trip.setSelected(false);

        String url = trip_create_picture_tv.getText().toString();
        if (url.isEmpty()) {
            url = Constants.DEFAULT_TRIP_PICTURE;
        }
        trip.setUrl(url);
    }

    private boolean existErrorInFields() {
        boolean error = false;
        Calendar today = Calendar.getInstance();

        if (trip_create_startPlace_tv.getText().toString().isEmpty()) {
            trip_create_startPlace.setErrorEnabled(true);
            trip_create_startPlace.setError(getText(R.string.trip_create_place_error));
            error = true;
        }
        if (trip_create_endPlace_tv.getText().toString().isEmpty()) {
            trip_create_endPlace.setErrorEnabled(true);
            trip_create_endPlace.setError(getText(R.string.trip_create_place_error));
            error = true;
        }
        if (trip_create_description_tv.getText().toString().isEmpty()) {
            trip_create_description.setErrorEnabled(true);
            trip_create_description.setError(getText(R.string.trip_create_description_error));
            error = true;
        }

        if (trip_create_price_tv.getText().toString().isEmpty()) {
            trip_create_price.setErrorEnabled(true);
            trip_create_price.setError(getText(R.string.trip_create_price_error));
            error = true;
        } else {
            try {
                Long.valueOf(trip_create_price_tv.getText().toString());
            } catch (Exception e) {
                trip_create_price.setErrorEnabled(true);
                trip_create_price.setError(getText(R.string.trip_create_price_error));
                error = true;
            }
        }

        if (startDatePicked.getTimeInMillis() > endDatePicked.getTimeInMillis()) {
            Toast.makeText(this, getText(R.string.trip_create_dates_error), Toast.LENGTH_LONG).show();
            error = true;
        } else if (today.getTimeInMillis() > startDatePicked.getTimeInMillis()) {
            Toast.makeText(this, getText(R.string.trip_create_dates_error_2), Toast.LENGTH_LONG).show();
            error = true;
        }

        return error;
    }

    public void tripCreateEndDateClick(View view) {
        if (endDatePicked == null) endDatePicked = Calendar.getInstance();
        int yy = endDatePicked.get(Calendar.YEAR);
        int mm = endDatePicked.get(Calendar.MONTH);
        int dd = endDatePicked.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this, (view1, year, month, dayOfMonth) -> {
            endDatePicked.set(year, month, dayOfMonth);
            endDateTV.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
        }, yy, mm, dd);
        dialog.show();
    }

    public void tripCreateStartDateClick(View view) {
        if (startDatePicked == null) startDatePicked = Calendar.getInstance();
        int yy = startDatePicked.get(Calendar.YEAR);
        int mm = startDatePicked.get(Calendar.MONTH);
        int dd = startDatePicked.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this, (view1, year, month, dayOfMonth) -> {
            startDatePicked.set(year, month, dayOfMonth);
            startDateTV.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
        }, yy, mm, dd);
        dialog.show();
    }
}
