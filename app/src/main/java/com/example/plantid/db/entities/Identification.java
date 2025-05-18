package com.example.plantid.db.entities;

import android.net.Uri;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.Index;
import androidx.room.TypeConverters;

import com.example.plantid.utils.UriListConverter;

import java.util.List;

@Entity
public class Identification {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public String date;
    public String notesPersonnelles;
    public String gbifId;

    @TypeConverters(UriListConverter.class)
    public List<Uri> imageUris;
}



