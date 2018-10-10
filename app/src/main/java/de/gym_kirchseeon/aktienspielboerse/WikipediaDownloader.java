package de.gym_kirchseeon.aktienspielboerse;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/*
    The helper class for downloading (by now) only wikipedia abstracts of given companies.

    Run downloadDescription(Context context, String company) with context as your local context and
    company as the name of the company of which you want to fetch the description.
 */

public class WikipediaDownloader {

    JSONObject wikiresponse;

    public WikipediaDownloader(Context context) {


    }


    public JSONObject downloadDescription(Context context, String company) {
        String urlcompany = "";
        RequestQueue queue = Volley.newRequestQueue(context);

        try {
            urlcompany = URLEncoder.encode(company, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String url = "https://en.wikipedia.org/w/api.php?format=json&action=query&prop=extracts" +
                "&exintro&explaintext&redirects=1&titles=" + urlcompany;


        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        try {
                            wikiresponse = new JSONObject(response);
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });


        queue.add(stringRequest);

        if(wikiresponse == null) {
            try {
                wikiresponse = new JSONObject("Networking Error");
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            return wikiresponse;
        }
        else {
            return wikiresponse;
        }
    }
}

