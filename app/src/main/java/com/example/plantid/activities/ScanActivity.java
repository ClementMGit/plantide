package com.example.plantid.activities;

import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.plantid.R;
import com.example.plantid.db.AppDatabase;
import com.example.plantid.utils.ImagePickerHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ScanActivity extends AppCompatActivity {
    private final String API_KEY = "2b10UgbpDCukpRYKDarORPFFxu";

    private ImageView big_image_view;
    private MaterialButton identify_btn;
    private ProgressBar progressBar;
    private int current_photo_index;
    private Uri[] uris ;
    private ImagePickerHelper pickerHelper;
    private MaterialCardView last_cv;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        progressBar = findViewById(R.id.progressBar);
        big_image_view = findViewById(R.id.big_image_view);
        identify_btn = findViewById(R.id.identify_btn);
        pickerHelper = new ImagePickerHelper(this);
        current_photo_index = 0;
        uris = new Uri[5];
        // Récupérer l'URI de la photo passée depuis l'intent
        String uriString = getIntent().getStringExtra("photoUri");
        Uri photoUri = null;
        if (uriString != null) {
            big_image_view.setTag("hihi");
            photoUri = Uri.parse(uriString);
            big_image_view.setImageURI(photoUri);
            uris[current_photo_index] = photoUri;
        }
        LinearLayout container = findViewById(R.id.take_btns);
        LayoutInflater inflater = LayoutInflater.from(this);

        for (int i = 0; i < 5; i++) {
            View itemView = inflater.inflate(R.layout.photo_btn, container, false);
            MaterialCardView cardView = itemView.findViewById(R.id.photo_cardview);

            ImageView imageView = itemView.findViewById(R.id.photo_image);
            imageView.setTag(i);
            if(i==0){
                imageView.setImageURI(photoUri);
                cardView.setStrokeColor(getColor(R.color.white));
                last_cv = cardView;
            }
            imageView.setOnClickListener(v -> {
                int index = (int) v.getTag();
                if(uris[index] == null){
                    pickerHelper.pickImage(uri -> {
                        uris[index] = uri;
                        ((ImageView) v).setImageURI(uri); // met à jour la petite image
                        big_image_view.setImageURI(uri);
                    });
                }else{
                    big_image_view.setImageURI(uris[index]);

                }
                last_cv.setStrokeColor(getColor(R.color.black));
                cardView.setStrokeColor(getColor(R.color.white));
                last_cv = cardView;
            });
            container.addView(itemView);
        }
        identify_btn.setOnClickListener(view -> {
            List<File> imageFiles = new ArrayList<>();

            for (Uri uri : uris) {
                if (uri != null) {
                    File imageFile = pickerHelper.uriToFile(uri);
                    if (imageFile != null && imageFile.exists()) {
                        imageFiles.add(imageFile);
                    }
                }
            }
            if (!imageFiles.isEmpty()) {
                progressBar.setVisibility(View.VISIBLE);
                identifyPlantWithImages(imageFiles);
            } else {
                Toast.makeText(this, "Aucune image valide sélectionnée", Toast.LENGTH_SHORT).show();
            }
        });

        // AppDatabase db = AppDatabase.getDatabase(this);
    }


    private void identifyPlantWithImages(List<File> imageFiles) {
        new Thread(() -> {
            try {
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .writeTimeout(40, TimeUnit.SECONDS)
                        .readTimeout(20, TimeUnit.SECONDS)
                        .build();

                MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM);

                for (File imageFile : imageFiles) {
                    RequestBody imagePart = RequestBody.create(imageFile, MediaType.parse("image/jpeg"));
                    multipartBuilder.addFormDataPart("images", imageFile.getName(), imagePart);
                    multipartBuilder.addFormDataPart("organs", "auto"); // ← ajouter un organe par image
                }


                MultipartBody requestBody = multipartBuilder.build();

                HttpUrl url = HttpUrl.parse("https://my-api.plantnet.org/v2/identify/all").newBuilder()
                        .addQueryParameter("include-related-images", "false")
                        .addQueryParameter("no-reject", "false")
                        .addQueryParameter("nb-results", "1")
                        .addQueryParameter("lang", "en")
                        .addQueryParameter("api-key", API_KEY)
                        .build();

                Request request = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .addHeader("accept", "application/json")
                        .build();

                Response response = client.newCall(request).execute();

                if (response.isSuccessful()) {
                    String jsonData = response.body().string();
                    JsonObject json = JsonParser.parseString(jsonData).getAsJsonObject();
                    JsonArray results = json.getAsJsonArray("results");

                    if (results != null && results.size() > 0) {
                        JsonObject firstResult = results.get(0).getAsJsonObject();
                        JsonObject species = firstResult.getAsJsonObject("species");
                        String scientificName = species.get("scientificNameWithoutAuthor").getAsString();

                        runOnUiThread(() -> {
                            Toast.makeText(this, "Espèce détectée : " + scientificName, Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        });
                    }
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Erreur HTTP : " + response.code(), Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    });
                }

            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Erreur : " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("Erreur", e.getMessage());
                    progressBar.setVisibility(View.GONE);
                });
            }
        }).start();
    }



}

