package com.example.pruebas;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;


public class datalist extends AppCompatActivity {

       private ArrayAdapter adapter;
       private ArrayList<String> list;
       private FirebaseFirestore Db;
       private ListView listView;
       private String tipo = "",result;
       private Intent intent;
       private  Toolbar toolbar;


    private String LoadData( String key) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String savedValue = sharedPreferences.getString(key,"");
        return savedValue;
    }


    private void GetData(String grupo){
        DocumentReference docRef = Db.collection("Grupos").document(grupo);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    list = (ArrayList<String>) document.get(LoadData("datonext"));
                    listView.setChoiceMode(listView.CHOICE_MODE_NONE);
                    adapter = new ArrayAdapter(com.example.pruebas.datalist.this, android.R.layout.simple_list_item_1, list);
                    listView.setAdapter(adapter);

                } else {
                    Toast.makeText(datalist.this, "Algo fallo, ve atras e intentalo de nuevo", Toast.LENGTH_LONG).show();
                }
            }
        });
    }



        protected void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_datalist);
            Db = FirebaseFirestore.getInstance();
            listView = findViewById(R.id.list_view);
            toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            list = new ArrayList<>();
            intent = getIntent();
            toolbar.setTitle(LoadData("datonext"));
            result = intent.getStringExtra("activo");
            GetData(result);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                }
            });


        }
    }