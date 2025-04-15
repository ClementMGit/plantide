package com.example.plantid.db.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Service {
    @PrimaryKey
    @NonNull
    public String nom;

    public Service(@NonNull String nom) {
        this.nom = nom;
    }
}

