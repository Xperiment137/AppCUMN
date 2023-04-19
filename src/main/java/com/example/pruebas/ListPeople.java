package com.example.pruebas;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;

public class ListPeople extends AppCompatActivity {


    ListView listView;
    ArrayList<String> list;
    ArrayAdapter adapter;
    private Button add;
    private FirebaseFirestore Db;
    private EditText editText;


    private void ExistUser(String user){
        DocumentReference docRef = Db.collection("Usuarios").document(user);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        list.add(user);
                        listView.setItemChecked(list.indexOf(user),true);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(ListPeople.this, "Añadido -> " + user, Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(ListPeople.this, "No existe ese usuario", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(ListPeople.this, "Algo fallo, vuelva a intentarlo", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people);
        Db = FirebaseFirestore.getInstance();
        add = findViewById(R.id.add);
        editText = findViewById(R.id.edit_text);
        listView = findViewById(R.id.list_view);
        list = new ArrayList<>();

        listView.setChoiceMode(listView.CHOICE_MODE_MULTIPLE);
        adapter = new ArrayAdapter(ListPeople.this,android.R.layout.simple_list_item_checked,list);
        listView.setAdapter(adapter);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String item = editText.getText().toString();


                if (!item.isEmpty()) {

                    ExistUser(item);
                }

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                if(!listView.isItemChecked(i)){

                    Toast.makeText(ListPeople.this, "Borrando....", Toast.LENGTH_SHORT).show();
                    list.remove(list.toArray()[i]);

                }


            }
        });
    }

}
