package us.master.acmeexplorer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import us.master.acmeexplorer.adapter.LinkAdapter;
import us.master.acmeexplorer.entity.Link;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.simpleListView);
        LinkAdapter linkAdapter = new LinkAdapter(this, Link.generateLinks());
        listView.setAdapter(linkAdapter);
    }
}
