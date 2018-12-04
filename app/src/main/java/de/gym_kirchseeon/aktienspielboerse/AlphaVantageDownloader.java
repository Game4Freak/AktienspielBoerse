package de.gym_kirchseeon.aktienspielboerse;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class AlphaVantageDownloader {

    private static String API_KEY = "UTDE555QI1GZPCU7";
    private static String BASE_URL = "https://www.alphavantage.co/query?function=";

    JSONObject resultCompanies;
    private RequestQueue queue;


    public AlphaVantageDownloader(Context context) {
        queue = Volley.newRequestQueue(context);
    }



    public void searchCompanyByName(String companyname, final AlphaVantageSearchCallback callback) {

        /**
         * Downloads the search results for a given char sequence. Result via AlphaVantageCallback interface
         * class as JSONObject.
         *
         * @param companyname
         */


        String url = BASE_URL + "SYMBOL_SEARCH&keywords=" + companyname + "&apikey=" + API_KEY + "&datatype=json";

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        callback.onSuccessfulSearch(response);
                    }
                }, null);

        queue.add(jsObjRequest);
    }

    public void getTimeSeriesBySymbol(String symbol, final AlphaVantageCallback callback) {

        String url = BASE_URL + "TIME_SERIES_DAILY&symbol=" + symbol + "&outputsize=compact&apikey=" + API_KEY;

        JsonObjectRequest timeSeriesRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onSuccessfulTimeSeries(response);
                    }
                }, null);

        queue.add(timeSeriesRequest);
    }

}

