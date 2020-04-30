package us.master.acmeexplorer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import us.master.acmeexplorer.adapter.LinkAdapter;
import us.master.acmeexplorer.entity.Link;
import us.master.acmeexplorer.entity.User;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private TextView greetingsTextView;
    private User user;
    private Button showProfileButton;
    public static String USER_PRINCIPAL = "userPrincipal";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.simpleListView);
        greetingsTextView = findViewById(R.id.greetings_user);
        showProfileButton = findViewById(R.id.show_profile_button);

        user = getIntent().getParcelableExtra(USER_PRINCIPAL);

        String greetingsMessage = "Hola, usuario";
        if(user != null && user.getName() != null && !user.getName().equals("")) {
            greetingsMessage = String.format(getString(R.string.greetings_user), user.getName());
        } else if (user != null) {
            greetingsMessage = String.format(getString(R.string.greetings_user), user.getEmail());
        }
        greetingsTextView.setText(greetingsMessage);

        LinkAdapter linkAdapter = new LinkAdapter(this, Link.generateLinks());
        listView.setAdapter(linkAdapter);

        showProfileButton.setOnClickListener(v -> redirectUserProfileActivity());
    }

    private void redirectUserProfileActivity() {
        Intent intent = new Intent(this, UserDetailActivity.class);
        intent.putExtra(UserDetailActivity.USER_PRINCIPAL, user);
        startActivity(intent);
    }
}
