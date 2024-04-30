package com.google.firebase.example.fireeats.java.model;

import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.Set;

@IgnoreExtraProperties
public class Anime {

    public static final String GENRES = "genres";
    public static final String SEASON = "season";
    public static final String STUDIOS = "studios";
    public static final String FIELD_POPULARITY = "numRatings";
    public static final String FIELD_AVG_RATING = "avgRating";
    private String title;
    private String status;
    private Integer num_episodes;
    private String rating;
    private String season;
    private String genres;
    private String studios;
    private String main_picture_medium;
    private int numRatings;
    private double avgRating;

    public Anime() {
    }

    public Anime(String title, String status, Integer num_episodes, String rating, String season,
                 String genres, String studios, String main_picture_medium,
                  int numRatings, double avgRating) {
        this.title = title;
        this.status = status;
        this.num_episodes = num_episodes;
        this.rating = rating;
        this.season = season;
        this.genres = genres;
        this.studios = studios;
        this.main_picture_medium = main_picture_medium;
        this.numRatings = numRatings;
        this.avgRating= avgRating;
    }



    public String getTitle() {
        return title;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }


    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public String getStudios() {
        return studios;
    }

    public void setStudios(String studios) {
        this.studios = studios;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getNum_episodes() {
        return num_episodes;
    }

    public void setNum_episodes(Integer num_episodes) {
        this.num_episodes = num_episodes;
    }



    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }



    public String getMain_picture_medium() {
        return main_picture_medium;
    }

    public void setMain_picture_medium(String main_picture_medium) {
        this.main_picture_medium = main_picture_medium;
    }

    public int getNumRatings() {
        return numRatings;
    }

    public void setNumRatings(int numRatings) {
        this.numRatings = numRatings;
    }

    public double getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(double avgRating) {
        this.avgRating = avgRating;
    }
}
