package com.google.firebase.example.fireeats.java.util;

import android.content.Context;
import androidx.annotation.NonNull;
import com.google.firebase.example.fireeats.R;
import com.google.firebase.example.fireeats.java.model.Anime;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import com.opencsv.exceptions.CsvException;

public class AnimeUtil {
    private static final String TAG = "AnimeUtil";
    @NonNull
    public static List<Anime> readFromCSV(Context context) throws IOException, CsvException {
        List<Anime> animeList = new ArrayList<>();

        try (InputStream inputStream = context.getResources().openRawResource(R.raw.animes);
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             BufferedReader reader = new BufferedReader(inputStreamReader)) {
            String line;
            // Skip the header line
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                Anime anime = new Anime();
                anime.setTitle(parts[0]);
                anime.setMain_picture_medium(parts[1]);
                anime.setNum_episodes(Integer.parseInt(parts[2]));

                animeList.add(anime);
            }
        }
        return animeList;
    }
}
