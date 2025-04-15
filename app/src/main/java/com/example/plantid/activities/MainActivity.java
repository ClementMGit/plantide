package com.example.plantid.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.plantid.db.AppDatabase;
import com.example.plantid.R;
import com.example.plantid.db.DatabaseBuilder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private Uri photoUri;

    private final ActivityResultLauncher<Intent> takePictureLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    // Lancer la deuxième activité en passant l'URI
                    Intent intent = new Intent(MainActivity.this, ScanActivity.class);
                    intent.putExtra("photoUri", photoUri.toString());
                    startActivity(intent);
                }
            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton takePhotoButton = findViewById(R.id.take_photo);
        takePhotoButton.setOnClickListener(v -> openCamera());
        // Init db
//        ExecutorService executor = Executors.newSingleThreadExecutor();
//        executor.execute(() -> {
//            DatabaseBuilder.buildDatabaseFromCsv(this);
//        });

    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "IMG_" + System.currentTimeMillis());
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/PlantID");

            photoUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

            takePictureLauncher.launch(takePictureIntent);
        }
    }
}
