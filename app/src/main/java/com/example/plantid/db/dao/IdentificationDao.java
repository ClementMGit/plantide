package com.example.plantid.db.dao;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.plantid.db.entities.IdentificationAvecImages;

@Dao
public interface IdentificationDao {
    @Transaction
    @Query("SELECT * FROM Identification WHERE id = :id")
    IdentificationAvecImages getIdentificationAvecImages(long id);
}

