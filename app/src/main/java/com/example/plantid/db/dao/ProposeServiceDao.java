package com.example.plantid.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Delete;

import com.example.plantid.db.entities.ProposeService;

import java.util.List;

@Dao
public interface ProposeServiceDao {

    // 🔹 Insertion simple
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ProposeService proposeService);



    // 🔹 Supprimer une association
    @Delete
    void delete(ProposeService proposeService);

    // 🔹 Optionnel : chercher les services proposés par une espèce donnée
    @Query("SELECT * FROM ProposeService WHERE nomEspece = :nomEspece")
    List<ProposeService> getByEspece(String nomEspece);
}

