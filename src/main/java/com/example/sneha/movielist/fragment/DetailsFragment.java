package com.example.sneha.movielist.fragment;

import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sneha.movielist.MovieListApplication;
import com.example.sneha.movielist.R;
import com.example.sneha.movielist.activity.MainActivity;
import com.example.sneha.movielist.model.MovieData;
import com.example.sneha.movielist.util.ImageUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by Sneha on 6/13/2017.
 */

public class DetailsFragment  extends Fragment
{
    private MainActivity activity;
    private View rootView;
    private ImageView backDropPath;
    private int backDropCheck;
    private TextView titleText;
    private TextView releaseDate;
    private ImageView posterPath;
    private TextView tagline;
    private TextView statusText;
    private TextView runtime;
    private TextView genres;
    private TextView countries;
    private TextView companies;
    private RatingBar ratingBar;
    private TextView voteCount;
    private GridView movieDetailsSimilarGrid;
//    private ArrayList<SimilarModel> similarList;
    private View similarHolder;
    private ScrollView scrollView;
    private int currentId;
    private ProgressBar spinner;
    private HttpURLConnection conn;
    private JSONAsyncTask request;
    private int timeOut;

//    private MovieDetails movieDetails = new MovieDetails();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.detailsfragment, container, false);

        Bundle bundle = getArguments();
        if(bundle != null) {
            currentId = bundle.getInt("id");
        }

        activity = ((MainActivity) getActivity());
        backDropPath = (ImageView) rootView.findViewById(R.id.backDropPath);
        spinner = (ProgressBar) rootView.findViewById(R.id.progressBar);

        titleText = (TextView) rootView.findViewById(R.id.title);
        releaseDate = (TextView) rootView.findViewById(R.id.releaseDate);
        posterPath = (ImageView) rootView.findViewById(R.id.posterPath);
        tagline = (TextView) rootView.findViewById(R.id.tagline);
        statusText = (TextView) rootView.findViewById(R.id.status);
        runtime = (TextView) rootView.findViewById(R.id.runtime);
        genres = (TextView) rootView.findViewById(R.id.genres);
        countries = (TextView) rootView.findViewById(R.id.countries);
        companies = (TextView) rootView.findViewById(R.id.companies);
        ratingBar = (RatingBar) rootView.findViewById(R.id.ratingBar);
        voteCount = (TextView) rootView.findViewById(R.id.voteCount);

//        movieDetailsSimilarGrid = (GridView) rootView.findViewById(R.id.movieDetailsSimilarGrid);
        similarHolder = rootView.findViewById(R.id.similarHolder);
        scrollView = (ScrollView) rootView.findViewById(R.id.moviedetailsinfo);
        View detailsLayout = rootView.findViewById(R.id.detailsLayout);
        ViewCompat.setElevation(detailsLayout, 2 * getResources().getDisplayMetrics().density);

        // Prevent event bubbling else if you touch on the details layout when the info tab is scrolled it will open gallery view
        detailsLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        requestDetails();

        return rootView;


    }

    private void requestDetails() {
        request = new JSONAsyncTask();
        new Thread(new Runnable() {
            public void run() {
                try {
                    request.execute(MovieListApplication.url + "movie/" + currentId + "?append_to_response=releases%2Ctrailers%2Ccasts%2Cimages%2Csimilar&api_key=" + MovieListApplication.key).get(50000, TimeUnit.MILLISECONDS);
                } catch (TimeoutException | ExecutionException | InterruptedException | CancellationException e) {
                    request.cancel(true);
                    // we abort the http request, else it will cause problems and slow connection later
                    if (conn != null)
                        conn.disconnect();
                    if (spinner != null)
                        activity.hideView(spinner);
                    if (getActivity() != null && !(e instanceof CancellationException)) {
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getActivity(), getResources().getString(R.string.timeout), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    setTimeOut(1);
                }
            }
        });
    }

    class JSONAsyncTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(10000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();

                int status = conn.getResponseCode();

                if (status == 200) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    br.close();

                    final JSONObject jsonData = new JSONObject(sb.toString());

                    // is added checks if we are still on the same view, if we don't do this check the program will crash
                    if (isAdded()) {
                        // Backdrop path
                        if (!jsonData.getString("backdrop_path").equals("null") && !jsonData.getString("backdrop_path").isEmpty()) {
                            ImageUtils.loadImage(backDropPath, jsonData.getString("backdrop_path"));
                        } else if (!jsonData.getString("poster_path").equals("null") && !jsonData.getString("poster_path").isEmpty()) {
                            ImageUtils.loadImage(backDropPath, jsonData.getString("poster_path"));
                        }
                        // Title
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    titleText.setText(jsonData.getString("title"));
                                    if(!jsonData.getString("status").equals("null") && !jsonData.getString("status").isEmpty())
                                        statusText.setText(jsonData.getString("status"));
                                    if (!jsonData.getString("tagline").equals("null") && !jsonData.getString("tagline").isEmpty())
                                        tagline.setText("\"" + jsonData.getString("tagline") + "\"");
                                    // Release date
                                    if (!jsonData.getString("release_date").equals("null") && !jsonData.getString("release_date").isEmpty()) {
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                                        try {
                                            Date date = sdf.parse(jsonData.getString("release_date"));
                                            String formattedDate = DateFormat.getDateFormat(getContext()).format(date);
                                            releaseDate.setText("(" + formattedDate + ")");
                                        } catch (java.text.ParseException e) {
                                            releaseDate.setVisibility(View.GONE);
                                        }
                                    } else
                                        releaseDate.setVisibility(View.GONE);

                                    try {
                                        if (Integer.parseInt(jsonData.getString("runtime")) != 0)
                                            runtime.setText(jsonData.getString("runtime") + " " + "min");
                                        else {
                                            runtime.setVisibility(View.GONE);
                                        }
                                    } catch (NumberFormatException e) {
                                        runtime.setVisibility(View.GONE);
                                    }

                            } catch (JSONException e2) {
                                    e2.printStackTrace();
                                }
                            }});

                        return true;
                    }
                }


            } catch (ParseException | IOException | JSONException e) {
                if (conn != null)
                    conn.disconnect();
            } finally {
                if (conn != null)
                    conn.disconnect();
            }
            return false;
        }

        /**
         * Fired after doInBackground() has finished
         *
         * @param result true if connection is successful, false if connection has failed.
         */

        protected void onPostExecute(Boolean result) {
            // is added checks if we are still on the same view, if we don't do this check the program will cra
            if (isAdded()) {
                if (!result) {
                    Toast.makeText(getActivity(), R.string.noConnection, Toast.LENGTH_LONG).show();
                    setTimeOut(1);
                    spinner.setVisibility(View.GONE);
                } else {
                    setTimeOut(0);
                    spinner.setVisibility(View.GONE);

                }
            } else setTimeOut(1);


        }
    }

    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }

}


