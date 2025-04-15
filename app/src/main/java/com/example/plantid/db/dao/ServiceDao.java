package com.example.plantid.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.plantid.db.entities.Service;

@Dao
public interface ServiceDao {
    @Insert
    void insert(Service service);

    @Query("SELECT COUNT(*) FROM service WHERE nom = :serviceName")
    boolean exists(String serviceName);
}
