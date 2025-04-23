package com.example.plantid.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.plantid.db.dao.EspeceDao;
import com.example.plantid.db.dao.IdentificationDao;
import com.example.plantid.db.dao.IdentificationServiceDao;
import com.example.plantid.db.dao.ProposeServiceDao;
import com.example.plantid.db.dao.ServiceDao;
import com.example.plantid.db.entities.Espece;
import com.example.plantid.db.entities.Identification;
import com.example.plantid.db.entities.IdentificationService;
import com.example.plantid.db.entities.ProposeService;
import com.example.plantid.db.entities.Service;
import com.example.plantid.utils.UriListConverter;

@Database(
        entities = {
                Espece.class,
                Service.class,
                ProposeService.class,
                Identification.class,
                IdentificationService.class
        },
        version = 1
)
@TypeConverters({UriListConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract EspeceDao especeDao();
    public abstract ServiceDao serviceDao();
    public abstract ProposeServiceDao proposeServiceDao();
    public abstract IdentificationServiceDao identificationServiceDao();
    public abstract IdentificationDao identificationDao();


    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "app_db")
                            .createFromAsset("app_db")
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}

