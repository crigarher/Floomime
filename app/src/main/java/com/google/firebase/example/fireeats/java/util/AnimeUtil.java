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
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.opencsv.exceptions.CsvException;

public class AnimeUtil {
    private static final String TAG = "AnimeUtil";
    private static final String [] STATUS ={
            "Finished Airing",
            "Currently Airing",
            "Not yet aired"

    };
    private static String getRandomString(String[] array, Random random) {
        int ind = random.nextInt(array.length);
        return array[ind];
    }

    private static String getRandomStatus(Random random){
        return getRandomString(STATUS, random);
    }
    @NonNull
    public static List<Anime> readFromCSV(Context context) throws IOException, CsvException {
        List<Anime> animeList = new ArrayList<>();
        Random random = new Random();

        try (InputStream inputStream = context.getResources().openRawResource(R.raw.animes);
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             BufferedReader reader = new BufferedReader(inputStreamReader)) {
            String line;
            // Skip the header line
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] seasons = context.getResources().getStringArray(R.array.seasons);
                seasons = Arrays.copyOfRange(seasons, 1, seasons.length);

                String[] genres = context.getResources().getStringArray(R.array.genres);
                genres = Arrays.copyOfRange(genres, 1, genres.length);

                String[] studios = context.getResources().getStringArray(R.array.studios);
                studios = Arrays.copyOfRange(studios, 1, studios.length);

                String[] parts = line.split(";");
                Anime anime = new Anime();
                anime.setTitle(parts[0]);
                anime.setMain_picture_medium(parts[1]);
                anime.setNum_episodes(Integer.parseInt(parts[2]));
                anime.setGenres(getRandomString(genres,random));
                anime.setStart_season_season(getRandomString(seasons,random));
                anime.setStudios(getRandomString(studios,random));
                anime.setStatus(getRandomStatus(random));

                animeList.add(anime);
            }
        }
        return animeList;
    }


}
