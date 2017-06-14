package com.example.sneha.movielist.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sneha.movielist.R;

/**
 * Defines movie list row elements.
 */
public class ContentItemViewHolder extends RecyclerView.ViewHolder{

    private TextView title;
    private ImageView posterPath;
    private TextView character;
    private TextView department;
    private TextView releaseDate;

    public ContentItemViewHolder(View itemView) {
        super(itemView);
        title = (TextView) itemView.findViewById(R.id.title);
        posterPath = (ImageView) itemView.findViewById(R.id.posterPath);
        character = (TextView) itemView.findViewById(R.id.character);
        department = (TextView) itemView.findViewById(R.id.department);
        releaseDate = (TextView) itemView.findViewById(R.id.releaseDate);
    }

    public TextView getTitle() {
        return title;
    }

    public ImageView getPosterPath() {
        return posterPath;
    }

    public TextView getCharacter() {
        return character;
    }

    public TextView getDepartment() {
        return department;
    }

    public TextView getReleaseDate() {
        return releaseDate;
    }

}