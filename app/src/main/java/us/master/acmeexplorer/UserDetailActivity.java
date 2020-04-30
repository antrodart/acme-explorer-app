package us.master.acmeexplorer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import us.master.acmeexplorer.entity.User;

public class UserDetailActivity extends AppCompatActivity {

    public static String USER_PRINCIPAL = "userPrincipal";
    private ImageView profilePhotoIV;
    private TextView nameTV;
    private TextView surnameTV;
    private TextView emailTV;
    private TextView cityTV;
    private TextView birthDateTV;
    private Button editProfileButton;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        profilePhotoIV = findViewById(R.id.user_detail_picture);
        nameTV = findViewById(R.id.user_detail_name);
        surnameTV = findViewById(R.id.user_detail_surname);
        emailTV = findViewById(R.id.user_detail_email);
        cityTV = findViewById(R.id.user_detail_city);
        birthDateTV = findViewById(R.id.user_detail_birthdate);
        editProfileButton = findViewById(R.id.user_detail_edit_button);

        user = getIntent().getParcelableExtra(USER_PRINCIPAL);

        if (user != null) {
            nameTV.setText(user.getName());
            surnameTV.setText(user.getSurname());
            emailTV.setText(user.getEmail());
            cityTV.setText(user.getCity());
            if (user.getBirthDate() != null) {
                birthDateTV.setText(Util.dateFormatSpanish(user.getBirthDate()));
            }

            if (!Util.checkStringEmpty(user.getPicture())) {
                Picasso.get()
                        .load(user.getPicture())
                        .placeholder(R.drawable.default_user_pic)
                        .error(R.drawable.default_user_pic)
                        .into(profilePhotoIV);
            }

            editProfileButton.setOnClickListener(v -> redirectEditUserProfile());
        } else {
            Toast.makeText(UserDetailActivity.this, getText(R.string.error_load_user_profile), Toast.LENGTH_LONG).show();
            editProfileButton.setVisibility(View.GONE);
        }

    }

    private void redirectEditUserProfile() {
        Intent intent = new Intent(this, UserEditActivity.class);
        intent.putExtra(USER_PRINCIPAL, user);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.USER_PRINCIPAL, user);
        startActivity(intent);
    }
}
