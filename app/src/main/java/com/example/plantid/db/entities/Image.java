package com.example.plantid.db.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        foreignKeys = {
                @ForeignKey(
                        entity = Identification.class,
                        parentColumns = "id",
                        childColumns = "identificationId",
                        onDelete = ForeignKey.CASCADE
                )
        },
        indices = {@Index("identificationId")}
)
public class Image {
    @PrimaryKey
    @NonNull
    public String uri;

    public long identificationId;

    public Image(@NonNull String uri) {
        this.uri = uri;
    }
}
