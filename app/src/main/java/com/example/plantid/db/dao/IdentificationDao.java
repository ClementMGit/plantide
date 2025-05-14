package com.example.plantid.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.plantid.db.entities.Identification;
import com.example.plantid.db.entities.IdentificationWithServices;

import java.util.List;

@Dao
public interface IdentificationDao {

    // Insérer une identification
    @Insert
    long insert(Identification identification);
    @Transaction
    @Query("SELECT * FROM Identification WHERE id = :id")
    IdentificationWithServices getWithServices(long id);

    // Récupérer une identification par son ID
    @Query("SELECT * FROM Identification WHERE id = :id")
    Identification getById(long id);

    // Récupérer toutes les identifications
    @Query("SELECT * FROM Identification")
    List<Identification> getAll();

    // Mettre à jour une identification
    @Update
    void update(Identification identification);

    // Supprimer une identification
    @Delete
    void delete(Identification identification);

    @Transaction
    @Query("SELECT * FROM Identification")
    LiveData<List<IdentificationWithServices>> getAllIdentificationsWithServices();
}

