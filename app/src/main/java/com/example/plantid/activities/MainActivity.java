package com.example.plantid.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.plantid.R;
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



    }
}
