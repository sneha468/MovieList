package com.example.sneha.movielist.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sneha.movielist.R;
import com.example.sneha.movielist.helper.ItemClickCallback;
import com.example.sneha.movielist.model.MovieData;
import com.example.sneha.movielist.util.ImageUtils;
import com.example.sneha.movielist.viewholder.ContentItemViewHolder;

import java.util.ArrayList;

public class MovieAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private ArrayList<MovieData> moviesList;
    private int Resource;
    private Context mContext;
    private ItemClickCallback listener;

    public MovieAdapter(Context context, int resource, ArrayList<MovieData> objects, ItemClickCallback callback) {
        mContext = context;
        Resource = resource;
        moviesList = objects;
        listener = callback;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(Resource, parent, false);
        final RecyclerView.ViewHolder viewHolder = new ContentItemViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = viewHolder.getAdapterPosition();
                listener.onItemClicked(pos);
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ((ContentItemViewHolder)holder).getTitle().setText(moviesList.get(position).getTitle());

        if (moviesList.get(position).getReleaseDate() != null) {
            ((ContentItemViewHolder)holder).getReleaseDate().setText("(" + moviesList.get(position).getReleaseDate() + ")");
            ((ContentItemViewHolder)holder).getReleaseDate().setVisibility(View.VISIBLE);
        } else
            ((ContentItemViewHolder)holder).getReleaseDate().setVisibility(View.GONE);


        if (moviesList.get(position).getCharacter() != null) {
            ((ContentItemViewHolder)holder).getCharacter().setText(moviesList.get(position).getCharacter());
            ((ContentItemViewHolder)holder).getCharacter().setVisibility(View.VISIBLE);
        } else
            ((ContentItemViewHolder)holder).getCharacter().setVisibility(View.GONE);


        if (moviesList.get(position).getDepartmentAndJob() != null) {
            ((ContentItemViewHolder)holder).getDepartment().setText(moviesList.get(position).getDepartmentAndJob());
            ((ContentItemViewHolder)holder).getDepartment().setVisibility(View.VISIBLE);
        } else
            ((ContentItemViewHolder)holder).getDepartment().setVisibility(View.GONE);

        // if getPosterPath returns null imageLoader automatically sets default image
        ImageUtils.loadImage(((ContentItemViewHolder)holder).getPosterPath(), moviesList.get(position).getPosterPath());
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }

    public void setOnItemClickListener(ItemClickCallback callback){
        listener = callback;
    }

}
