package com.example.pruebas;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ListPeople extends AppCompatActivity {


    ListView listView;
    ArrayList<String> list;
    ArrayList <String> plat,pers,grup;
    ArrayAdapter adapter;
    private Button add,add2;
    private FirebaseFirestore Db;
    private EditText editText;
    private  int cuenta = 0;
    String textoPersonas = "", Lider = "";
    String name = "";



    private String LoadEmail( String email) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String savedValue = sharedPreferences.getString(email,"");
        return savedValue;
    }

    private String LoadName() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String savedValue = sharedPreferences.getString("key","");
        return savedValue;
    }


    private void Escribir(String Persona) {
        if(textoPersonas == "") {

            textoPersonas = Persona + ",";

        }else {
            textoPersonas = textoPersonas + Persona + ",";

        }

    }

    private void Borrar(String pl) {
        String nuevo = "";
        String[] parts = textoPersonas.split(",");
        for (int i = 0; i < parts.length; i++) {
            if (parts[i]!=""){
                Log.e("Partes", parts[i]);
                if (!pl.equals(parts[i])) {

                    if(nuevo == "") {

                        nuevo = parts[i] + ",";

                    }else {
                        nuevo = nuevo + parts[i] + ",";
                    }
                }
            }
        }
        textoPersonas = nuevo;

    }

    /*public void  writeToFile (String data){
        String savedValue = LoadName();
        String fileName = savedValue + ".txt";
        String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Grupos/";
        String pathfin = rootPath +  savedValue + ".txt";

        File newDir = new File(pathfin);
        try{
            if (!newDir.exists()) {
                Log.e("TAG", "Ya existe: " + fileName);
            }
            FileOutputStream writer = new FileOutputStream(new File(rootPath,savedValue + ".txt"));
            writer.write(data.getBytes());
            writer.close();
            Log.e("TAG", "Wrote to file: " + fileName);
            addGroupDataToFirestore();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    private void GetLeader() {
        Db.collection("Usuarios").whereEqualTo("Email", LoadEmail("email"))
              .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("good", document.getId() + " => " + document.getData());
                                Lider = document.getString("Nombre");

                            }
                        } else {
                            Log.d("error", "Error getting documents: ", task.getException());

                        }
                    }
                });
    }

    private void addGroupDataToFirestore() {

        Map<String, Object> group = new HashMap<>();
        group.put("Participantes", pers);
        group.put("Plantas", plat);
        group.put("Lider", Lider);

        // creating a collection reference
        // for our Firebase Firestore database.
        CollectionReference grupos = Db.collection("Grupos");


        // below method is use to add data to Firebase Firestore.
        grupos.document(name).set(group).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void Avoid) {
                // after the data addition is successful
                // we are displaying a success toast message.
                Toast.makeText(ListPeople.this, "You create a new group!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ListPeople.this, MoveMenu.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // this method is called when the data addition process is failed.
                // displaying a toast message when data addition is failed.
                Toast.makeText(ListPeople.this, "Fail to add Group \n" + e, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addGroupDataToUsers() {
        grup = new ArrayList<String>();
        grup.add(name);
        Map<String, Object> group = new HashMap<>();
        group.put("Grupos", grup);

        // creating a collection reference
        // for our Firebase Firestore database.
        CollectionReference grupos = Db.collection("Usuarios");

        // añadir los grupos a cada participante
        pers.add(Lider);
        for(int i = 0; i < pers.toArray().length;i++) {
            grupos.document(pers.toArray()[i].toString()).set(group).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void Avoid) {
                    // after the data addition is successful
                    // we are displaying a success toast message.
                    Toast.makeText(ListPeople.this, "You create a new group!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ListPeople.this, MoveMenu.class));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // this method is called when the data addition process is failed.
                    // displaying a toast message when data addition is failed.
                    Toast.makeText(ListPeople.this, "Fail to add Group \n" + e, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

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
                        Escribir(user);

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
        add2 = findViewById(R.id.add2);
        editText = findViewById(R.id.edit_text);
        listView = findViewById(R.id.list_view);
        list = new ArrayList<>();
        name = LoadName();
        listView.setChoiceMode(listView.CHOICE_MODE_MULTIPLE);
        adapter = new ArrayAdapter(ListPeople.this,android.R.layout.simple_list_item_checked,list);
        listView.setAdapter(adapter);
        GetLeader();
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String item = editText.getText().toString();

                if (!item.isEmpty()) {

                        if(!list.contains(item)) {
                            ExistUser(item);
                            editText.setText("");
                        }else{
                            Toast.makeText(ListPeople.this, "No existe un usuario con ese nombre", Toast.LENGTH_SHORT).show();
                        }

                }
            }
        });

        add2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              if(textoPersonas != ""){
                  int aux = 0;
                  String [] plantas,personas;
                  String [] words = new String [2];
                  String savedValue = LoadName();
                  String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Grupos/";

                  StringBuilder text = new StringBuilder();
                  try {
                      File file = new File(rootPath,savedValue + ".txt");
                      BufferedReader br = new BufferedReader(new FileReader(file));
                      String line;
                      while ((line = br.readLine()) != null) {

                          words[aux++] = line;
                          Log.e("si", line);
                      }
                      br.close() ;
                  }catch (IOException e) {
                      e.printStackTrace();
                  }

                  plantas =  words[0].split(",");
                  personas =  textoPersonas.split(",");

                  plat = new ArrayList<String>(Arrays.asList(plantas));
                  pers = new ArrayList<String>(Arrays.asList(personas));
                  addGroupDataToFirestore();
                  addGroupDataToUsers();
              }

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                if(!listView.isItemChecked(i)){

                    Toast.makeText(ListPeople.this, "Selected -> " + list.toArray()[i].toString(), Toast.LENGTH_SHORT).show();
                    Borrar(list.toArray()[i].toString());
                    list.remove(list.toArray()[i]);


                }


            }
        });
    }

}
