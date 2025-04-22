package com.example.plantid.activities;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

import com.example.plantid.R;
import com.example.plantid.db.AppDatabase;
import com.example.plantid.db.entities.ProposeService;
import com.example.plantid.utils.SlideShowAdapter;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.slider.Slider;

import java.util.ArrayList;
import java.util.List;

public class DetailsActivity extends Activity {
    private AppDatabase db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        TextView espece = findViewById(R.id.espece_name);
        String nomEspece = getIntent().getStringExtra("espece");
        espece.setText(nomEspece);

        LinearLayout container = findViewById(R.id.sliders);
        LayoutInflater inflater = LayoutInflater.from(this);
        db = AppDatabase.getDatabase(this);
        ArrayList<String> uriStrings = getIntent().getStringArrayListExtra("imageUris");
        List<Uri> uris = new ArrayList<>();

        for (String uriString : uriStrings) {
            uris.add(Uri.parse(uriString)); // Convertir String en Uri
        }
        ViewPager2 viewPager = findViewById(R.id.imageSlider);
        SlideShowAdapter adapter = new SlideShowAdapter(this, uris);
        viewPager.setAdapter(adapter);

        // On récupère les services associés à l'espèce dans un thread (obligatoire hors main thread)
        new Thread(() -> {
            List<ProposeService> services = db.proposeServiceDao().getByEspece(nomEspece);

            runOnUiThread(() -> {
                for (ProposeService ps : services) {
                    // On "gonfle" le layout d’un slider
                    View itemView = inflater.inflate(R.layout.slider, container, false);
                    // Mettre à jour les valeurs des vues
                    Slider slider = itemView.findViewById(R.id.slider);
                    slider.setValue(ps.getQualite());

                    TextView label = itemView.findViewById(R.id.slider_label);
                    label.setText(ps.getService());

                    // Ajout de la vue au container
                    container.addView(itemView);
                }
            });
        }).start();
    }
}
