package com.example.plantid.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
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
    private Long identificationId = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // Initialisation des vues
        TextView espece = findViewById(R.id.espece_name);
        saveButton = findViewById(R.id.save_btn);
        notesEditText = findViewById(R.id.notes); // Assurez-vous que "notes" est bien l'id de l'EditText
        String notes_extra = getIntent().getStringExtra("notes");
        if(notes_extra!=null){
            notesEditText.setText(notes_extra);
        }
        identificationId = getIntent().hasExtra("identificationId") ?
                getIntent().getLongExtra("identificationId", -1) : null;
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
                    TextView label = itemView.findViewById(R.id.slider_label);
                    TextView valueText = itemView.findViewById(R.id.slider_value);
                    String service = ps.getNomService();
                    //amélioration possible, mettre les textes francais en bd
                    String serviceTxt = "";
                    if(service.equals("nitrogen_provision")){
                        serviceTxt = "Fixation de l'azote";
                    }else if(service.equals("soil_structuration")){
                        serviceTxt = "Amélioration de la structure du sol";
                    }else if(service.equals("storage_and_return_water")){
                        serviceTxt = "Capacité à retenir l'eau dans le sol";
                    }
                    label.setText(serviceTxt);
                    float qualite = ps.getQualite();
                    slider.setValue(qualite);
                    System.out.println(qualite);
                    int couleur;

                    if (qualite <= 0.25) {
                        couleur = getResources().getColor(R.color.slider_colorlow);
                    } else if (qualite >= 0.75) {
                        couleur = getResources().getColor(R.color.slider_colormhigh);
                    } else {
                        couleur = getResources().getColor(R.color.slider_colormid);
                    }

                    slider.setThumbTintList(ColorStateList.valueOf(couleur)); // Change la couleur principale du thumb
                    valueText.setText(String.format(Locale.getDefault(), "%.2f", qualite));
                    // Ajouter la vue au container
                    container.addView(itemView);
                }
            });
        }).start();

        // Ajouter un OnClickListener sur le bouton de sauvegarde
        saveButton.setOnClickListener(view -> {
            String notes = notesEditText.getText().toString();

            new Thread(() -> {
                if (identificationId != null && identificationId != -1) {
                    // Cas : mise à jour d'une identification existante
                    Identification existing = db.identificationDao().getById(identificationId);
                    if (existing != null) {
                        existing.notesPersonnelles = notes;
                        db.identificationDao().update(existing);
                    }
                } else {
                    // Cas : nouvelle identification
                    Identification identification = new Identification();
                    identification.date = getCurrentDate();
                    identification.notesPersonnelles = notes;
                    identification.imageUris = uris;

                    long newId = db.identificationDao().insert(identification);

                    for (ProposeService service : services) {
                        IdentificationService identificationService = new IdentificationService();
                        identificationService.identificationId = newId;
                        identificationService.nomEspece = nomEspece;
                        identificationService.nomService = service.getNomService();
                        identificationService.qualite = service.getQualite();

                        db.identificationServiceDao().insert(identificationService);
                    }
                }

                runOnUiThread(() -> {
                    Toast.makeText(this, "Identification enregistrée avec succès !", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(DetailsActivity.this, HistoryActivity.class));
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

