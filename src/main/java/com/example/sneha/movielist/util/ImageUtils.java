package com.example.sneha.movielist.util;

import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.sneha.movielist.MovieListApplication;
import com.example.sneha.movielist.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Callback;

public class ImageUtils {
    private static final String TAG = ImageUtils.class.getSimpleName();

    private static void loadImagePicasso(final ImageView view, final ProgressBar progressBar, final String imageUrl, final float rotation) {
        if(!TextUtils.isEmpty(imageUrl)) {
            try {
                Picasso.with(MovieListApplication.getContext())
                        .load(imageUrl)
                        .rotate(rotation)
                        .placeholder(R.drawable.tmdb)
                        .into(view, new Callback() {
                            @Override
                            public void onSuccess() {
                                if (progressBar != null) progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError() {
                                Log.e(TAG, "Error loading media: " + imageUrl);
                                if (progressBar != null) progressBar.setVisibility(View.GONE);
                                view.setImageDrawable(ContextCompat.getDrawable(MovieListApplication.getContext(), R.drawable.tmdb));
                            }
                        });

            } catch (Exception exc) {
                Log.e(TAG, "Error loading image. Attempting to set the media placeholder.", exc);
                view.setImageDrawable(ContextCompat.getDrawable(MovieListApplication.getContext(), R.drawable.tmdb));
            }
        }
        else{
            view.setImageDrawable(ContextCompat.getDrawable(MovieListApplication.getContext(), R.drawable.tmdb));
        }
    }

    public static void loadImage(final ImageView view, final String imageUrl){
        loadImagePicasso(view, null, imageUrl, 0f);
    }

    public static void loadImage(final ImageView view, final String imageUrl, float rotation){
        loadImagePicasso(view, null, imageUrl, rotation);
    }

    public static String getThumbnailUrl(final String parameterUrl, boolean isPhoto, boolean isTablet){
        DisplayMetrics metrics = MovieListApplication.getContext().getResources().getDisplayMetrics();
        char marker = (isPhoto) ? '-' : 'x'; //if it is coming from USAToday, use the - otherwise the CDN uses the x
        String res = (isTablet) ? "263M148" : "263M148";
        switch (metrics.densityDpi) {
            case DisplayMetrics.DENSITY_MEDIUM:
                res = (isTablet) ? "130M73" : "130M73";
                break;
            case DisplayMetrics.DENSITY_HIGH:
                res = (isTablet) ? "263M148" : "263M148";
                break;
            case DisplayMetrics.DENSITY_XHIGH:
            case DisplayMetrics.DENSITY_400:
                res = (isTablet) ? "390M220" : "357M201";
                break;
            case DisplayMetrics.DENSITY_XXHIGH:
            case DisplayMetrics.DENSITY_560:
            case DisplayMetrics.DENSITY_XXXHIGH:
                res = (isTablet) ? "480M270" : "357M201";
                break;
        }

        res = res.replace('M', marker);

        return parameterUrl.replace("[SIZE]", res);
    }
}

