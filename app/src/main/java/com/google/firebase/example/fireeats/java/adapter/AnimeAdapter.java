package com.google.firebase.example.fireeats.java.adapter;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.example.fireeats.databinding.ItemAnimeBinding;
import com.google.firebase.example.fireeats.java.model.Anime;
import com.google.firebase.example.fireeats.java.util.AnimeUtil;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

public class AnimeAdapter extends FirestoreAdapter<AnimeAdapter.ViewHolder>{

    public interface onAnimeSelectedListener{
        void onAnimeSelected(DocumentSnapshot anime);
    }

    private onAnimeSelectedListener mListener;

    public AnimeAdapter(Query query, onAnimeSelectedListener listener){
        super(query);
        mListener=listener;
    }
    @Override
    public  ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(ItemAnimeBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private  ItemAnimeBinding binding;
        public  ViewHolder(ItemAnimeBinding binding){
            super(binding.getRoot());
            this.binding=binding;
        }
        public ViewHolder(View itemView) {
            super(itemView);
        }
        public void bind(final DocumentSnapshot snapshot,
                         final onAnimeSelectedListener listener) {
            Anime anime = snapshot.toObject(Anime.class);
            Resources resources = itemView.getResources();
            //Load anime pic
            Glide.with(binding.animePic.getContext()).
                    load(anime.getMain_picture_medium())
                    .into(binding.animePic);
            binding.animeTitle.setText(anime.getTitle());
            // Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onAnimeSelected(snapshot);
                    }
                }
            });
        }
    }


}
