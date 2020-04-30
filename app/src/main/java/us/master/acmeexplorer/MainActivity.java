package us.master.acmeexplorer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import us.master.acmeexplorer.adapter.LinkAdapter;
import us.master.acmeexplorer.entity.Link;
import us.master.acmeexplorer.entity.User;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private TextView greetingsTextView;
    private User user;
    public static String USER_PRINCIPAL = "userPrincipal";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.simpleListView);
        greetingsTextView = findViewById(R.id.greetings_user);

        user = (User) getIntent().getSerializableExtra(USER_PRINCIPAL);

        String greetingsMessage = "Hola, usuario";
        if(user != null && user.getName() != null && !user.getName().equals("")) {
            greetingsMessage = String.format(getString(R.string.greetings_user), user.getName());
        } else if (user != null) {
            greetingsMessage = String.format(getString(R.string.greetings_user), user.getEmail());
        }
        greetingsTextView.setText(greetingsMessage);

        LinkAdapter linkAdapter = new LinkAdapter(this, Link.generateLinks());
        listView.setAdapter(linkAdapter);
    }
}
