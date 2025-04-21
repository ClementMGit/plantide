package com.example.plantid.db.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Espece {
    @NonNull
    public String getNom() {
        return nom;
    }

    @PrimaryKey
    @NonNull
    public String nom;

    public Espece(@NonNull String nom) {
        this.nom = nom;
    }
}
