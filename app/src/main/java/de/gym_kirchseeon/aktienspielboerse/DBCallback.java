package de.gym_kirchseeon.aktienspielboerse;

import org.json.JSONObject;

public interface DBCallback {
    void onCompanyResult(JSONObject avresults);
}
