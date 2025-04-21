package com.example.plantid.db.repositories;

import android.content.Context;

import com.example.plantid.db.AppDatabase;
import com.example.plantid.db.dao.EspeceDao;
import com.example.plantid.db.entities.Espece;

import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.List;

public class EspeceRepository {
    private AppDatabase db;

    private final EspeceDao especeDao;

    public EspeceRepository(Context context) {
        db = AppDatabase.getDatabase(context);
        this.especeDao = db.especeDao();
    }

    /**
     * Trouve l'espèce la plus proche du nom fourni selon la distance de Levenshtein.
     * @param nomSaisi le nom à comparer
     * @return l'espèce la plus proche ou null s'il n'y a rien en base
     */
    public Espece getClosestEspece(String nomSaisi) {
        List<Espece> toutesLesEspeces = especeDao.getAll();
        if (toutesLesEspeces.isEmpty()) return null;

        int seuilMaxDistance = Math.max(2, nomSaisi.length() / 3);

        LevenshteinDistance ld = new LevenshteinDistance();
        Espece plusProche = null;
        int minDistance = Integer.MAX_VALUE;

        for (Espece e : toutesLesEspeces) {
            int distance = ld.apply(nomSaisi.toLowerCase(), e.nom.toLowerCase());
            if (distance < minDistance) {
                minDistance = distance;
                plusProche = e;
            }
        }

        return (minDistance <= seuilMaxDistance) ? plusProche : null;
    }

}
