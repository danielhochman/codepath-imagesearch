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
import com.runops.imagesearch.adapter.ResultArrayAdapter;
import com.runops.imagesearch.api.GoogleImageSearchApi;
import com.runops.imagesearch.model.ResponseData;
import com.runops.imagesearch.model.Result;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class SearchActivity extends ActionBarActivity {

    private ArrayList<Result> items;
    private ResultArrayAdapter resultArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        items = new ArrayList<Result>();
        resultArrayAdapter = new ResultArrayAdapter(this, items);

        StaggeredGridView gridView = (StaggeredGridView) findViewById(R.id.grid_view);
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
                searchGoogleImages(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        return true;
    }

    public void searchGoogleImages(String query) {
        GoogleImageSearchApi.getGoogleImageSearchApiClient().getResults(
                query, new Callback<ResponseData>() {
                    @Override
                    public void success(ResponseData responseData, Response response) {
                        items.clear();
                        items.addAll(responseData.responseData.results);
                        resultArrayAdapter.notifyDataSetChanged();

                        Toast.makeText(getApplicationContext(), "success!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void failure(RetrofitError error) {
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
            EditFilterDialog editFilterDialog = EditFilterDialog.newInstance(null);
            editFilterDialog.show(fm, "fragment_edit_filter");
        }

        return super.onOptionsItemSelected(item);
    }
}
