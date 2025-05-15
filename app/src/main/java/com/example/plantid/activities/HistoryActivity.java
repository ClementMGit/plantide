package com.example.plantid.activities;

import android.graphics.Rect;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;

import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plantid.R;
import com.example.plantid.db.AppDatabase;
import com.example.plantid.db.dao.IdentificationDao;
import com.example.plantid.db.entities.IdentificationWithServices;
import com.example.plantid.utils.IdentificationAdapter;
import com.example.plantid.utils.ImagePickerHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class HistoryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private IdentificationAdapter adapter;
    private SearchView searchView;
    private Spinner spinnerTri;
    private List<IdentificationWithServices> allItems = new ArrayList<>();
    private List<IdentificationWithServices> displayedItems = new ArrayList<>();
    private AppDatabase db;
    private IdentificationDao identificationDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        recyclerView = findViewById(R.id.recyclerViewIdentifications);
        searchView = findViewById(R.id.searchView);
        spinnerTri = findViewById(R.id.spinnerTri);
        searchView.setQueryHint("Rechercher");
        ImageButton takePhotoButton = findViewById(R.id.take_photo_from_history);
        ImagePickerHelper helper = new ImagePickerHelper(this);
        takePhotoButton.setOnClickListener(v -> {
            helper.pickImage(null);
        });

        adapter = new IdentificationAdapter(displayedItems,
                item -> {}, // onClick si besoin
                this::onDeleteClick,this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        db = AppDatabase.getDatabase(this);
        identificationDao = db.identificationDao();
        observeData();

        setupSpinner();
        setupSearch();
    }

    private void observeData() {
        identificationDao.getAllIdentificationsWithServices().observe(this, new Observer<List<IdentificationWithServices>>() {
            @Override
            public void onChanged(List<IdentificationWithServices> identifications) {
                allItems.clear();
                if (identifications != null) {
                    allItems.addAll(identifications);
                }
                filterAndSort();
            }
        });
    }

    private void setupSpinner() {
        // Créer un tableau avec les 4 options
        ArrayAdapter<String> triAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, new String[]{
                "Nom (A - Z)", "Nom (Z - A)", "Date (Plus récent)", "Date (Plus ancien)"
        });
        triAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTri.setAdapter(triAdapter);

        spinnerTri.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterAndSort();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupSearch() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return true; }
            @Override
            public boolean onQueryTextChange(String newText) {
                filterAndSort();
                return true;
            }
        });
    }

    private void filterAndSort() {
        String query = searchView.getQuery().toString().toLowerCase().trim();
        displayedItems.clear();

        for (IdentificationWithServices item : allItems) {
            String espece = item.services.isEmpty() ? "" : item.services.get(0).nomEspece;
            if (espece.toLowerCase().contains(query)) {
                displayedItems.add(item);
            }
        }

        Comparator<IdentificationWithServices> comparator = Comparator.comparing(item ->
                item.services.isEmpty() ? "" : item.services.get(0).nomEspece, String::compareToIgnoreCase);

        switch (spinnerTri.getSelectedItemPosition()) {
            case 1: // Z-A
                comparator = comparator.reversed();
                break;
            case 2: // Plus récent
                comparator = comparator.thenComparing((a, b) ->
                        b.identification.date.compareToIgnoreCase(a.identification.date));
                break;
            case 3: // Plus ancien
                comparator = comparator.thenComparing((a, b) ->
                        a.identification.date.compareToIgnoreCase(b.identification.date));
                break;
            default: // A-Z
                break;
        }

        Collections.sort(displayedItems, comparator);
        adapter.notifyDataSetChanged();
    }

    private void onDeleteClick(IdentificationWithServices item) {
        new AlertDialog.Builder(this)
                .setTitle("Supprimer")
                .setMessage("Supprimer cette identification ?")
                .setPositiveButton("Oui", (dialog, which) -> {
                    // À compléter si tu veux supprimer depuis Room
                    allItems.remove(item);
                    filterAndSort();
                })
                .setNegativeButton("Non", null)
                .show();
    }
}


