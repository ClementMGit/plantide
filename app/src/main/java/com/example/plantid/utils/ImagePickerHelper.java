package com.example.plantid.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.plantid.activities.ScanActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

public class ImagePickerHelper {
    private Consumer<Uri> onImagePicked;
    private AppCompatActivity activity;
    private Uri photoUri;
    private ActivityResultLauncher<Intent> takePictureLauncher;

    public ImagePickerHelper(AppCompatActivity activity) {
        this.activity = activity;
        this.takePictureLauncher = activity.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Uri imageUri;

                        if (data == null || data.getData() == null) {
                            // Cas de la caméra : on utilise photoUri qu'on a préparé avant
                            imageUri = photoUri;
                        } else {
                            // Cas de la galerie
                            imageUri = data.getData();
                        }

                        if (activity instanceof ScanActivity) {
                            if (onImagePicked != null) {
                                onImagePicked.accept(imageUri); // On utilise directement l'URI de la galerie ou de la photo
                                onImagePicked = null;
                            }
                        } else {
                            Intent intent = new Intent(activity, ScanActivity.class);
                            intent.putExtra("photoUri", imageUri.toString());
                            activity.startActivity(intent);
                        }

                    }
                });
    }

    public void pickImage(Consumer<Uri> onImagePicked) {
        this.onImagePicked = onImagePicked;
        checkPermissions();
        openImageChooser();
    }

    public void openImageChooser() {
        // Intent galerie
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");

        // Intent caméra
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(activity.getPackageManager()) != null) {
            try {
                // Fichier temporaire pour la photo prise avec la caméra
                File photoFile = File.createTempFile(
                        "IMG_" + System.currentTimeMillis(),
                        ".jpg",
                        activity.getExternalCacheDir()
                );
                photoUri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".provider", photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                cameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } catch (IOException e) {
                Log.e("ERROR", e.getMessage());
                return;
            }
        }

        // Chooser
        Intent chooser = Intent.createChooser(galleryIntent, "Choisir une image ou prendre une photo");
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{cameraIntent});

        takePictureLauncher.launch(chooser);
    }

    // Demander des permissions spécifiques à Android 14+ si nécessaire
    public void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13 (API 33) et plus
            if (activity.checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES}, 100);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // Android 10 (API 29) et 11 (API 30)
            if (activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
            }
        }
    }
    public File uriToFile(Uri uri) {
        File file = null;
        InputStream inputStream = null;
        FileOutputStream outputStream = null;

        try {
            // Si l'URI provient de la galerie ou de l'appareil photo
            inputStream = activity.getContentResolver().openInputStream(uri);

            // Créer un fichier dans le répertoire Pictures
            File pictureFile = new File(activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "image_" + System.currentTimeMillis() + ".jpg");
            outputStream = new FileOutputStream(pictureFile);

            // Lire les données de l'URI et les écrire dans le fichier
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.flush();
            file = pictureFile;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return file;
    }

    // Enregistrer l'image dans le répertoire Pictures
    public File saveImageToPictures(Uri imageUri) {
        File outputFile = null;
        try {
            // Créer un fichier dans le répertoire public "Pictures"
            File picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            if (!picturesDir.exists()) {
                picturesDir.mkdirs();
            }

            // Crée un fichier avec un nom unique
            outputFile = new File(picturesDir, "IMG_" + System.currentTimeMillis() + ".jpg");

            // Copier le contenu de l'image vers le fichier
            InputStream inputStream = activity.getContentResolver().openInputStream(imageUri);
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, len);
            }

            outputStream.flush();
            outputStream.close();
            inputStream.close();

        } catch (IOException e) {
            Log.e("ImagePicker", "Error saving image: " + e.getMessage());
        }

        return outputFile;
    }
}
