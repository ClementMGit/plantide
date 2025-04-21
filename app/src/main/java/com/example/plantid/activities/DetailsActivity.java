package com.example.plantid.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.plantid.R;

public class DetailsActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        TextView espece = findViewById(R.id.espece_name);
        String nom_espece = getIntent().getStringExtra("espece");

        espece.setText(nom_espece);
    }
}
