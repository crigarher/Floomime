package com.google.firebase.example.fireeats.java;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.core.text.HtmlCompat;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.example.fireeats.Login;
import com.google.firebase.example.fireeats.R;
import com.google.firebase.example.fireeats.databinding.FragmentMainBinding;
import com.google.firebase.example.fireeats.java.adapter.AnimeAdapter;
import com.google.firebase.example.fireeats.java.model.Rating;
import com.google.firebase.example.fireeats.java.model.Anime;
import com.google.firebase.example.fireeats.java.util.RatingUtil;
import com.google.firebase.example.fireeats.java.util.AnimeUtil;
import com.google.firebase.example.fireeats.java.viewmodel.MainActivityViewModel;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.WriteBatch;
import com.opencsv.exceptions.CsvException;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class MainFragment extends Fragment implements
        FilterDialogFragment.FilterListener,
        AnimeAdapter.onAnimeSelectedListener, View.OnClickListener,
        MenuProvider {

    private static final String TAG = "MainActivity";

    private static final int LIMIT = 50;

    private FragmentMainBinding mBinding;

    private FirebaseFirestore mFirestore;
    private Query mQuery;

    private FilterDialogFragment mFilterDialog;
    private AnimeAdapter mAdapter;

    private MainActivityViewModel mViewModel;

    private MenuHost menuHost;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentMainBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding.filterBar.setOnClickListener(this);
        mBinding.buttonClearFilter.setOnClickListener(this);

        // MenuProvider
        menuHost = requireActivity();
        menuHost.addMenuProvider(this);

        // View model
        mViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);

        // Enable Firestore logging
        FirebaseFirestore.setLoggingEnabled(true);

        // Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Get ${LIMIT} animes
        mQuery = mFirestore.collection("animes")
                .orderBy("avgRating", Query.Direction.DESCENDING)
                .limit(LIMIT);

        // RecyclerView
        mAdapter = new AnimeAdapter(mQuery, this) {
            @Override
            protected void onDataChanged() {
                // Show/hide content if the query returns empty.
                if (getItemCount() == 0) {
                    mBinding.recyclerAnimes.setVisibility(View.GONE);
                    mBinding.viewEmpty.setVisibility(View.VISIBLE);
                } else {
                    mBinding.recyclerAnimes.setVisibility(View.VISIBLE);
                    mBinding.viewEmpty.setVisibility(View.GONE);
                }
            }

            @Override
            protected void onError(FirebaseFirestoreException e) {
                // Show a snackbar on errors
                Snackbar.make(mBinding.getRoot(),
                        "Error: check logs for info.", Snackbar.LENGTH_LONG).show();
            }
        };

        mBinding.recyclerAnimes.setLayoutManager(new LinearLayoutManager(requireContext()));
        mBinding.recyclerAnimes.setAdapter(mAdapter);

        // Filter Dialog
        mFilterDialog = new FilterDialogFragment();
    }

    @Override
    public void onStart() {
        super.onStart();

        // Apply filters
        onFilter(mViewModel.getFilters());

        // Start listening for Firestore updates
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }

    @Override
    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onMenuItemSelected(@NonNull MenuItem item) {
        //Due to bump in Java version, we can not use view ids in switch
        //(see: http://tools.android.com/tips/non-constant-fields), so we
        //need to use if/else:

        int itemId = item.getItemId();
        if (itemId == R.id.menu_add_items) {
            onAddItemsClicked();
            return true;
        } else if (itemId == R.id.menu_sign_out) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getContext(), Login.class);
            startActivity(intent);
            return true;
        }
        return false;
    }


    public void onFilterClicked() {
        // Show the dialog containing filter options
        mFilterDialog.show(getChildFragmentManager(), FilterDialogFragment.TAG);
    }

    public void onClearFilterClicked() {
        mFilterDialog.resetFilters();

        onFilter(Filters.getDefault());
    }

    @Override
    public void onAnimeSelected(DocumentSnapshot anime) {
        // Go to the details page for the selected anime
        MainFragmentDirections.ActionMainFragmentToAnimeDetailFragment action = MainFragmentDirections
                .actionMainFragmentToAnimeDetailFragment(anime.getId());

        NavHostFragment.findNavController(this)
                .navigate(action);
    }
    @Override
    public void onFilter(Filters filters) {
        Query query = mFirestore.collection("animes");
        //Genre
        if (filters.hasGenre()) {
            query = query.whereEqualTo(Anime.GENRES, filters.getGenre());
        }
        // Season
        if (filters.hasSeason()) {
            query = query.whereEqualTo(Anime.SEASON, filters.getSeason());
        }
        //Studios
        if (filters.hasStudios()) {
            query = query.whereEqualTo(Anime.STUDIOS, filters.getStudios());
        }
        if (filters.hasSortBy()) {
            query = query.orderBy(filters.getSortBy(), filters.getSortDirection());
        }

        // Limit items
        query = query.limit(LIMIT);

        // Update the query
        mAdapter.setQuery(query);

        // Set header
        mBinding.textCurrentSearch.setText(HtmlCompat.fromHtml(filters.getSearchDescription(requireContext()),
                HtmlCompat.FROM_HTML_MODE_LEGACY));
        mBinding.textCurrentSortBy.setText(filters.getOrderDescription(requireContext()));

        // Save filters
        mViewModel.setFilters(filters);
    }
    private boolean shouldStartSignIn() {
        return (!mViewModel.getIsSigningIn() && FirebaseAuth.getInstance().getCurrentUser() == null);
    }

    private void onAddItemsClicked() {
        // Add a bunch of random animes
        WriteBatch batch = mFirestore.batch();
        try {
            List<Anime> animeList = AnimeUtil.readFromCSV(requireContext());
            for (Anime anime : animeList) {
                DocumentReference animeRef = mFirestore.collection("animes").document();

                batch.set(animeRef, anime);
            }
        } catch (IOException | CsvException e) {
            Log.e(TAG, "Error adding items from CSV", e);
        }

        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Write batch succeeded.");
                } else {
                    Log.w(TAG, "write batch failed.", task.getException());
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.filterBar) {
            onFilterClicked();
        } else if (viewId == R.id.buttonClearFilter) {
            onClearFilterClicked();
        }
    }


}
