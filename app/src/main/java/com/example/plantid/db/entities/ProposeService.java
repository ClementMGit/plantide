package com.example.plantid.db.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(
        primaryKeys = {"nomEspece", "nomService"},
        foreignKeys = {
                @ForeignKey(
                        entity = Espece.class,
                        parentColumns = "nom",
                        childColumns = "nomEspece",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Service.class,
                        parentColumns = "nom",
                        childColumns = "nomService",
                        onDelete = ForeignKey.CASCADE
                )
        }
)
public class ProposeService {

    @NonNull
    public String nomEspece;

    @NonNull
    public String nomService;

    public float qualite;
    public float confiance;

    public ProposeService(@NonNull String nomEspece, @NonNull String nomService, float qualite, float confiance) {
        this.nomEspece = nomEspece;
        this.nomService = nomService;
        this.qualite = qualite;
        this.confiance = confiance;
    }
    public float getConfiance() {
        return confiance;
    }

    public float getQualite() {
        return qualite;
    }
    public String getNomService() {
        return nomService;
    }
}

