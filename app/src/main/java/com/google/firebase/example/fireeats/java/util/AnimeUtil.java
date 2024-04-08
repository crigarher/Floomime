package com.google.firebase.example.fireeats.java.util;
import android.content.Context;

import com.google.firebase.example.fireeats.R;
import com.google.firebase.example.fireeats.java.model.Anime;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

public class AnimeUtil {
    private static final String TAG = "AnimeUtil";
    public static List<Anime> readFromCSV(Context context) throws IOException, CsvException {
        List<Anime> animeList = new ArrayList<>();

        try (InputStream inputStream =  context.getResources().openRawResource(R.raw.animes);
             InputStreamReader reader = new InputStreamReader(inputStream);
             CSVReader csvReader = new CSVReader(reader)) {

            List<String[]> csvData = csvReader.readAll();
            csvData.remove(0);

            for (String[] row : csvData) {
                Anime anime = new Anime();
                anime.setStatus(row[1]);
                anime.setNum_episodes(Integer.parseInt(row[2]));
                anime.setRating(row[3]);
                anime.setStart_season_year(Integer.parseInt(row[4]));
                anime.setStart_season_season(row[5]);
                anime.setBroadcast_day_of_the_week(row[6]);
                anime.setGenres(parseValues(row[7]));
                anime.setStudios(parseValues(row[8]));
                anime.setSynopsis(row[9]);
                anime.setMain_picture_medium(row[10]);
                anime.setAlternative_title_en(row[11]);
                anime.setAlternative_title_synonyms(parseValues(row[12]));
                anime.setNumRatings(Integer.parseInt(row[13]));


                animeList.add(anime);
            }
        }

        return animeList;
    }

    private static Set<String> parseValues(String valuesString) {
        Set<String> values = new HashSet<>();
        String[] valuesArray = valuesString.split(",");
        for (String value : valuesArray) {
            values.add(value.trim());
        }
        return values;
    }
}
