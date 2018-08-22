package de.gym_kirchseeon.aktienspielboerse;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Don't use this class for your own purposes. Only used by RestClient.
 */

public class DownloadJsonTask extends AsyncTask<String, Void, JSONObject> {

    @Override
    protected JSONObject doInBackground(String... strings) {
        JSONObject result;
        try {
            URL url = new URL(strings[0]);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            InputStream stream = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder builder = new StringBuilder();

            String inputString;
            while ((inputString = bufferedReader.readLine()) != null) {
                builder.append(inputString);
            }

            result = new JSONObject(builder.toString());

            urlConnection.disconnect();

            return result;
        } catch (IOException | JSONException e) {
            Log.e("JSONException", e.getMessage());
        }
        return null; //TODO: ERSETZEN! nur für Kompilieren drinnen
    }
}
