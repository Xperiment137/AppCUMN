package com.example.pruebas;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class Camara  extends AppCompatActivity {

    ActivityResultLauncher activityResultLauncher;
    Button bflor,bcorteza,bhoja,bfruto,bsalir;
    TextView texto;
    ImageView visor;
    Uri uri;

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


    private void uploadFile(Uri uri,String Organ) {

        File file = new File(uri.getPath());

        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

// MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body = MultipartBody.Part.createFormData("images", file.getName(), requestFile);

// add another part within the multipart request
        RequestBody organs = RequestBody.create(MediaType.parse("multipart/form-data"), Organ);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://my-api.plantnet.org/")
                .build();

        Galeria.Api service = retrofit.create(Galeria.Api.class);
        Call<ResponseBody> call = service.updateProfile(body, organs);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    String responseBody = null;
                    try {
                        responseBody = response.body().string();
                        JSONObject json = new JSONObject(responseBody);
                        JSONArray pages = json.getJSONArray("results");
                        String best = json.getString("bestMatch");
                        Log.e("obj", pages.getJSONObject(0).getJSONObject("species").getJSONArray("commonNames").getString(0));
                        String comun = pages.getJSONObject(0).getJSONObject("species").getJSONArray("commonNames").getString(0);
                        Log.e("good", comun);
                        Toast.makeText(Camara.this, comun, Toast.LENGTH_SHORT).show();
                        //String best = jsonObject.getString("bestMatch");
                        //JSONArray comoon = jsonObject.getJSONArray("commonNames");
                        bhoja.setVisibility(View.GONE);
                        bfruto.setVisibility(View.GONE);
                        bflor.setVisibility(View.GONE);
                        bcorteza.setVisibility(View.GONE);
                        texto.setText("Nombre científico: " + best + "\n" + "Nombre común: " + comun);
                        visor.setImageURI(uri);
                        texto.setVisibility(View.VISIBLE);
                        visor.setVisibility(View.VISIBLE);
                        bsalir.setVisibility(View.VISIBLE);
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(Camara.this, response.errorBody().toString(), Toast.LENGTH_SHORT).show();
                    try {
                        Log.e("bad",   response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onFailure (Call < ResponseBody > call, Throwable t){
                Toast.makeText(Camara.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("si", t.getMessage());
            }



        });
    }

    public static File savebitmap(Bitmap bmp) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                + File.separator + "testimage.jpg");
        f.createNewFile();
        FileOutputStream fo = new FileOutputStream(f);
        fo.write(bytes.toByteArray());
        fo.close();
        return f;
    }




    public void onBackPressed() {
        startActivity(new Intent(Camara.this, MoveMenu.class));
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camara);
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

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Bundle bundle = result.getData().getExtras();
                    Bitmap bitmap = (Bitmap) bundle.get("data");
                    try {
                       uri = Uri.parse(Uri.fromFile(savebitmap(bitmap)).getPath());
                       Log.e("Uri",uri.getPath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    visor.setImageBitmap(bitmap);
                    texto.setVisibility(View.VISIBLE);
                    bhoja.setVisibility(View.VISIBLE);
                    bfruto.setVisibility(View.VISIBLE);
                    bflor.setVisibility(View.VISIBLE);
                    bcorteza.setVisibility(View.VISIBLE);


                }
            }
        });
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            activityResultLauncher.launch(intent);
        } else {
            Toast.makeText(Camara.this, "There is no app that support this action",
                    Toast.LENGTH_SHORT).show();
        }


        bflor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile(uri,"flower");

            }
        });
        bfruto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile(uri,"fruit");
            }
        });
        bcorteza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile(uri,"bark");
            }
        });
        bhoja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile(uri,"leaf");
            }
        });
        bsalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Camara.this, MoveMenu.class));
            }
        });

    }
}

