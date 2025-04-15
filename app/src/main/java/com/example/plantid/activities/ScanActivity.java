package com.example.plantid.activities;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.plantid.R;
import com.example.plantid.db.AppDatabase;

public class ScanActivity extends AppCompatActivity {

    private ImageView imageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        imageView = findViewById(R.id.imageView);

        // Récupérer l'URI de la photo passée depuis l'intent
        String uriString = getIntent().getStringExtra("photoUri");
        if (uriString != null) {
            Uri photoUri = Uri.parse(uriString);
            imageView.setImageURI(photoUri);
        }
        AppDatabase db = AppDatabase.getDatabase(this);

    }
}
