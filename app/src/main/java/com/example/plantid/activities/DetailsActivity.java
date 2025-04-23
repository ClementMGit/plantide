package com.example.plantid.activities;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

import com.example.plantid.R;
import com.example.plantid.db.AppDatabase;
import com.example.plantid.db.entities.Identification;
import com.example.plantid.db.entities.IdentificationService;
import com.example.plantid.db.entities.ProposeService;
import com.example.plantid.utils.SlideShowAdapter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.slider.Slider;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DetailsActivity extends Activity {
    private AppDatabase db;
    private MaterialButton saveButton;
    private EditText notesEditText;
    private String nomEspece;
    private List<Uri> uris;
    private List<ProposeService> services;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // Initialisation des vues
        TextView espece = findViewById(R.id.espece_name);
        saveButton = findViewById(R.id.save_btn);
        notesEditText = findViewById(R.id.notes); // Assurez-vous que "notes" est bien l'id de l'EditText

        nomEspece = getIntent().getStringExtra("espece");
        espece.setText(nomEspece);

        LinearLayout container = findViewById(R.id.sliders);
        LayoutInflater inflater = LayoutInflater.from(this);
        db = AppDatabase.getDatabase(this);

        // Récupérer les URI des images passées via l'Intent
        ArrayList<String> uriStrings = getIntent().getStringArrayListExtra("imageUris");
        uris = new ArrayList<>();
        for (String uriString : uriStrings) {
            uris.add(Uri.parse(uriString)); // Convertir String en Uri
        }

        // Configuration du ViewPager pour l'affichage des images
        ViewPager2 viewPager = findViewById(R.id.imageSlider);
        SlideShowAdapter adapter = new SlideShowAdapter(this, uris);
        viewPager.setAdapter(adapter);

        // Récupération des services associés à l'espèce
        new Thread(() -> {
            services = db.proposeServiceDao().getByEspece(nomEspece);

            runOnUiThread(() -> {
                for (ProposeService ps : services) {
                    // "Gonfler" le layout d'un slider
                    View itemView = inflater.inflate(R.layout.slider, container, false);
                    Slider slider = itemView.findViewById(R.id.slider);
                    slider.setValue(ps.getQualite());

                    TextView label = itemView.findViewById(R.id.slider_label);
                    label.setText(ps.getNomService());

                    // Ajouter la vue au container
                    container.addView(itemView);
                }
            });
        }).start();

        // Ajouter un OnClickListener sur le bouton de sauvegarde
        saveButton.setOnClickListener(view -> {
            // Créer un objet Identification
            String notes = notesEditText.getText().toString();
            Identification identification = new Identification();
            identification.date = getCurrentDate();  // Par exemple, récupère la date actuelle
            identification.notesPersonnelles = notes;
            identification.imageUris = uris;

            // Sauvegarder l'Identification dans la base de données
            new Thread(() -> {
                // Enregistrer l'Identification dans la base de données
                long identificationId = db.identificationDao().insert(identification);

                // Lier cette identification aux services proposés (enregistrer dans IdentificationService)
                for (ProposeService service : services) {
                    IdentificationService identificationService = new IdentificationService();
                    identificationService.identificationId = identificationId; // Associer l'ID de l'Identification
                    identificationService.nomEspece = nomEspece;
                    identificationService.nomService = service.getNomService();
                    identificationService.qualite = service.getQualite();

                    db.identificationServiceDao().insert(identificationService); // Enregistrer dans la table d'association
                }

                runOnUiThread(() -> {
                    // Afficher un message ou effectuer une action après la sauvegarde
                    Toast.makeText(this, "Identification saved successfully!", Toast.LENGTH_SHORT).show();
                    finish(); // Fermer l'activité après la sauvegarde
                });
            }).start();
        });
    }

    // Méthode pour récupérer la date actuelle, à adapter selon tes besoins
    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }
}

