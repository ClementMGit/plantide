package com.example.plantid.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.plantid.db.entities.Identification;
import com.example.plantid.db.entities.IdentificationService;
import com.example.plantid.db.entities.IdentificationWithServices;

import java.util.List;

@Dao
public interface IdentificationServiceDao {

    // Insérer une association Identification-Service
    @Insert
    void insert(IdentificationService identificationService);

    // Récupérer tous les services associés à une identification
    @Query("SELECT * FROM IdentificationService WHERE identificationId = :identificationId")
    List<IdentificationService> getByIdentification(long identificationId);

    // Supprimer les services associés à une identification
    @Query("DELETE FROM IdentificationService WHERE identificationId = :identificationId")
    void deleteByIdentification(long identificationId);
}



