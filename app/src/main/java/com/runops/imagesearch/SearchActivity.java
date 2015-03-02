package com.runops.imagesearch;

import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.etsy.android.grid.StaggeredGridView;
import com.runops.imagesearch.adapter.EndlessScrollListener;
import com.runops.imagesearch.adapter.ResultArrayAdapter;
import com.runops.imagesearch.api.GoogleImageSearchApi;
import com.runops.imagesearch.model.MyPreferences;
import com.runops.imagesearch.model.ResponseData;
import com.runops.imagesearch.model.Result;

import java.io.Serializable;
import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class SearchActivity extends ActionBarActivity implements EditFilterDialog.EditFilterDialogListener {

    private ArrayList<Result> items;
    private ResultArrayAdapter resultArrayAdapter;
    private StaggeredGridView gridView;

    private String lastQueryString;

    private MyPreferences myPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        myPreferences = new MyPreferences();

        items = new ArrayList<Result>();
        resultArrayAdapter = new ResultArrayAdapter(this, items);

        gridView = (StaggeredGridView) findViewById(R.id.grid_view);
        gridView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                searchGoogleImages((page - 1) * 8);
            }
        });
        gridView.setAdapter(resultArrayAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (!s.equals(lastQueryString)) {
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
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        resultArrayAdapter.notifyDataSetChanged();
                        Toast.makeText(getApplicationContext(), "failure", Toast.LENGTH_SHORT).show();
                        Log.e("API", "request failure", error);
                    }
                });
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
            searchGoogleImages(0);
        }

    }
}
