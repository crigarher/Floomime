package com.google.firebase.example.fireeats.java;

import android.app.BroadcastOptions;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.example.fireeats.R;
import com.google.firebase.example.fireeats.databinding.FragmentAnimeDetailBinding;
import com.google.firebase.example.fireeats.java.adapter.RatingAdapter;
import com.google.firebase.example.fireeats.java.model.Rating;
import com.google.firebase.example.fireeats.java.model.Anime;
import com.google.firebase.example.fireeats.java.util.AnimeUtil;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Transaction;

public class AnimeDetailFragment extends Fragment
        implements EventListener<DocumentSnapshot>, RatingDialogFragment.RatingListener, View.OnClickListener {

    private static final String TAG = "AnimeDetail";

    private FragmentAnimeDetailBinding mBinding;
    
    private RatingDialogFragment mRatingDialog;

    private FirebaseFirestore mFirestore;
    private DocumentReference mAnimeRef;
    private ListenerRegistration mAnimeRegistration;

    private RatingAdapter mRatingAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = FragmentAnimeDetailBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBinding.animeButtonBack.setOnClickListener(this);
        mBinding.fabShowRatingDialog.setOnClickListener(this);

        String AnimeId = AnimeDetailFragmentArgs.fromBundle(getArguments()).getKeyAnimeId();

        // Initialize Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Get reference to the Anime
        mAnimeRef = mFirestore.collection("animes").document(AnimeId);

        // Get ratings
        Query ratingsQuery = mAnimeRef
                .collection("ratings")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(50);

        // RecyclerView
        mRatingAdapter = new RatingAdapter(ratingsQuery) {
            @Override
            protected void onDataChanged() {
                if (getItemCount() == 0) {
                    mBinding.recyclerRatings.setVisibility(View.GONE);
                    mBinding.viewEmptyRatings.setVisibility(View.VISIBLE);
                } else {
                    mBinding.recyclerRatings.setVisibility(View.VISIBLE);
                    mBinding.viewEmptyRatings.setVisibility(View.GONE);
                }
            }
        };
        mBinding.recyclerRatings.setLayoutManager(new LinearLayoutManager(requireContext()));
        mBinding.recyclerRatings.setAdapter(mRatingAdapter);

        mRatingDialog = new RatingDialogFragment();
    }

    @Override
    public void onStart() {
        super.onStart();

        mRatingAdapter.startListening();
        mAnimeRegistration = mAnimeRef.addSnapshotListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        mRatingAdapter.stopListening();

        if (mAnimeRegistration != null) {
            mAnimeRegistration.remove();
            mAnimeRegistration = null;
        }
    }

    /**
     * Listener for the Anime document ({@link #mAnimeRef}).
     */
    @Override
    public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {
        if (e != null) {
            Log.w(TAG, "Anime:onEvent", e);
            return;
        }

        onAnimeLoaded(snapshot.toObject(Anime.class));
    }

    private void onAnimeLoaded(Anime Anime) {
        mBinding.animeName.setText(Anime.getTitle());
        mBinding.animeStudios.setText(Anime.getStudios());
        mBinding.animeRating.setRating((float) Anime.getAvgRating());
        mBinding.animeNumRatings.setText(getString(R.string.fmt_num_ratings, Anime.getNumRatings()));
        mBinding.animeSeason.setText(Anime.getSeason());
        mBinding.animeGenre.setText(Anime.getGenres());
        //mBinding.animeNumEpisodes.setText(Anime.getNum_episodes());


        // Background image
        Glide.with(mBinding.animeImage.getContext())
                .load(Anime.getMain_picture_medium())
                .into(mBinding.animeImage);
    }

    public void onBackArrowClicked(View view) {
        NavHostFragment.findNavController(this).popBackStack();
    }

    public void onAddRatingClicked(View view) {
        mRatingDialog.show(getChildFragmentManager(), RatingDialogFragment.TAG);
    }

    @Override
    public void onRating(Rating rating) {
        // In a transaction, add the new rating and update the aggregate totals
        addRating(mAnimeRef, rating)
                .addOnSuccessListener(requireActivity(), new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Rating added");

                        // Hide keyboard and scroll to top
                        hideKeyboard();
                        mBinding.recyclerRatings.smoothScrollToPosition(0);
                    }
                })
                .addOnFailureListener(requireActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Add rating failed", e);

                        // Show failure message and hide keyboard
                        hideKeyboard();
                        Snackbar.make(mBinding.getRoot(), "Failed to add rating",
                                Snackbar.LENGTH_SHORT).show();
                    }
                });
    }

    private Task<Void> addRating(final DocumentReference AnimeRef, final Rating rating) {
        // Create reference for new rating, for use inside the transaction
        final DocumentReference ratingRef = AnimeRef.collection("ratings").document();

        // In a transaction, add the new rating and update the aggregate totals
        return mFirestore.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                Anime Anime = transaction.get(AnimeRef).toObject(Anime.class);

                // Compute new number of ratings
                int newNumRatings = Anime.getNumRatings() + 1;

                // Compute new average rating
                double oldRatingTotal = Anime.getAvgRating() * Anime.getNumRatings();
                double newAvgRating = (oldRatingTotal + rating.getRating()) / newNumRatings;

                // Set new Anime info
                Anime.setNumRatings(newNumRatings);
                Anime.setAvgRating(newAvgRating);

                // Commit to Firestore
                transaction.set(AnimeRef, Anime);
                transaction.set(ratingRef, rating);

                return null;
            }
        });
    }

    private void hideKeyboard() {
        View view = requireActivity().getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onClick(View v) {
        //Due to bump in Java version, we can not use view ids in switch
        //(see: http://tools.android.com/tips/non-constant-fields), so we
        //need to use if/else:

        int viewId = v.getId();
        if (viewId == R.id.animeButtonBack) {
            onBackArrowClicked(v);
        } else if (viewId == R.id.fabShowRatingDialog) {
            onAddRatingClicked(v);
        }
    }

}
