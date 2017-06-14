package com.example.sneha.movielist.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.example.sneha.movielist.R;
import com.example.sneha.movielist.fragment.ListFragment;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListFragment fragment = new ListFragment();
        pushFragment(fragment);
    }

    public void hideView(final View view) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.setVisibility(View.GONE);
            }
        });
    }

    /**
     * This method is used in MovieDetails
     *
     * @param view the view which we show.
     */
    public void showView(final View view) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.setVisibility(View.VISIBLE);
            }
        });
    }

    private void pushFragment(ListFragment fragment) {

        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
		transaction.addToBackStack(ListFragment.class.getSimpleName());
        transaction.commit();
    }

}
