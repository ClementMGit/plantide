package com.example.plantid.db.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.Index;

@Entity(
        foreignKeys = {
                @ForeignKey(
                        entity = ProposeService.class,
                        parentColumns = {"nomEspece", "nomService"},
                        childColumns = {"nomEspece", "nomService"},
                        onDelete = ForeignKey.CASCADE
                )
        },
        indices = {@Index(value = {"nomEspece", "nomService"})}
)
public class Identification {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public String date; // ou LocalDate si tu ajoutes un TypeConverter
    public String notesPersonnelles;

    public String nomEspece;
    public String nomService;
}

