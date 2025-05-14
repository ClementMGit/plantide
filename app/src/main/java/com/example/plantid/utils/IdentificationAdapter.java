package com.example.plantid.utils;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;

import com.example.plantid.R;
import com.example.plantid.db.entities.IdentificationWithServices;

import java.util.List;

public class IdentificationAdapter extends RecyclerView.Adapter<IdentificationAdapter.ViewHolder> {

    public interface OnClickListener {
        void onClick(IdentificationWithServices item);
    }

    public interface OnDeleteClickListener {
        void onDelete(IdentificationWithServices item);
    }

    private final List<IdentificationWithServices> identifications;
    private final OnClickListener onClick;
    private final OnDeleteClickListener onDeleteClick;

    public IdentificationAdapter(List<IdentificationWithServices> identifications,
                                 OnClickListener onClick,
                                 OnDeleteClickListener onDeleteClick) {
        this.identifications = identifications;
        this.onClick = onClick;
        this.onDeleteClick = onDeleteClick;
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
        String date = item.identification.date != null ? item.identification.date : "Date inconnue";

        holder.nomPlante.setText(nomEspece);
        holder.notes.setText(note);
        holder.date.setText(date);

        if (item.identification.imageUris != null && !item.identification.imageUris.isEmpty()) {
            Uri imageUri = item.identification.imageUris.get(0);
            holder.image.setImageURI(imageUri);
        } else {
            holder.image.setImageResource(R.drawable.ic_launcher_foreground); // Image par défaut
        }

        holder.itemView.setOnClickListener(v -> onClick.onClick(item));
        holder.supprimerButton.setOnClickListener(v -> onDeleteClick.onDelete(item));
    }

    @Override
    public int getItemCount() {
        return identifications.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView nomPlante, notes, date;
        Button supprimerButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imageViewPlante);
            nomPlante = itemView.findViewById(R.id.nomPlanteTextView);
            notes = itemView.findViewById(R.id.notesTextView);
            date = itemView.findViewById(R.id.dateTextView);
            supprimerButton = itemView.findViewById(R.id.supprimerButton);
        }
    }
}
