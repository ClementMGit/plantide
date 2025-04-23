package com.example.plantid.utils;

import android.net.Uri;
import android.text.TextUtils;

import androidx.room.TypeConverter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UriListConverter {

    // Convertit une liste de Uri en une chaîne séparée par des ";;"
    @TypeConverter
    public static String fromUriList(List<Uri> uris) {
        if (uris == null) {
            return null;
        }
        List<String> urisAsString = new ArrayList<>();
        for (Uri uri : uris) {
            urisAsString.add(uri.toString()); // Convertit chaque Uri en String
        }
        return TextUtils.join(";;", urisAsString); // Les séparer par ";;"
    }

    // Convertit une chaîne séparée par ";;" en une liste de Uri
    @TypeConverter
    public static List<Uri> toUriList(String data) {
        if (data == null || data.isEmpty()) {
            return new ArrayList<>();
        }
        List<String> urisAsString = Arrays.asList(data.split(";;"));
        List<Uri> uris = new ArrayList<>();
        for (String uriString : urisAsString) {
            uris.add(Uri.parse(uriString)); // Convertit chaque String en Uri
        }
        return uris;
    }
}
