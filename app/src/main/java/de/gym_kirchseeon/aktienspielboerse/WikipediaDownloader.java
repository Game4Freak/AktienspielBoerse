package de.gym_kirchseeon.aktienspielboerse;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * The helper class for downloading (by now) only wikipedia abstracts of given companies.
 */
public class WikipediaDownloader {

    private String extract = "";
    private RequestQueue queue;
    private String URL = "";

    public WikipediaDownloader(Context context) {
        queue = Volley.newRequestQueue(context);
    }

    /**
     * Downloads the description of the given company.
     *
     * @param company  The name of the company
     * @param language The language (e.g. en, de)
     * @param callback The interface to use the description
     */
    public void downloadDescription(String company, String language, final ServerCallback callback) {
        String urlcompany = "";

        try {
            urlcompany = URLEncoder.encode(company, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String url = "https://" + language + ".wikipedia.org/w/api.php?format=json&action=query&prop=extracts" +
                "&exintro&explaintext&redirects=1&titles=" + urlcompany;


        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            if (response.getJSONObject("query").getJSONObject("pages").names().getString(0).equals("-1")) {
                                callback.onError();
                            }
                            extract = response.getJSONObject("query").getJSONObject("pages")
                                    .getJSONObject(response.getJSONObject("query").getJSONObject("pages")
                                            .names().getString(0)).getString("extract");
                            URL = response.getJSONObject("query").getJSONObject("pages")
                                    .getJSONObject(response.getJSONObject("query").getJSONObject("pages")
                                            .names().getString(0)).getString("pageid");
                            callback.onSuccess(extract, URL);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, null);


        queue.add(jsObjRequest);
    }
}
