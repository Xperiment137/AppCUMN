package com.example.pruebas;

        import android.content.SharedPreferences;
        import androidx.appcompat.app.AppCompatActivity;
        import androidx.appcompat.widget.Toolbar;
        import android.os.Bundle;
        import android.preference.PreferenceManager;
        import android.util.Log;
        import android.widget.ArrayAdapter;
        import android.widget.ListView;

        import com.google.android.gms.tasks.OnCompleteListener;
        import com.google.android.gms.tasks.Task;
        import com.google.firebase.firestore.FirebaseFirestore;
        import com.google.firebase.firestore.QueryDocumentSnapshot;
        import com.google.firebase.firestore.QuerySnapshot;

        import org.checkerframework.checker.nullness.qual.NonNull;

        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.Map;


public class listStat extends AppCompatActivity {

    private ListView listView;
    private ArrayList<String> list;
    private ArrayAdapter adapter;
    private Map<String, Integer> stats = new HashMap<>();
    private FirebaseFirestore Db;


    private String LoadEmail( String email) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String savedValue = sharedPreferences.getString(email,"");
        return savedValue;
    }

    private void AddToList(){

        for (Map.Entry<String, Integer> pair : stats.entrySet()) {
           list.add(String.format("Especie: %s  veces vista  %s", pair.getKey(), pair.getValue()));
        }
        listView.setChoiceMode(listView.CHOICE_MODE_NONE);
        adapter = new ArrayAdapter(listStat.this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);
    }

    private void GetStats() {
        Db.collection("Usuarios").whereEqualTo("Email", LoadEmail("email"))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("good", document.getId() + " => " + document.getData());
                                stats = (Map<String,Integer>)document.get("Stats");
                                Log.e("stats",stats.toString());
                                if(stats!=null) {
                                    if(!stats.isEmpty()) {

                                        AddToList();

                                    }
                                }
                            }
                        } else {
                            Log.d("error", "Error getting documents: ", task.getException());

                        }
                    }
                });
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stat);

        // assigning ID of the toolbar to a variable
        Toolbar toolbar = findViewById(R.id.toolbar);

        // using toolbar as ActionBar
        setSupportActionBar(toolbar);
        Db = FirebaseFirestore.getInstance();
        list = new ArrayList<>();
        listView = findViewById(R.id.list_view);
        GetStats();

    }
}
