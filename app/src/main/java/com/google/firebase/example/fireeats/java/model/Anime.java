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
    private Integer start_season_year;
    private String start_season_season;
    private String broadcast_day_of_the_week;
    private String genres;
    private String studios;
    private String synopsis;
    private String main_picture_medium;
    private int numRatings;
    private double avgRating;

    public Anime() {
    }

    public Anime(String title, String status, Integer num_episodes, String rating, Integer start_season_year, String start_season_season,
                 String broadcast_day_of_the_week, String genres, String studios, String synopsis, String main_picture_medium,
                  int numRatings, double avgRating) {
        this.title = title;
        this.status = status;
        this.num_episodes = num_episodes;
        this.rating = rating;
        this.start_season_year = start_season_year;
        this.start_season_season = start_season_season;
        this.broadcast_day_of_the_week = broadcast_day_of_the_week;
        this.genres = genres;
        this.studios = studios;
        this.synopsis = synopsis;
        this.main_picture_medium = main_picture_medium;
        this.numRatings = numRatings;
        this.avgRating= avgRating;
    }

    public String getRandomIfNull(String test){
       if (test.equals(null)){
           return "aaa";
       }else return test;
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

    public Integer getStart_season_year() {
        return start_season_year;
    }

    public void setStart_season_year(Integer start_season_year) {
        this.start_season_year = start_season_year;
    }

    public String getStart_season_season() {
        return start_season_season;
    }

    public void setStart_season_season(String start_season_season) {
        this.start_season_season = start_season_season;
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

    public String getBroadcast_day_of_the_week() {
        return broadcast_day_of_the_week;
    }

    public void setBroadcast_day_of_the_week(String broadcast_day_of_the_week) {
        this.broadcast_day_of_the_week = broadcast_day_of_the_week;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
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
