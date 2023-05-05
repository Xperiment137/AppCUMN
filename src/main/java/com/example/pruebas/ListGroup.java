package com.example.pruebas;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
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

public class ListGroup extends AppCompatActivity {

    private ArrayAdapter adapter;
    private ArrayList<String> list,list2;
    private MutableLiveData<String> listen,listen2;
    private Button add, add2, add3;
    private FirebaseFirestore Db;
    private AlertDialog.Builder builder;
    private ListView listView;
    private String activo = "";


    private String LoadEmail( String email) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String savedValue = sharedPreferences.getString(email,"");
        return savedValue;
    }
    private void SaveData(String key, String value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    private void GetGrupos() {
        Db.collection("Usuarios").whereEqualTo("Email", LoadEmail("email"))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                list = (ArrayList<String>)document.get("Grupos");
                                if(list!=null) {
                                    if(!list.isEmpty()) {
                                        listView.setChoiceMode(listView.CHOICE_MODE_SINGLE);
                                        adapter = new ArrayAdapter(ListGroup.this, android.R.layout.simple_list_item_single_choice, list);
                                        listView.setAdapter(adapter);
                                        Log.d("good", document.getId() + " => " + list.toString());
                                    }
                                }
                            }
                        } else {
                            Log.d("error", "Error getting documents: ", task.getException());

                        }
                    }
                });
    }

    private String LoadData( String email) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String savedValue = sharedPreferences.getString(email,"");
        return savedValue;
    }

    private void DeleteGroupDb(String name){
        Db.collection("Grupos").document(name)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("si", "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("no", "Error deleting document", e);
                    }
                });

    }


    private void GetData(String grupo){
        DocumentReference docRef = Db.collection("Grupos").document(grupo);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("si", "DocumentSnapshot data: " + document.getData());
                        list2 = (ArrayList<String>) document.get("Participantes");
                        list2.add(document.getString("Lider"));
                        Log.e("act", list2.toString());
                        listen2.setValue("borrar");
                    } else {
                        Log.d("no", "No such document");
                    }
                } else {
                    Log.d("ns", "get failed with ", task.getException());
                }
            }
        });

    }

    private void DeleteGrouofUser(String name) {

        CollectionReference grupos = Db.collection("Usuarios");
        Map<String,Object> updates = new HashMap<>();
        updates.put("Grupos", FieldValue.delete());
        for(int i = 0; i < list2.toArray().length;i++) {
            grupos.document(list2.toArray()[i].toString()).update(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void Avoid) {
                    // after the data addition is successful
                    // we are displaying a success toast message.
                    Toast.makeText(ListGroup.this, "Grupo borrado!", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // this method is called when the data addition process is failed.
                    // displaying a toast message when data addition is failed.
                    Toast.makeText(ListGroup.this, "Fallo al borrar grupo \n" + e, Toast.LENGTH_SHORT).show();
                }
            });
        }
        DeleteGroup(name);
        listen2.setValue("nada");
    }

    private void showMessageOtype() {

        //Setting message manually and performing action on button click
        builder.setMessage("Elige una opción:")
                .setCancelable(false)
                .setPositiveButton("Plantas", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getApplicationContext(),"Plantas",
                                Toast.LENGTH_SHORT).show();
                         SaveData("datonext","Plantas");
                        listen.setValue("Plantas");

                    }
                })
                .setNegativeButton("Participantes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getApplicationContext(),"Participantes",
                                Toast.LENGTH_SHORT).show();
                        SaveData("datonext","Participantes");
                        listen.setValue("Participantes");
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


    private void  DeleteGroup(String nombre){

        DeleteGroupDb(nombre);
        String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Grupos/" + nombre + ".txt";
        File dir = new File(rootPath);
        if (dir.exists())
        {

                dir.delete();

        }

        String auxc = LoadData("select");

        if(nombre.equals(auxc)){
            SaveData("select","");
        }
        list.remove(nombre);

        if(nombre.equals(activo)) {
            activo = "";
        }
       adapter.notifyDataSetChanged();
    }








    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showgroup);
        Db = FirebaseFirestore.getInstance();
        add = findViewById(R.id.add1);
        add2 = findViewById(R.id.add2);
        add3 = findViewById(R.id.add3);
        listView = findViewById(R.id.list_view);
        list = new ArrayList<String>();
        list2 = new ArrayList<String>();
        listen = new MutableLiveData<>();
        listen2 = new MutableLiveData<>();
        builder = new AlertDialog.Builder(this);
        listen.setValue("nada");
        listen2.setValue("nada");
        Toast.makeText(ListGroup.this, "Cargando grupos...", Toast.LENGTH_LONG).show();
        GetGrupos();
        Log.d("good",  list.toString());

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list != null) {
                    if (!list.isEmpty()) {
                        if (activo != "") {
                            SaveData("select", activo);
                            Toast.makeText(getApplicationContext(), "Grupo activo: " + activo, Toast.LENGTH_LONG).show();

                        }
                    }
                }
            }
        });

        add2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list != null) {
                    if (!list.isEmpty()) {
                        if (activo != "") {
                            showMessageOtype();
                        }
                    }
                }
            }
        });

        add3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(list!=null) {
                    if (!list.isEmpty()) {
                        GetData(activo);
                    }
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                if(listView.isItemChecked(i)){

                activo = list.toArray()[i].toString();


                }


            }
        });

        listen.observe(  ListGroup.this,new Observer<String>() {
            @Override
            public void onChanged(String changedValue) {
                if (changedValue.equals("Plantas") || changedValue.equals("Participantes") ) {
                    Intent aux = new Intent(ListGroup.this, datalist.class);
                    Log.e("act",activo);
                    aux.putExtra("activo",activo);
                    startActivity(aux);

                }
            }
        });
        listen2.observe(  ListGroup.this,new Observer<String>() {
            @Override
            public void onChanged(String changedValue) {
                if (changedValue.equals("borrar")) {
                    DeleteGrouofUser(activo);
                }
            }
        });
    }
}
