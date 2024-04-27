package com.google.firebase.example.fireeats.java;

import android.content.Context;
import android.text.TextUtils;

import com.google.firebase.example.fireeats.R;
import com.google.firebase.example.fireeats.java.model.Anime;
import com.google.firebase.example.fireeats.java.util.AnimeUtil;
import com.google.firebase.firestore.Query;

/**
 * Object for passing filters around.
 */
public class Filters {

    private String genre = null;
    private String season = null;
    private String studios = null;
    private String sortBy = null;
    private Query.Direction sortDirection = null;

    public Filters() {}

    public static Filters getDefault() {
        Filters filters = new Filters();
        filters.setSortBy(Anime.FIELD_AVG_RATING);
        filters.setSortDirection(Query.Direction.DESCENDING);

        return filters;
    }

    public boolean hasGenre() {
        return !(TextUtils.isEmpty(genre));
    }

    public boolean hasSeason() {
        return !(TextUtils.isEmpty(season));
    }

    public boolean hasStudios() {
        return !(TextUtils.isEmpty(studios));
    }

    public boolean hasSortBy() {
        return !(TextUtils.isEmpty(sortBy));
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
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

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public Query.Direction getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(Query.Direction sortDirection) {
        this.sortDirection = sortDirection;
    }

    public String getSearchDescription(Context context) {
        StringBuilder desc = new StringBuilder();

        if (genre == null && season == null && studios == null) {
            desc.append("<b>");
            desc.append(context.getString(R.string.all_animes));
            desc.append("</b>");
        }

        if (genre != null) {
            desc.append("<b>");
            desc.append( genre);
            desc.append("</b>");
        }

        if (genre != null && season != null && studios!=null) {
            desc.append(" in ");
        }

        if (season != null) {
            desc.append("<b>");
            desc.append(season);
            desc.append("</b>");
        }

        if (studios != null) {
            desc.append("<b>");
            desc.append(studios);
            desc.append("</b>");
        }

        return desc.toString();
    }

    public String getOrderDescription(Context context) {
        if (Anime.FIELD_POPULARITY.equals(sortBy)) {
            return context.getString(R.string.sorted_by_popularity);
        } else {
            return context.getString(R.string.sorted_by_rating);
        }
    }
}
