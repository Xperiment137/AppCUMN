package com.example.pruebas;

        import android.content.SharedPreferences;
        import androidx.appcompat.app.AppCompatActivity;
        import androidx.appcompat.widget.Toolbar;
        import android.os.Bundle;
        import android.preference.PreferenceManager;
        import android.widget.ArrayAdapter;
        import android.widget.ListView;
        import java.util.ArrayList;


public class listStat extends AppCompatActivity {

    private ListView listView;
    private ArrayList<String> list,words;
    private ArrayAdapter adapter;







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listview);

        // assigning ID of the toolbar to a variable
        Toolbar toolbar = findViewById(R.id.toolbar);

        // using toolbar as ActionBar
        setSupportActionBar(toolbar);

        list = new ArrayList<>();
        listView = findViewById(R.id.list_view);
        listView.setChoiceMode(listView.CHOICE_MODE_NONE);
        adapter = new ArrayAdapter(listStat.this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);

    }
}
