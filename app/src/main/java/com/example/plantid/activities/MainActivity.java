package com.example.plantid.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.plantid.R;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private Uri photoUri;

    private final ActivityResultLauncher<Intent> takePictureLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    Uri imageUri;

                    if (data == null || data.getData() == null) {
                        // Cas de la caméra : on utilise photoUri qu'on a préparé avant
                        imageUri = photoUri;
                    } else {
                        // Cas de la galerie
                        imageUri = data.getData();
                    }

                    Intent intent = new Intent(MainActivity.this, ScanActivity.class);
                    intent.putExtra("photoUri", imageUri.toString());
                    startActivity(intent);
                }
            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageButton takePhotoButton = findViewById(R.id.take_photo);
        takePhotoButton.setOnClickListener(v -> {
            checkPermissions(); // Vérification des permissions avant d'ouvrir l'éditeur
            openImageChooser();
        });
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

    private void openImageChooser() {
        // Intent galerie
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");

        // Intent caméra
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            try {
                // Fichier temporaire pour la photo prise avec la caméra
                File photoFile = File.createTempFile(
                        "IMG_" + System.currentTimeMillis(),
                        ".jpg",
                        getExternalCacheDir()
                );
                photoUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                cameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } catch (IOException e) {
                Log.e("ERROR", Objects.requireNonNull(e.getMessage()));
                return;
            }
        }

        // Chooser
        Intent chooser = Intent.createChooser(galleryIntent, "Choisir une image ou prendre une photo");
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{cameraIntent});

        takePictureLauncher.launch(chooser);
    }

    // Demander des permissions spécifiques à Android 14+ si nécessaire
    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13 (API 33) et plus
            if (checkSelfPermission(android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.READ_MEDIA_IMAGES}, 100);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // Android 10 (API 29) et 11 (API 30)
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
            }
        }
    }
}
