package de.gym_kirchseeon.aktienspielboerse;

import org.json.JSONObject;

public interface AlphaVantageCallback {
    void onSuccess(JSONObject result);
}
