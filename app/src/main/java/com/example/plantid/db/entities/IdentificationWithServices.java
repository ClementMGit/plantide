package com.example.plantid.db.entities;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class IdentificationWithServices {
    @Embedded
    public Identification identification;

    @Relation(
            parentColumn = "id",
            entityColumn = "identificationId",
            entity = IdentificationService.class
    )
    public List<IdentificationService> services;
}

