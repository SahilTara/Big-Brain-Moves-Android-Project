package com.uottawa.bigbrainmoves.servio.util.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.uottawa.bigbrainmoves.servio.R;
import com.uottawa.bigbrainmoves.servio.models.Rating;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RatingListAdapter extends RecyclerView.Adapter<RatingListAdapter.ViewHolder> {
    private List<Rating> ratings = new ArrayList<>();
    public RatingListAdapter(List<Rating> ratings) {
        this.ratings.addAll(ratings);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
         View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.rating_list_item, viewGroup, false);
         return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        final Rating rating = ratings.get(position);

        viewHolder.raterUserTextView.setText(rating.getRaterUser());
        viewHolder.ratingPostTimeTextView.setText(rating.getStringDate());
        viewHolder.ratingCommentTextView.setText(rating.getComment());


        viewHolder.ratingBar.setRating((float) rating.getRatingGiven());

    }

    @Override
    public int getItemCount() {
        return ratings.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView raterUserTextView;
        TextView ratingPostTimeTextView;
        RatingBar ratingBar;
        TextView ratingCommentTextView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            raterUserTextView = itemView.findViewById(R.id.raterUserTextView);
            ratingPostTimeTextView = itemView.findViewById(R.id.ratingPostTimeTextView);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            ratingCommentTextView = itemView.findViewById(R.id.ratingCommentTextView);
        }
    }
}
