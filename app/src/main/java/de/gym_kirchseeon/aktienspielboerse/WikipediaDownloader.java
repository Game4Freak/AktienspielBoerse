package de.gym_kirchseeon.aktienspielboerse;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
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

    private String extract = "";
    private RequestQueue queue;
    private JSONArray names;

    public WikipediaDownloader(Context context) {
        queue = Volley.newRequestQueue(context);

    }


    public void downloadDescription(String company, final ServerCallback callback) {
        String urlcompany = "";

        try {
            urlcompany = URLEncoder.encode(company, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String url = "https://en.wikipedia.org/w/api.php?format=json&action=query&prop=extracts" +
                "&exintro&explaintext&redirects=1&titles=" + urlcompany;

        if (urlcompany == null) {
            extract = "An url encoding error has occured.";
          //  return extract;
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                       /*     extract = response.getJSONObject("query").getJSONObject("pages")
                                    .getJSONObject(response.getJSONObject("query").getJSONObject("pages")
                                            .names().getString(0)).getString("extract");   */

                            names = response.getJSONObject("query").getJSONObject("pages").names();
                            extract = response.getJSONObject("query").getJSONObject("pages").getJSONObject(names.getString(0)).getString("extract");
                            callback.onSuccess(extract);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, null);


        queue.add(jsObjRequest);

      //  if (extract == null)    {
       //     extract = "Another unknown error occured.";
        }
       // return extract;
    }
//}