package com.example.pruebas;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import java.io.File;
import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Galeria  extends AppCompatActivity {

    private ActivityResultLauncher activityResultLauncher;
    private Button bflor,bcorteza,bhoja,bfruto,bsalir;
    private TextView texto;
    private ImageView visor;
    private Toolbar toolbar;
    private Uri uri;
    private String activo,comun,textoStats,name;
    private ArrayList<String> list,list2;
    private FirebaseFirestore Db;
    private Map<String, Integer> stats = new HashMap<>();

    private static final String PROJECT = "all"; // try "weurope" or "canada"
    private static final String API_URL = "/v2/identify/" + PROJECT + "?api-key=";
    private static final String API_PRIVATE_KEY = "2b102ii3ydTnBVlQ6gbHTUEBu"; // secret
    private static final String API_LANG = "&lang=es";

    public interface Api {

        @Multipart
        @POST(API_URL + API_PRIVATE_KEY + API_LANG)
        Call<ResponseBody> updateProfile(@Part MultipartBody.Part image,
                                         @Part("organs") RequestBody organs);
    }

    public static String getPath(Context context, Uri uri ) {
        String result = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver( ).query( uri, proj, null, null, null );
        if(cursor != null){
            if ( cursor.moveToFirst( ) ) {
                int column_index = cursor.getColumnIndexOrThrow( proj[0] );
                result = cursor.getString( column_index );
            }
            cursor.close( );
        }
        if(result == null) {
            result = "Not found";
        }
        return result;
    }

    private void uploadFile(Uri uri,String Organ) {

        File file = new File(getPath(getApplicationContext(),uri));
        Log.e("si2",getPath(getApplicationContext(),uri));
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

// MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body = MultipartBody.Part.createFormData("images", file.getName(), requestFile);

// add another part within the multipart request
        RequestBody organs = RequestBody.create(MediaType.parse("multipart/form-data"), Organ);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://my-api.plantnet.org/")
                .build();

        Api service = retrofit.create(Api.class);
        Call<ResponseBody> call = service.updateProfile(body, organs);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    String responseBody = null;
                    try {
                        responseBody = response.body().string();
                        Log.e("response",responseBody);
                        JSONObject json = new JSONObject(responseBody);
                        JSONArray pages = json.getJSONArray("results");
                        String best = json.getString("bestMatch");
                        //("obj", pages.getJSONObject(0).getJSONObject("species").getJSONArray("commonNames").getString(0));
                        if(pages.getJSONObject(0).getJSONObject("species").getJSONArray("commonNames").length() == 0){

                            texto.setText("Nombre científico: " + best + "\n" + "Nombre común: No consta en la BD");
                            comun = null;

                        }else {
                            comun = pages.getJSONObject(0).getJSONObject("species").getJSONArray("commonNames").getString(0);
                            //Log.e("good", comun);
                            Toast.makeText(Galeria.this, comun, Toast.LENGTH_SHORT).show();
                            //String best = jsonObject.getString("bestMatch");
                            //JSONArray comoon = jsonObject.getJSONArray("commonNames");
                            texto.setText("Nombre científico: " + best + "\n" + "Nombre común: " + comun);
                        }
                        bhoja.setVisibility(View.GONE);
                        bfruto.setVisibility(View.GONE);
                        bflor.setVisibility(View.GONE);
                        bcorteza.setVisibility(View.GONE);
                        visor.setImageURI(uri);
                        texto.setVisibility(View.VISIBLE);
                        visor.setVisibility(View.VISIBLE);
                        bsalir.setVisibility(View.VISIBLE);
                        if(comun != null) {
                            int cuenta = IsInStats(comun);
                            if(stats == null){
                                stats = new HashMap<>();
                            }
                            cuenta+=1;
                            stats.put(comun,cuenta);
                            addStatsDataToFirestore();
                            if (activo != "") {
                                //Log.e("listaf",list.toString());
                                // Log.e("lista2f",list2.toString());
                                Log.e("comun", comun);
                                String auc = NormalizarEspecie(comun);
                                if (list2.contains(auc)) {
                                    int pos = list2.indexOf(auc);
                                    list.remove(pos);
                                    Toast.makeText(Galeria.this, "Has descubierto: " + comun + " para tu grupo " + activo, Toast.LENGTH_LONG).show();

                                }
                                DeletePlanta();
                            }
                        }else{

                            int cuenta = IsInStats(best);
                            if(stats == null){
                                stats = new HashMap<>();
                            }
                            cuenta+=1;
                            stats.put(best,cuenta);
                            addStatsDataToFirestore();
                        }
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(Galeria.this, response.errorBody().toString(), Toast.LENGTH_SHORT).show();
                    try {
                        Log.e("bad",   response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
                @Override
                public void onFailure (Call < ResponseBody > call, Throwable t){
                    Toast.makeText(Galeria.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("si", t.getMessage());
                }



        });
    }

    private String LoadEmail( String email) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String savedValue = sharedPreferences.getString(email,"");
        return savedValue;
    }

    private int IsInStats(String ccomun){
        if(stats != null) {
            if (!stats.isEmpty()) {
                if (stats.containsKey(ccomun)) {
                    if(stats.get(ccomun)!= null) {

                      return ((Number)stats.get(ccomun)).intValue();

                    }else {

                        return 0;

                    }

                    }

                }
            }
        return 0;
    }


    private void GetUsername() {
        Db.collection("Usuarios").whereEqualTo("Email", LoadEmail("email"))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("good", document.getId() + " => " + document.getData());
                                name = document.getString("Nombre");
                                stats = (Map<String,Integer>)document.get("Stats");
                                //Log.e("stats",stats.toString());
                            }
                        } else {
                            Log.d("error", "Error getting documents: ", task.getException());

                        }
                    }
                });
    }

    private String  NormalizarEspecie(String aux){

        aux = Normalizer.normalize(aux, Normalizer.Form.NFD).replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        aux = aux.toLowerCase();
        return aux;

    }

    private String LoadData( String grupo) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String savedValue = sharedPreferences.getString(grupo,"");
        return savedValue;
    }

    private void addStatsDataToFirestore() {
        Log.e("stats",stats.toString());
        Map<String, Object> group = new HashMap<>();
        group.put("Stats", stats);


        CollectionReference user = Db.collection("Usuarios");

        // below method is use to add data to Firebase Firestore.
        user.document(name).update(group).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void Avoid) {
                // after the data addition is successful
                // we are displaying a success toast message.
                Log.e("si:", "Succeed");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // this method is called when the data addition process is failed.
                // displaying a toast message when data addition is failed.
                Log.e("no:", "Fail");
            }
        });
    }


    private  void NormalizarLista(){

        for(int i = 0;i<list.size();i++) {
            String aux = NormalizarEspecie(list.get(i));
            list2.add(aux);
        }
        Log.e("lista2:", list2.toString());
    }

    private void GetData(String grupo){
        DocumentReference docRef = Db.collection("Grupos").document(grupo);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.e("lista1",document.getData().toString());
                        list = (ArrayList<String>) document.get("Plantas");
                        NormalizarLista();
                        //Log.e("lista1",list.toString());
                    } else {
                        Log.d("no", "No such document");
                    }
                } else {
                    Log.d("ns", "get failed with ", task.getException());
                }
            }
        });

    }

    private void DeletePlanta() {

        Map<String, Object> group = new HashMap<>();
        group.put("Plantas",list);

        // creating a collection reference
        // for our Firebase Firestore database.
        CollectionReference grupos = Db.collection("Grupos");


        // below method is use to add data to Firebase Firestore.
        grupos.document(activo).update(group).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void Avoid) {
                // after the data addition is successful
                // we are displaying a success toast message.

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // this method is called when the data addition process is failed.
                // displaying a toast message when data addition is failed.
                Toast.makeText(Galeria.this, "Fail to chage specie in DB\n" + e, Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void onBackPressed() {

        startActivity(new Intent(Galeria.this, MoveMenu.class));
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camara);
        Db = FirebaseFirestore.getInstance();
        list = new ArrayList<String>();
        list2 = new ArrayList<String>();
        bflor = findViewById(R.id.flor);
        bcorteza = findViewById(R.id.corteza);
        bhoja = findViewById(R.id.hoja);
        bfruto = findViewById(R.id.fruto);
        bsalir = findViewById(R.id.inicio);
        texto = findViewById(R.id.texto);
        visor = findViewById(R.id.visor);

        bhoja.setVisibility(View.GONE);
        bfruto.setVisibility(View.GONE);
        bflor.setVisibility(View.GONE);
        bcorteza.setVisibility(View.GONE);
        bsalir.setVisibility(View.GONE);
        texto.setVisibility(View.GONE);
        visor.setVisibility(View.GONE);
        GetUsername();
        activo = LoadData("select");
        Log.e("ac",activo);

        Intent intent = new Intent(Intent.ACTION_PICK);
        ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        //here we will handle the result of our intent
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            //image picked
                            //get uri of image
                            Intent data = result.getData();
                            Uri imageUri = data.getData();
                            texto.setVisibility(View.VISIBLE);
                           bhoja.setVisibility(View.VISIBLE);
                           bfruto.setVisibility(View.VISIBLE);
                           bflor.setVisibility(View.VISIBLE);
                           bcorteza.setVisibility(View.VISIBLE);
                            uri = data.getData();
                            Log.e("si",uri.toString());


                        } else {
                            //cancelled
                            Toast.makeText(Galeria.this, "Cancelled...", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        intent.setType("image/*");
        galleryActivityResultLauncher.launch(intent);

        bflor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!activo.equals("")) {
                    GetData(activo); // la lista no se carga IDK
                }
                uploadFile(uri,"flower");

            }
        });
        bfruto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!activo.equals("")) {
                    GetData(activo); // la lista no se carga IDK
                    NormalizarLista();
                }
                uploadFile(uri,"fruit");
            }
        });
        bcorteza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(activo != "") {
                    GetData(activo); // la lista no se carga IDK
                   NormalizarLista();
                }
                uploadFile(uri,"bark");
            }
        });
        bhoja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetData(activo); // la lista no se carga IDK
                NormalizarLista();
                uploadFile(uri,"leaf");
            }
        });
        bsalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Galeria.this, MoveMenu.class));
            }
        });




    }
}

