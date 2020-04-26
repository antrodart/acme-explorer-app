package us.master.acmeexplorer;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;

public class FilterTripActivity extends AppCompatActivity {

    private Calendar startDate;
    private Calendar endDate;
    private Integer maxPrice;
    private Integer minPrice;
    private TextView startDateText;
    private TextView endDateText;
    private EditText maxPriceEditText;
    private EditText minPriceEditText;
    Button saveFiltersButton;
    Button deleteFiltersButton;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    boolean newFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_trip);
        saveFiltersButton = findViewById(R.id.filtrar_boton);
        deleteFiltersButton = findViewById(R.id.borrar_filtros);
        maxPriceEditText = findViewById(R.id.precio_max_edittext);
        minPriceEditText = findViewById(R.id.precio_min_edittext);
        startDateText = findViewById(R.id.startDate_text);
        endDateText = findViewById(R.id.endDate_text);
        newFilter = false;

        // Guardamos en nuestros atributos las variables recibidas por el SharedPreferences
        inicializeVariablesFromSharedPreferences();

        // Insertamos en las vistas de la pantalla los valores de las variables
        if (startDate != null) startDateText.setText(Util.dateFormatSpanish(startDate));
        if (endDate != null) endDateText.setText(Util.dateFormatSpanish(endDate));
        if (maxPrice != null) maxPriceEditText.setText(maxPrice.toString());
        if (minPrice != null) minPriceEditText.setText(minPrice.toString());

        saveFiltersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String maxPriceStringText;
                String minPriceStringText;

                newFilter = true;

                if(maxPrice == null) maxPriceStringText = maxPriceEditText.getText().toString();
                else maxPriceStringText = maxPrice.toString();

                if(minPrice == null) minPriceStringText = minPriceEditText.getText().toString();
                else minPriceStringText = minPrice.toString();

                editor = sharedPreferences.edit();
                editor.putLong(Constants.fechaInicio, Util.calendar2long(startDate));
                editor.putLong(Constants.fechaFin, Util.calendar2long(endDate));
                editor.putString(Constants.maxPrice, maxPriceStringText);
                editor.putString(Constants.minPrice, minPriceStringText);
                editor.apply();

                onBackPressed();
            }
        });

        deleteFiltersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newFilter = true;

                editor = sharedPreferences.edit();
                editor.putLong(Constants.fechaInicio, 0);
                editor.putLong(Constants.fechaFin, 0);
                editor.putString(Constants.maxPrice, "");
                editor.putString(Constants.minPrice, "");
                editor.apply();

                startDateText.setText("Vacío");
                endDateText.setText("Vacío");
                maxPriceEditText.setText("");
                minPriceEditText.setText("");

                onBackPressed();
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("NewFilter",newFilter);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void inicializeVariablesFromSharedPreferences() {
        sharedPreferences = getSharedPreferences(Constants.filtroPreferences, MODE_PRIVATE);
        long fechaInicioSPLong = sharedPreferences.getLong(Constants.fechaInicio,0);
        long fechaFinSPLong = sharedPreferences.getLong(Constants.fechaFin, 0);
        String maxPriceString = sharedPreferences.getString(Constants.maxPrice, "");
        String minPriceString = sharedPreferences.getString(Constants.minPrice, "");
        Calendar fechaInicioSP = Calendar.getInstance();
        Calendar fechaFinSP = Calendar.getInstance();

        fechaInicioSP.setTimeInMillis(fechaInicioSPLong * 1000);
        fechaFinSP.setTimeInMillis(fechaFinSPLong * 1000);

        if(fechaInicioSPLong != 0 && fechaFinSPLong == 0){ // Se ha filtrado por fecha inicio pero no de fin
            startDate = fechaInicioSP;
        }else if(fechaInicioSPLong == 0 && fechaFinSPLong != 0) { // Se ha filtrado por fecha fin pero no de inicio
            endDate = fechaFinSP;
        } else if(fechaInicioSPLong != 0) { // Se ha filtrado por las 2 fechas
            startDate = fechaInicioSP;
            endDate = fechaFinSP;
        }

        if(!maxPriceString.equals("") && minPriceString.equals("")){ // Se ha filtrado por precio máx
            maxPrice = Integer.parseInt(maxPriceString);
        }else if(maxPriceString.equals("") && !minPriceString.equals("")){ // Se ha filtrado por precio min
            minPrice = Integer.parseInt(minPriceString);
        }else if(!maxPriceString.equals("")){ // Se ha filtrado por los 2 precios
            maxPrice = Integer.parseInt(maxPriceString);
            minPrice = Integer.parseInt(minPriceString);
        }
    }

    public void endDateClick(View view) {
        if(endDate == null) endDate = Calendar.getInstance();
        int yy = endDate.get(Calendar.YEAR);
        int mm = endDate.get(Calendar.MONTH);
        int dd = endDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                endDate.set(year, month, dayOfMonth);
                endDateText.setText(dayOfMonth + "/" + (month+1) + "/" + year);
            }
        }, yy, mm, dd);
        dialog.show();
    }

    public void startDateClick(View view) {
        if(startDate == null) startDate = Calendar.getInstance();
        int yy = startDate.get(Calendar.YEAR);
        int mm = startDate.get(Calendar.MONTH);
        int dd = startDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                startDate = Calendar.getInstance();
                startDate.set(year, month, dayOfMonth);
                startDateText.setText(dayOfMonth + "/" + (month+1) + "/" + year);
            }
        }, yy, mm, dd);
        dialog.show();
    }

}
