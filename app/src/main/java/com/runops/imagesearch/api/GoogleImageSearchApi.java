package com.runops.imagesearch.api;

import com.runops.imagesearch.model.ResponseData;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public class GoogleImageSearchApi {
    private static GoogleImageSearchApiInterface googleImageSearchService;

    public static GoogleImageSearchApiInterface getGoogleImageSearchApiClient() {
        if (googleImageSearchService == null) {
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint("https://ajax.googleapis.com/ajax/services/search/")
                    .setRequestInterceptor(new RequestInterceptor() {
                        @Override
                        public void intercept(RequestFacade request) {
                            request.addQueryParam("v", "1.0");
                        }
                    })
                    .build();

            googleImageSearchService = restAdapter.create(GoogleImageSearchApiInterface.class);
        }

        return googleImageSearchService;
    }


    public interface GoogleImageSearchApiInterface {
        @GET("/images")
        void getResults(
                @Query("q") String query,
                Callback<ResponseData> callback
        );
    }
}
