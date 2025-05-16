package com.example.plantid.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import com.example.plantid.R;
import com.example.plantid.activities.DetailsActivity;
import com.example.plantid.db.AppDatabase;
import com.example.plantid.db.entities.IdentificationWithServices;

import java.util.List;

public class IdentificationAdapter extends RecyclerView.Adapter<IdentificationAdapter.ViewHolder> {
    private final List<IdentificationWithServices> identifications;
    private final AppDatabase db;

    public IdentificationAdapter(List<IdentificationWithServices> identifications,  Activity activity) {
        this.identifications = identifications;
        this.db = AppDatabase.getDatabase(activity);
    }

    @NonNull
    @Override
    public IdentificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_identification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IdentificationAdapter.ViewHolder holder, int position) {
        IdentificationWithServices item = identifications.get(position);
        Context context = holder.itemView.getContext();

        String nomEspece = item.services.isEmpty() ? "Inconnue" : item.services.get(0).nomEspece;
        String note = item.identification.notesPersonnelles != null ? item.identification.notesPersonnelles : "";
        String date = item.identification.date != null ? formatDateToFrench(item.identification.date) : "Date inconnue";

        holder.nomPlante.setText(nomEspece);
        holder.notes.setText(note);
        holder.date.setText(date);

        if (item.identification.imageUris != null && !item.identification.imageUris.isEmpty()) {
            Uri imageUri = item.identification.imageUris.get(0);
            holder.image.setImageURI(imageUri);
        } else {
            holder.image.setImageResource(R.drawable.ic_launcher_foreground); // Image par défaut
        }

        // Clic sur l'élément pour ouvrir DetailsActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailsActivity.class);
            // Passer les données nécessaires via l'Intent
            intent.putExtra("espece", nomEspece);
            intent.putExtra("notes", item.identification.notesPersonnelles);
            intent.putExtra("identificationId", item.identification.id);
            // Convertir les URIs en ArrayList<String> pour les envoyer
            ArrayList<String> uriStrings = new ArrayList<>();
            for (Uri uri : item.identification.imageUris) {
                uriStrings.add(uri.toString());
            }
            intent.putStringArrayListExtra("imageUris", uriStrings);

            // Démarrer l'activité DetailsActivity
            context.startActivity(intent);
        });


        // Action pour le bouton de suppression
        holder.supprimerButton.setOnClickListener(v -> {
            // Créer un AlertDialog de confirmation
            new android.app.AlertDialog.Builder(context)
                    .setTitle("Confirmation")
                    .setMessage("Êtes-vous sûr de vouloir supprimer cet élément ?")
                    .setPositiveButton("Oui", (dialog, which) -> {
                        // Si l'utilisateur confirme, supprimer l'élément de la base de données
                        new Thread(() -> {
                            db.identificationDao().delete(item.identification); // Supprimer l'identification
                            // Suppression de l'élément de la liste et notification de l'adaptateur
                            identifications.remove(position);
                            ((Activity) context).runOnUiThread(() -> notifyItemRemoved(position));
                        }).start();
                    })
                    .setNegativeButton("Non", null) // Annuler la suppression si l'utilisateur clique sur "Non"
                    .show();
        });

    }

    @Override
    public int getItemCount() {
        return identifications.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView nomPlante, notes, date;
        ImageButton supprimerButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imageViewPlante);
            nomPlante = itemView.findViewById(R.id.nomPlanteTextView);
            notes = itemView.findViewById(R.id.notesTextView);
            date = itemView.findViewById(R.id.dateTextView);
            supprimerButton = itemView.findViewById(R.id.supprimerButton);
        }
    }

    private String formatDateToFrench(String rawDate) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US); // format de base
            Date date = inputFormat.parse(rawDate);
            SimpleDateFormat outputFormat = new SimpleDateFormat("d MMMM yyyy", Locale.FRENCH);
            return outputFormat.format(date);
        } catch (ParseException e) {
            return rawDate; // fallback si erreur de parsing
        }
    }
}
