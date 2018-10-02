package de.gym_kirchseeon.aktienspielboerse;

import android.app.Activity;
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

public class WikipediaDownloader {


    public WikipediaDownloader(Context context) {

        RequestQueue queue = Volley.newRequestQueue(context);
    }


    public JSONObject downloadDescription(String company) {
        String urlcompany = "";


        try {
            urlcompany = URLEncoder.encode(company, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
        }

        String url = "https://en.wikipedia.org/w/api.php?format=json&action=query&prop=extracts&exintro&explaintext&redirects=1&titles=" + urlcompany;


        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jobj = null;
                            jobj = new JSONObject(response);
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

        return new JSONObject(); // TODO Return muss entfernt werden und durch jobj aus der stringRequest ersetzt werden. stringRequest muss durch queue.add(stsringRequest) noch gestartet werden!
    }
}

