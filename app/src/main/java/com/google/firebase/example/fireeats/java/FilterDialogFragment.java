package com.google.firebase.example.fireeats.java;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.example.fireeats.R;
import com.google.firebase.example.fireeats.databinding.DialogFiltersBinding;
import com.google.firebase.example.fireeats.java.model.Anime;
import com.google.firebase.firestore.Query;

/**
 * Dialog Fragment containing filter form.
 */
public class FilterDialogFragment extends DialogFragment implements View.OnClickListener {

    public static final String TAG = "FilterDialog";

    interface FilterListener {

        void onFilter(Filters filters);

    }

    private DialogFiltersBinding mBinding;
    private FilterListener mFilterListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DialogFiltersBinding.inflate(inflater, container, false);
        
        mBinding.buttonSearch.setOnClickListener(this);
        mBinding.buttonCancel.setOnClickListener(this);

        return mBinding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (getParentFragment() instanceof FilterListener) {
            mFilterListener = (FilterListener) getParentFragment();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public void onSearchClicked() {
        if (mFilterListener != null) {
            mFilterListener.onFilter(getFilters());
        }

        dismiss();
    }

    public void onCancelClicked() {
        dismiss();
    }

    @Nullable
    private String getSelectedGenre() {
        String selected = (String) mBinding.spinnerGenre.getSelectedItem();
        if (getString(R.string.value_any_category).equals(selected)) {
            return null;
        } else {
            return selected;
        }
    }

    @Nullable
    private String getSelectedSeason() {
        String selected = (String) mBinding.spinnerSeason.getSelectedItem();
        if (getString(R.string.value_any_season).equals(selected)) {
            return null;
        } else {
            return selected;
        }
    }

    private String getSelectedStudios() {
        String selected = (String) mBinding.spinnerStudios.getSelectedItem();
        if (getString(R.string.value_any_studios).equals(selected)) {
            return null;
        } else {
            return selected;
        }
    }

    @Nullable
    private String getSelectedSortBy() {
        String selected = (String) mBinding.spinnerSort.getSelectedItem();
        if (getString(R.string.sort_by_rating).equals(selected)) {
            return Anime.FIELD_AVG_RATING;
        }  if (getString(R.string.sort_by_popularity).equals(selected)) {
            return Anime.FIELD_POPULARITY;
        }

        return null;
    }

    @Nullable
    private Query.Direction getSortDirection() {
        String selected = (String) mBinding.spinnerSort.getSelectedItem();
        if (getString(R.string.sort_by_rating).equals(selected)) {
            return Query.Direction.DESCENDING;
        }
        if (getString(R.string.sort_by_popularity).equals(selected)) {
            return Query.Direction.DESCENDING;
        }

        return null;
    }

    public void resetFilters() {
        if (mBinding != null) {
            mBinding.spinnerGenre.setSelection(0);
            mBinding.spinnerSeason.setSelection(0);
            mBinding.spinnerStudios.setSelection(0);
            mBinding.spinnerSort.setSelection(0);
        }
    }

    public Filters getFilters() {
        Filters filters = new Filters();

        if (mBinding != null) {
            filters.setGenre(getSelectedGenre());
            filters.setSeason(getSelectedSeason());
            filters.setStudios(getSelectedStudios());
            filters.setSortBy(getSelectedSortBy());
            filters.setSortDirection(getSortDirection());
        }

        return filters;
    }

    @Override
    public void onClick(View v) {
        //Due to bump in Java version, we can not use view ids in switch
        //(see: http://tools.android.com/tips/non-constant-fields), so we
        //need to use if/else:

        int viewId = v.getId();
        if (viewId == R.id.buttonSearch) {
            onSearchClicked();
        } else if (viewId == R.id.buttonCancel) {
            onCancelClicked();
        }
    }
}
