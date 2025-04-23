package com.example.plantid.db.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        foreignKeys = {
                @ForeignKey(
                        entity = ProposeService.class,
                        parentColumns = {"nomEspece", "nomService"},
                        childColumns = {"nomEspece", "nomService"},
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Identification.class,
                        parentColumns = "id",
                        childColumns = "identificationId",
                        onDelete = ForeignKey.CASCADE
                )
        }
)
public class IdentificationService {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public long identificationId;  // ID de l'identification associée
    public String nomEspece;       // Nom de l'espèce
    public String nomService;      // Nom du service proposé
    public float qualite;          // Qualité du service (ou autre attribut)
}


