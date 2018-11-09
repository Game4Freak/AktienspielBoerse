package de.gym_kirchseeon.aktienspielboerse;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class AlphaVantageDownloader {

    private static String API_KEY = "UTDE555QI1GZPCU7"

    JSONObject resultCompanies;
    private RequestQueue queue;




    public void searchCompanyByName(String companyname, final AlphaVantageCallback callback) {

        String url = "https://www.alphavantage.co/query?function=SYMBOL_SEARCH&keywords=" + companyname + "&apikey=" + API_KEY + "&datatype=json";

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        resultCompanies = response;
                    }
                }, null);

        queue.add(jsObjRequest);
    }

}

