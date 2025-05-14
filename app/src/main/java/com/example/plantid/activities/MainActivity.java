package com.example.plantid.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.plantid.R;
import com.example.plantid.db.AppDatabase;
import com.example.plantid.db.DatabaseBuilder;
import com.example.plantid.db.entities.Identification;
import com.example.plantid.utils.ImagePickerHelper;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageButton takePhotoButton = findViewById(R.id.take_photo);
        ImagePickerHelper helper = new ImagePickerHelper(this);
        takePhotoButton.setOnClickListener(v -> {
            helper.pickImage(null);
        });

        Button myPlantsButton = findViewById(R.id.myPlantsBtn);
        myPlantsButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(intent);
        });

//        new Thread(() -> {
//            AppDatabase db = AppDatabase.getDatabase(MainActivity.this);
//            db.identificationDao().deleteAll();
//            db.identificationDao().deleteAllIdService();
//
//        }).start();

    }
}
