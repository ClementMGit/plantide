package com.example.plantid.db.entities;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class IdentificationAvecImages {
    @Embedded
    public Identification identification;

    @Relation(
            parentColumn = "id",
            entityColumn = "identificationId"
    )
    public List<Image> images;
}

