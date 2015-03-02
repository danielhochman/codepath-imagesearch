package com.runops.imagesearch;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.etsy.android.grid.StaggeredGridView;
import com.runops.imagesearch.adapter.EndlessScrollListener;
import com.runops.imagesearch.adapter.ResultArrayAdapter;
import com.runops.imagesearch.api.GoogleImageSearchApi;
import com.runops.imagesearch.model.MyPreferences;
import com.runops.imagesearch.model.ResponseData;
import com.runops.imagesearch.model.Result;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class SearchActivity extends ActionBarActivity implements EditFilterDialog.EditFilterDialogListener {

    private ArrayList<Result> items;
    private ResultArrayAdapter resultArrayAdapter;
    private StaggeredGridView gridView;
    private ProgressBar progressBar;

    private String lastQueryString;

    private MyPreferences myPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        myPreferences = new MyPreferences();

        items = new ArrayList<Result>();
        resultArrayAdapter = new ResultArrayAdapter(this, items);

        progressBar = (ProgressBar) findViewById(R.id.progressBarSearch);


        gridView = (StaggeredGridView) findViewById(R.id.grid_view);
        gridView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                searchGoogleImages((page - 1) * 8);
            }
        });
        gridView.setAdapter(resultArrayAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Result item = items.get(position);

                Intent i = new Intent(SearchActivity.this, FullscreenActivity.class);
                i.putExtra("result", item);
                startActivity(i);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
                if (!s.equals(lastQueryString)) {
                    progressBar.setVisibility(View.VISIBLE);

                    // update cached query
                    lastQueryString = s;

                    // clear all items
                    items.clear();
                    resultArrayAdapter.notifyDataSetChanged();

                    // search!
                    searchGoogleImages(0);

                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        return true;
    }

    public void searchGoogleImages(Integer start) {

        if (isNetworkAvailable()) {
            GoogleImageSearchApi.getGoogleImageSearchApiClient().getResults(
                    lastQueryString, start, myPreferences.size, myPreferences.color, myPreferences.type, myPreferences.site,
                    new Callback<ResponseData>() {
                        @Override
                        public void success(ResponseData responseData, Response response) {
                            Log.i(this.getClass().toString(), "Fetched " + response.getUrl());
                            if (responseData != null && responseData.responseData != null && responseData.responseData.results != null) {
                                items.addAll(responseData.responseData.results);
                                resultArrayAdapter.notifyDataSetChanged();
                            }
                            progressBar.setVisibility(View.GONE);

                        }

                        @Override
                        public void failure(RetrofitError error) {
                            resultArrayAdapter.notifyDataSetChanged();
                            Toast.makeText(getApplicationContext(), "Request failure!", Toast.LENGTH_SHORT).show();
                            Log.e("API", "Request failure!", error);
                            progressBar.setVisibility(View.GONE);
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), "Network unavailable!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_filter) {
            FragmentManager fm = getSupportFragmentManager();
            EditFilterDialog editFilterDialog = EditFilterDialog.newInstance(myPreferences);
            editFilterDialog.show(fm, "fragment_edit_filter");
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFinishEditFilterDialog(MyPreferences currentPreferences) {
        if (!this.myPreferences.equals(currentPreferences)) {
            Log.i(this.getClass().toString(), "Updating preferences");
            this.myPreferences = currentPreferences;

            items.clear();
            resultArrayAdapter.notifyDataSetChanged();
            if (lastQueryString != null && !lastQueryString.trim().equals("")) {
                progressBar.setVisibility(View.VISIBLE);
                searchGoogleImages(0);
            }
        }

    }

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }
}
