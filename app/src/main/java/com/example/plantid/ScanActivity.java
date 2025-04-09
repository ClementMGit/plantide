package com.example.plantid;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ScanActivity extends AppCompatActivity {
    private Uri photoUri;
    private ImageView imageView;
    private Button captureButton;

    // Déclaration du launcher
    private final ActivityResultLauncher<Intent> takePictureLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                // Traiter le résultat ici
                if (result.getResultCode() == RESULT_OK) {
                    // Photo prise avec succès
                    imageView.setImageURI(photoUri);
                    imageView.setVisibility(View.VISIBLE);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        imageView = findViewById(R.id.imageView);
        captureButton = findViewById(R.id.captureButton);

        captureButton.setOnClickListener(view -> openCamera());
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "IMG_" + System.currentTimeMillis());
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/CameraX");

            // Insérer le fichier dans MediaStore
            photoUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

            takePictureLauncher.launch(takePictureIntent); // Utilisation du launcher pour lancer l'intention
        }
    }
}
