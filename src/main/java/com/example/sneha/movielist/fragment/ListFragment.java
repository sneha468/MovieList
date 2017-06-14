package com.example.sneha.movielist.fragment;

import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.sneha.movielist.MovieListApplication;
import com.example.sneha.movielist.R;
import com.example.sneha.movielist.activity.MainActivity;
import com.example.sneha.movielist.adapter.MovieAdapter;
import com.example.sneha.movielist.helper.EndlessRecyclerViewScrollListener;
import com.example.sneha.movielist.helper.ItemClickCallback;
import com.example.sneha.movielist.helper.Scrollable;
import com.example.sneha.movielist.model.MovieData;
import com.example.sneha.movielist.util.DeviceUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class ListFragment extends Fragment implements ItemClickCallback{

    private MainActivity activity;
    private View rootView;
    private ArrayList<MovieData> moviesList;
    private int checkLoadMore = 0;
    private int totalPages;
    private MovieAdapter movieAdapter;
    private String mSearchString = "";
    private int backState;
    private String title;
    private MovieData movieData;
    private RecyclerView recyclerView;
    private EndlessRecyclerViewScrollListener mScrollListener;
    private ProgressBar spinner;
    private Toast toastLoadingMore;
    private HttpURLConnection conn;
    private boolean isLoading;
    private Bundle save;
    private boolean fragmentActive;
    private float scale;
    private RelativeLayout mSubmitLayout;
    private Button mSearchButton;
    private EditText mEditText;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.listfragment, container, false);
        spinner = (ProgressBar) view.findViewById(R.id.progressBar);
        toastLoadingMore = Toast.makeText(getContext(), R.string.loadingMore, Toast.LENGTH_SHORT);
        scale = getResources().getDisplayMetrics().density;

        activity = ((MainActivity) getActivity());

        mSearchButton = (Button) view.findViewById(R.id.search);
        mSubmitLayout = (RelativeLayout) view.findViewById(R.id.submit_layout);
        mEditText = (EditText) view.findViewById(R.id.search_edit_field);
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchButton.setVisibility(View.GONE);
                mSubmitLayout.setVisibility(View.VISIBLE);
            }
        });
        view.findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchString = mEditText.getText().toString();
                mEditText.setText("");
                mSearchButton.setVisibility(View.VISIBLE);
                mSubmitLayout.setVisibility(View.GONE);
                updateList();
            }
        });

        // Configure the RecyclerView
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        // Retain an instance so that you can call `resetState()` for fresh searches
        mScrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadNextDataFromApi(page);
            }
        };
        // Adds the scroll listener to RecyclerView
        recyclerView.addOnScrollListener(mScrollListener);

        return view;
    }

    private void pushFragment(int pos) {
        DetailsFragment fragment = new DetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("id", pos);
        fragment.setArguments(bundle);

        android.support.v4.app.FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
		transaction.addToBackStack(DetailsFragment.class.getSimpleName());
        transaction.commit();

    }

    public void updateList() {
            moviesList = new ArrayList<>();
            movieAdapter = new MovieAdapter(getActivity(), R.layout.row, moviesList, this);
            recyclerView.setAdapter(movieAdapter);
            movieAdapter.setOnItemClickListener(this);
            checkLoadMore = 0;
            final LoadMoviesAsyncTask request = new LoadMoviesAsyncTask();
            new Thread(new Runnable() {
                public void run() {
                    try {
                        if (!isLoading) {
                            request.execute(MovieListApplication.url + "?&api_key=" + MovieListApplication.key+ "&query=" + getSearchString()).get(50000, TimeUnit.MILLISECONDS);
                        }
                    } catch (TimeoutException | ExecutionException | InterruptedException e) {
                        request.cancel(true);
                        toastLoadingMore.cancel();
                        // we abort the http request, else it will cause problems and slow connection later
                        if (conn != null)
                            conn.disconnect();
                        isLoading = false;
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    if (spinner != null)
                                        spinner.setVisibility(View.GONE);
                                    Toast.makeText(getActivity(), getResources().getString(R.string.timeout), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }
            }).start();

    }

    public void loadNextDataFromApi(final int offset) {
        final LoadMoviesAsyncTask request = new LoadMoviesAsyncTask();
        new Thread(new Runnable() {
            public void run() {
                try {
                    request.execute(MovieListApplication.url  + "?&api_key=" + MovieListApplication.key + "&query=" + getSearchString()+ "&page=" + offset).get(50000, TimeUnit.MILLISECONDS);
                } catch (TimeoutException | ExecutionException | InterruptedException e) {
                    request.cancel(true);
                    // we abort the http request, else it will cause problems and slow connection later
                    if (conn != null)
                        conn.disconnect();
                    toastLoadingMore.cancel();
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getActivity(), getResources().getString(R.string.timeout), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        }).start();
    }

    @Override
    public void onItemClicked(int pos) {
        pushFragment(pos);
    }

    class LoadMoviesAsyncTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    if (checkLoadMore == 0) {
                        if (spinner != null)
                            spinner.setVisibility(View.VISIBLE);
                        isLoading = true;
                    } else {

                        toastLoadingMore.show();

                    }
                }});
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

                    JSONObject movieData = new JSONObject(sb.toString());
                    totalPages = movieData.getInt("total_pages");
                    JSONArray movieArray = movieData.getJSONArray("results");

                    // is added checks if we are still on the same view, if we don't do this check the program will crash
                    if (isAdded()) {
                        for (int i = 0; i < movieArray.length(); i++) {
                            JSONObject object = movieArray.getJSONObject(i);

                            MovieData movie = new MovieData();
                            movie.setId(object.getInt("id"));
                            movie.setTitle(object.getString("title"));
                            if (!object.getString("release_date").equals("null") && !object.getString("release_date").isEmpty())
                                movie.setReleaseDate(object.getString("release_date"));


                            if (!object.getString("poster_path").equals("null") && !object.getString("poster_path").isEmpty()){
                                String posterPath = MovieListApplication.imageUrl + /*getResources().getString(R.string.imageSize) +*/"w154"+ object.getString("poster_path");
                                Log.d("ListsFragment", "posterpath : "+posterPath);
                                movie.setPosterPath(posterPath);
                            }
                            moviesList.add(movie);
                        }

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

        @Override
        protected void onPostExecute(Boolean result) {
            // is added checks if we are still on the same view, if we don't do this check the program will cra
            if (isAdded()) {
                if (checkLoadMore == 0) {
                    spinner.setVisibility(View.GONE);
                    isLoading = false;
                }

                if (!result) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.noConnection), Toast.LENGTH_LONG).show();
                    backState = 0;
                } else {
                    movieAdapter.notifyDataSetChanged();
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            toastLoadingMore.cancel();
                        }
                    });
                    recyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            if (!DeviceUtils.isTablet(getContext()))
                                recyclerView.smoothScrollBy((int) (56 * scale), 0);
                            else
                                recyclerView.smoothScrollBy((int) (59 * scale), 0);
                        }
                    });

                    backState = 1;
                    save = null;
                }
            }
        }
    }



    public void setAdapter() {
        if (recyclerView != null && recyclerView.getAdapter() == null)
            recyclerView.setAdapter(movieAdapter);
    }

    public ArrayList<MovieData> getMoviesList() {
        return moviesList;
    }

    private String getSearchString() {
        return mSearchString;
    }
}
