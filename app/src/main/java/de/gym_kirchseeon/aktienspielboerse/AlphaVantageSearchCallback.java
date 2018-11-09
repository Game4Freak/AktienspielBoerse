package de.gym_kirchseeon.aktienspielboerse;

import org.json.JSONObject;

public interface AlphaVantageSearchCallback {
    void onSuccessfulSearch(JSONObject companies);
}
