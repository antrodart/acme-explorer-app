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

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;

import us.master.acmeexplorer.entity.User;

public class UserEditActivity extends AppCompatActivity {

    private Calendar birthDatePicked;
    private AutoCompleteTextView user_edit_name_tv;
    private AutoCompleteTextView user_edit_surname_tv;
    private AutoCompleteTextView user_edit_city_tv;
    private AutoCompleteTextView user_edit_picture_tv;
    private AutoCompleteTextView user_edit_email_tv;
    private TextInputLayout user_edit_name;
    private TextInputLayout user_edit_surname;
    private TextInputLayout user_edit_city;
    private TextInputLayout user_edit_picture;
    private TextView birthDateTV;
    private Button user_edit_save_button;
    private ProgressBar progressBar;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit);

        FirebaseDatabaseService firebaseDatabaseService = FirebaseDatabaseService.getSeriveInstance();

        user = getIntent().getParcelableExtra(UserDetailActivity.USER_PRINCIPAL);
        user_edit_name_tv = findViewById(R.id.user_edit_name_tv);
        user_edit_surname_tv = findViewById(R.id.user_edit_surname_tv);
        user_edit_email_tv = findViewById(R.id.user_edit_email_tv);
        user_edit_city_tv = findViewById(R.id.user_edit_city_tv);
        user_edit_picture_tv = findViewById(R.id.user_edit_picture_tv);
        birthDateTV = findViewById(R.id.user_edit_birthdate);

        user_edit_name = findViewById(R.id.user_edit_name);
        user_edit_surname = findViewById(R.id.user_edit_surname);
        user_edit_city = findViewById(R.id.user_edit_city);
        user_edit_picture = findViewById(R.id.user_edit_picture);

        user_edit_save_button = findViewById(R.id.user_edit_save_button);
        progressBar = findViewById(R.id.user_edit_progress);

        progressBar.setVisibility(View.GONE);

        fillFieldsWithUserData();

        user_edit_save_button.setOnClickListener(v -> {
            if (!existErrorinFields()) {
                progressBar.setVisibility(View.VISIBLE);
                updateUser();
                firebaseDatabaseService.saveUser(user, ((databaseError, databaseReference) -> {
                    if (databaseError == null) {
                        Log.i("AcmeExplorer", "El usuario se ha actualizado correctamente: " + user.getId());
                        redirectShowProfileActivity();
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Log.i("AcmeExplorer", "Error al actualizar el usuario: " + databaseError.getMessage());
                    }
                }));
            }
        });

    }

    private void redirectShowProfileActivity() {
        progressBar.setVisibility(View.GONE);
        Intent intent = new Intent(this, UserDetailActivity.class);
        intent.putExtra(UserDetailActivity.USER_PRINCIPAL, user);
        startActivity(intent);
    }

    private void fillFieldsWithUserData() {
        user_edit_name_tv.setText(user.getName());
        user_edit_surname_tv.setText(user.getSurname());
        user_edit_email_tv.setText(user.getEmail());
        user_edit_city_tv.setText(user.getCity());
        user_edit_picture_tv.setText(user.getPicture());
        birthDatePicked = user.getBirthDate();
        if (birthDatePicked != null) {
            birthDateTV.setText(Util.dateFormatSpanish(birthDatePicked));
        }
    }

    private void updateUser() {
        user.setName(user_edit_name_tv.getText().toString());
        user.setSurname(user_edit_surname_tv.getText().toString());
        user.setCity(user_edit_city_tv.getText().toString());
        user.setPicture(user_edit_picture_tv.getText().toString());
        user.setBirthDate(birthDatePicked);
    }

    public boolean existErrorinFields() {
        boolean result = false;

        if (user_edit_name_tv.getText().toString().isEmpty()) {
            user_edit_name.setErrorEnabled(true);
            user_edit_name.setError(getText(R.string.user_edit_name_error));
            result = true;
        }
        if (user_edit_surname_tv.getText().toString().isEmpty()) {
            user_edit_surname.setErrorEnabled(true);
            user_edit_surname.setError(getText(R.string.user_edit_surname_error));
            result = true;
        }
        if (user_edit_city_tv.getText().toString().isEmpty()) {
            user_edit_city.setErrorEnabled(true);
            user_edit_city.setError(getText(R.string.user_edit_city_error));
            result = true;
        }

        return result;
    }

    public void userEditBirthdateClick(View view) {
        if (birthDatePicked == null) birthDatePicked = Calendar.getInstance();
        int yy = birthDatePicked.get(Calendar.YEAR);
        int mm = birthDatePicked.get(Calendar.MONTH);
        int dd = birthDatePicked.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this, (view1, year, month, dayOfMonth) -> {
            birthDatePicked.set(year, month, dayOfMonth);
            birthDateTV.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
        }, yy, mm, dd);
        dialog.show();
    }
}
