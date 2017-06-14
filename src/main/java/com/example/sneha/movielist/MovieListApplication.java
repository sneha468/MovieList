package com.example.sneha.movielist;

import android.app.Application;
import android.content.Context;


public class MovieListApplication extends Application {
    private static Context mApplicationContext = null;
    public static final String url = "https://api.themoviedb.org/3/search/movie";
    public static final String key = "3ee68ad5da712fe0e8f776f737a89b9f";
    public static final String imageUrl = "https://image.tmdb.org/t/p/";


    @Override
    public void onCreate() {
        super.onCreate();

        mApplicationContext = getApplicationContext();
    }

    public static Context getContext(){
        return mApplicationContext;
    }

}
