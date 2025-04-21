package com.example.plantid.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.plantid.db.entities.Espece;

import java.util.List;

@Dao
public interface EspeceDao {
    @Query("SELECT * FROM Espece")
    List<Espece> getAll();

    @Insert
    void insert(Espece espece); // Insère une nouvelle espèce

    @Query("SELECT COUNT(*) FROM espece WHERE nom = :nomEspece")
    boolean exists(String nomEspece); // Vérifie si une espèce existe dans la base de données
}

