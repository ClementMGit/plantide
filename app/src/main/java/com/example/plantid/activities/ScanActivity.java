package com.example.plantid.activities;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.plantid.R;
import com.example.plantid.db.AppDatabase;
import com.google.android.material.button.MaterialButton;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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

    private ImageView imageView;
    private MaterialButton identify_btn;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        progressBar = findViewById(R.id.progressBar);
        imageView = findViewById(R.id.imageView);
        identify_btn = findViewById(R.id.identify_btn);
        // Récupérer l'URI de la photo passée depuis l'intent
        String uriString = getIntent().getStringExtra("photoUri");
        if (uriString != null) {
            Uri photoUri = Uri.parse(uriString);
            imageView.setImageURI(photoUri);
        }
        AppDatabase db = AppDatabase.getDatabase(this);
        identify_btn.setOnClickListener(view -> {
            if (uriString != null) {
                File imageFile = uriToFile(Uri.parse(uriString));
                if (imageFile != null && imageFile.exists()) {
                    progressBar.setVisibility(View.VISIBLE);
                    identifyPlantWithImage(imageFile);
                } else {
                    Toast.makeText(this, "Fichier image introuvable", Toast.LENGTH_SHORT).show();
                }
            }

        });

    }
    public File uriToFile(Uri uri) {
        File file = null;
        try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                if (idx != -1) {
                    String filePath = cursor.getString(idx);
                    file = new File(filePath);
                } else {
                    // Fallback méthode pour Android Q+
                    InputStream inputStream = getContentResolver().openInputStream(uri);
                    File tempFile = File.createTempFile("temp_image", ".jpg", getCacheDir());
                    FileOutputStream out = new FileOutputStream(tempFile);
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = inputStream.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    out.close();
                    inputStream.close();
                    file = tempFile;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    private void identifyPlantWithImage(File imageFile) {
        new Thread(() -> {
            try {
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(20, TimeUnit.SECONDS)
                        .writeTimeout(20, TimeUnit.SECONDS)
                        .readTimeout(20, TimeUnit.SECONDS)
                        .build();
                RequestBody imagePart = RequestBody.create(imageFile, MediaType.parse("image/jpeg"));
                MultipartBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("images", imageFile.getName(), imagePart)
                        .addFormDataPart("organs", "auto")
                        .build();

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

                        runOnUiThread(() ->{
                                Toast.makeText(this, "Espèce détectée : " + scientificName, Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                                }
                        );
                    }
                } else {
                    runOnUiThread(() -> {
                                Toast.makeText(this, "Erreur HTTP : " + response.code(), Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                            }
                    );
                }

            } catch (Exception e) {
                runOnUiThread(() -> {
                            Toast.makeText(this, "Erreur : " + e.getMessage(), Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                );
            }
        }).start();
    }


}

