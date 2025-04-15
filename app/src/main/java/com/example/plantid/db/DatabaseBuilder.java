package com.example.plantid.db;


import android.content.Context;
import android.util.Log;

import com.example.plantid.db.entities.Espece;
import com.example.plantid.db.entities.ProposeService;
import com.example.plantid.db.entities.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DatabaseBuilder {

    public static void buildDatabaseFromCsv(Context context) {
        AppDatabase myDatabase = AppDatabase.getDatabase(context);

        try {
            InputStream is = context.getAssets().open("services.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;

            // Sauter les lignes d'en-tête
            reader.readLine();
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(";");

                if (tokens.length >= 5) {
                    try {
                        String espece = tokens[1];
                        String service = tokens[0];
                        float value = Float.parseFloat(tokens[2]);
                        float reliability = Float.parseFloat(tokens[3]);

                        if (!myDatabase.especeDao().exists(espece)) {
                            myDatabase.especeDao().insert(new Espece(espece));
                        }

                        if (service.equals("storage_and_return_water") || service.equals("nitrogen_provision") || service.equals("soil_structuration")) {
                            if (!myDatabase.serviceDao().exists(service)) {
                                myDatabase.serviceDao().insert(new Service(service));
                            }
                        } else {
                            Log.e("CSV Error", "Service invalide : " + service);
                            continue;
                        }

                        ProposeService ps = new ProposeService(espece, service, value, reliability);
                        myDatabase.proposeServiceDao().insert(ps);

                    } catch (NumberFormatException e) {
                        Log.e("CSV Error", "Erreur de format pour la ligne : " + line, e);
                    }
                } else {
                    Log.e("CSV Error", "Ligne mal formée : " + line);
                }
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

