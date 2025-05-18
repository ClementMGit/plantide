package com.example.plantid.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
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
                        Uri originalUri = (data == null || data.getData() == null) ? photoUri : data.getData();

                        // On copie immédiatement le fichier dans un répertoire privé
                        Uri safeUri = copyUriToInternalStorage(originalUri);

                        if (activity instanceof ScanActivity) {
                            if (onImagePicked != null) {
                                onImagePicked.accept(safeUri);
                                onImagePicked = null;
                            }
                        } else {
                            Intent intent = new Intent(activity, ScanActivity.class);
                            intent.putExtra("photoUri", safeUri.toString());
                            activity.startActivity(intent);
                        }
                    }
                });
    }

    public void pickImage(Consumer<Uri> onImagePicked) {
        this.onImagePicked = onImagePicked;
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
                File photoFile = File.createTempFile("IMG_" + System.currentTimeMillis(), ".jpg", activity.getExternalCacheDir());
                photoUri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".provider", photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                cameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } catch (IOException e) {
                Log.e("ImagePicker", "Camera file creation error: " + e.getMessage());
                return;
            }
        }

        // Chooser
        Intent chooser = Intent.createChooser(galleryIntent, "Choisir une image ou prendre une photo");
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{cameraIntent});
        takePictureLauncher.launch(chooser);
    }

    /**
     * Copie l'image vers un fichier interne de l'application
     */
    private Uri copyUriToInternalStorage(Uri sourceUri) {
        try {
            InputStream inputStream = activity.getContentResolver().openInputStream(sourceUri);
            if (inputStream == null) return null;

            File internalDir = activity.getFilesDir(); // répertoire privé persistant
            File outputFile = new File(internalDir, "image_" + System.currentTimeMillis() + ".jpg");

            FileOutputStream outputStream = new FileOutputStream(outputFile);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            inputStream.close();
            outputStream.close();

            return Uri.fromFile(outputFile);
        } catch (IOException e) {
            Log.e("ImagePicker", "Erreur lors de la copie : " + e.getMessage());
            return null;
        }
    }
    public File uriToFile(Uri uri) {
        try {
            InputStream inputStream = activity.getContentResolver().openInputStream(uri);
            if (inputStream == null) return null;

            // Crée un fichier temporaire dans le cache
            File tempFile = File.createTempFile("image", ".jpg", activity.getCacheDir());
            tempFile.deleteOnExit();

            FileOutputStream outputStream = new FileOutputStream(tempFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }


            outputStream.close();
            inputStream.close();

            return tempFile;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
