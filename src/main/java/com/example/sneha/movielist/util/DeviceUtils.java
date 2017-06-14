package com.example.sneha.movielist.util;

import android.content.Context;
import android.content.res.Configuration;

import com.example.sneha.movielist.MovieListApplication;

public class DeviceUtils {
    private static final String TAG = DeviceUtils.class.getSimpleName();

    /**
     * finds whether a device is Tablet of Handset based on the screen size.
     * @param context
     * 			application context;
     * */
    public static boolean isTablet(Context context) {
        if(context != null)
        {
            return (context.getResources().getConfiguration().screenLayout
                    & Configuration.SCREENLAYOUT_SIZE_MASK)
                    >= Configuration.SCREENLAYOUT_SIZE_LARGE;
        }
        else
        {
            return (MovieListApplication.getContext().getResources().getConfiguration().screenLayout
                    & Configuration.SCREENLAYOUT_SIZE_MASK)
                    >= Configuration.SCREENLAYOUT_SIZE_LARGE;
        }
    }
}
