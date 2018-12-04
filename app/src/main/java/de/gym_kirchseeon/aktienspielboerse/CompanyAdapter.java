package de.gym_kirchseeon.aktienspielboerse;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * The adapter to load shares into a Recycler
 */
public class CompanyAdapter extends RecyclerView.Adapter<CompanyAdapter.viewHolder> implements Filterable {

    public final static int SORT_ALPHABETICALLY = 0;
    public final static int SORT_ALPHABETICALLY_REVERSE = 1;
    public final static int SORT_BY_WORTH = 2;
    public final static int SORT_BY_WORTH_REVERSE = 3;
    public final static int SORT_BY_COUNT = 4;
    public final static int SORT_BY_COUNT_REVERSE = 5;
    public final static int SORT_BY_SUM_WORTH = 6;
    public final static int SORT_BY_SUM_WORTH_REVERSE = 7;

    public final static int MAIN_ACTIVITY = 0;
    public final static int SHARES_ACTIVITY = 1;

    private final String keyName;
    private final String keyWorth;
    private final String keyChange;
    private final String keyCount;
    private final String keyCompany;
    private CustomFilter filter;
    private ArrayList<JSONObject> jObjList;
    private JSONArray jObjArray;
    private int sorting;
    private int context;


    public CompanyAdapter(Context context, int contextInt, JSONObject jObj) {
        this.context = contextInt;

        keyName = context.getResources().getString(R.string.nameCompany);
        keyWorth = context.getResources().getString(R.string.currentWorthCompany);
        keyChange = context.getResources().getString(R.string.changeCompany);
        keyCount = context.getResources().getString(R.string.countCompany);
        keyCompany = context.getResources().getString(R.string.company);

        jObjList = new ArrayList<>();

        try {
            jObjArray = (JSONArray) jObj.get(keyCompany);
            for (int i = 0; i < jObjArray.length(); i++) {
                jObjList.add(jObjArray.getJSONObject(i));
            }
        } catch (JSONException e) {
            Log.e("JSONException", e.getMessage());
        }

        sorting = SORT_ALPHABETICALLY;

        sort(sorting);
    }

    public JSONObject getCompany(String name, double worth) {
        for (int i = 0; i < jObjArray.length(); i++) {
            try {
                JSONObject testFor = jObjArray.getJSONObject(i);
                if (testFor.getString(keyName).equals(name) && Math.abs(testFor.getDouble(keyWorth) - worth) < 0.01) {
                    return testFor;
                }
            } catch (JSONException e) {
                Log.e("JSONException", e.getMessage());
            }
        }
        return null;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        if (context == MAIN_ACTIVITY) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.shares_recycler_row, parent, false);
        } else if (context == SHARES_ACTIVITY) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.company_recycler_row, parent, false);
        } else {
            itemView = null;
        }

        return new viewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        JSONObject jObj = jObjList.get(position);

        if (context == MAIN_ACTIVITY) {
            try {
                String name = jObj.getString(keyName);
                double worth = jObj.getDouble(keyWorth);
                double change = jObj.getDouble(keyChange);
                int count = jObj.getInt(keyCount);

                holder.companySharesNameTxt.setText(name);
                holder.sharesSumWorthTxt.setText(String.format("%.2f€", worth * count));
                holder.shareMultiplicationTxt.setText(String.format("%d x %.2f€", count, worth));
                holder.changeTxt.setText(String.format("%+.1f%%", change));
                if (change > 0) {
                    holder.changeTxt.setTextColor(Color.parseColor("#00c853"));
                } else if (change < 0) {
                    holder.changeTxt.setTextColor(Color.parseColor("#d50000"));
                }
            } catch (JSONException e) {
                holder.companySharesNameTxt.setText("Fehler");
                holder.sharesSumWorthTxt.setText("Fehler");
                holder.shareMultiplicationTxt.setText("Fehler");
                holder.changeTxt.setText("Fehler");
                Log.e("JSONException", e.getMessage());
            }
        } else if (context == SHARES_ACTIVITY) {
            try {
                String name = jObj.getString(keyName);
                double worth = jObj.getDouble(keyWorth);
                double change = jObj.getDouble(keyChange);
                int count = jObj.getInt(keyCount);

                holder.companyNameTxt.setText(name);
                holder.shareWorthTxt.setText(String.format("%.2f€", worth));
                holder.shareCountTxt.setText(String.format("%d", count));
                holder.changeCompaniesTxt.setText(String.format("%+.1f%%", change));
                if (change > 0) {
                    holder.changeCompaniesTxt.setTextColor(Color.parseColor("#00c853"));
                } else if (change < 0) {
                    holder.changeCompaniesTxt.setTextColor(Color.parseColor("#d50000"));
                }
            } catch (JSONException e) {
                holder.companyNameTxt.setText("Fehler");
                holder.shareWorthTxt.setText("Fehler");
                holder.shareCountTxt.setText("Fehler");
                holder.changeCompaniesTxt.setText("Fehler");
                Log.e("JSONException", e.getMessage());
            }
        }
    }

    @Override
    public int getItemCount() {
        if (jObjList != null) {
            return jObjList.size();
        }
        return 0;
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new CustomFilter(this, jObjList);
        }
        return filter;
    }

    /**
     * Sorts the Recycler
     *
     * @param sorting by what to sort
     */
    public void sort(int sorting) {
        this.sorting = sorting;
        if (jObjList.size() > 1) {
            switch (sorting) {
                case SORT_ALPHABETICALLY:
                    sortAlphabetically();
                    break;
                case SORT_ALPHABETICALLY_REVERSE:
                    sortAlphabeticallyReverse();
                    break;
                case SORT_BY_WORTH:
                    sortByWorth();
                    break;
                case SORT_BY_WORTH_REVERSE:
                    sortByWorthReverse();
                    break;
                case SORT_BY_COUNT:
                    sortByCount();
                    break;
                case SORT_BY_COUNT_REVERSE:
                    sortByCountReverse();
                    break;
                case SORT_BY_SUM_WORTH:
                    sortBySumWorth();
                    break;
                case SORT_BY_SUM_WORTH_REVERSE:
                    sortBySumWorthReverse();
                    break;
            }
        }
        notifyDataSetChanged();
    }

    /**
     * Sorts alphabetically
     */
    private void sortAlphabetically() {
        Collections.sort(jObjList, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject j1, JSONObject j2) {
                try {
                    return j1.getString(keyName).compareToIgnoreCase(j2.getString(keyName));
                } catch (JSONException e) {
                    Log.e("JSONException", e.getMessage());
                    return 0;
                }
            }
        });
    }

    /**
     * Sorts reverse alphabetically
     */
    private void sortAlphabeticallyReverse() {
        Collections.reverse(jObjList);
    }

    /**
     * Sorts by the lowest single worth
     */
    private void sortByWorth() {
        sortByWorthReverse();
        Collections.reverse(jObjList);
    }

    /**
     * Sorts by the highest single worth
     */
    private void sortByWorthReverse() {
        Collections.sort(jObjList, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject j1, JSONObject j2) {
                try {
                    return Double.compare(j1.getDouble(keyWorth), j2.getDouble(keyWorth));
                } catch (JSONException e) {
                    Log.e("JSONException", e.getMessage());
                    return 0;
                }
            }
        });
    }

    /**
     * Sorts by the highest sum worth
     */
    private void sortBySumWorth() {
        sortBySumWorthReverse();
        Collections.reverse(jObjList);
    }

    /**
     * Sorts by the lowest sum worth
     */
    private void sortBySumWorthReverse() {
        Collections.sort(jObjList, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject j1, JSONObject j2) {
                try {
                    return Double.compare(j1.getDouble(keyWorth) * j1.getDouble(keyCount), j2.getDouble(keyWorth) * j2.getDouble(keyCount));
                } catch (JSONException e) {
                    Log.e("JSONException", e.getMessage());
                    return 0;
                }
            }
        });
    }

    /**
     * Sorts by the highest count
     */
    private void sortByCount() {
        sortByCountReverse();
        Collections.reverse(jObjList);
    }

    /**
     * Sorts by the lowest count
     */
    private void sortByCountReverse() {
        Collections.sort(jObjList, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject j1, JSONObject j2) {
                try {
                    return Double.compare(j1.getDouble(keyCount), j2.getDouble(keyCount));
                } catch (JSONException e) {
                    Log.e("JSONException", e.getMessage());
                    return 0;
                }
            }
        });
    }

    /**
     * Filter to search companies in the Recycler
     */
    private static class CustomFilter extends Filter {

        private final CompanyAdapter adapter;
        private final List<JSONObject> originalList;
        private final List<JSONObject> filteredList;

        private CustomFilter(CompanyAdapter adapter, List<JSONObject> originalList) {
            super();
            this.adapter = adapter;
            this.originalList = new LinkedList<>(originalList);
            this.filteredList = new ArrayList<>();
        }

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            filteredList.clear();
            final FilterResults results = new FilterResults();

            if (charSequence.length() == 0) {
                filteredList.addAll(originalList);
            } else {
                final String filterPattern = charSequence.toString().toLowerCase().trim();
                for (int i = 0; i < originalList.size(); i++) {
                    try {
                        JSONObject item = originalList.get(i);
                        String itemName = item.getString(adapter.keyName);
                        if (itemName.toLowerCase().contains(filterPattern)) {
                            filteredList.add(item);
                        }
                    } catch (JSONException e) {
                        Log.e("JSONException", e.getMessage());
                    }
                }
            }

            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            adapter.jObjList = (ArrayList) filterResults.values;
            adapter.notifyDataSetChanged();
        }
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        public TextView companyNameTxt, shareWorthTxt, changeCompaniesTxt, shareCountTxt, companySharesNameTxt, shareMultiplicationTxt, changeTxt, sharesSumWorthTxt;


        public viewHolder(View view) {
            super(view);

            if (context == MAIN_ACTIVITY) {
                companySharesNameTxt = view.findViewById(R.id.companySharesNameTxt);
                shareMultiplicationTxt = view.findViewById(R.id.shareMultiplicationTxt);
                changeTxt = view.findViewById(R.id.changeTxt);
                sharesSumWorthTxt = view.findViewById(R.id.sharesSumWorthTxt);
            } else if (context == SHARES_ACTIVITY) {
                companyNameTxt = view.findViewById(R.id.companyNameTxt);
                shareWorthTxt = view.findViewById(R.id.shareWorthTxt);
                shareCountTxt = view.findViewById(R.id.shareCountTxt);
                changeCompaniesTxt = view.findViewById(R.id.changeCompaniesTxt);
            }
        }
    }

    public void changeData(JSONObject jObj) {
        try {
            jObjArray = (JSONArray) jObj.get(keyCompany);
            for (int i = 0; i < jObjArray.length(); i++) {
                jObjList.add(jObjArray.getJSONObject(i));
            }
        } catch (JSONException e) {
            Log.e("JSONException", e.getMessage());
        }
        notifyDataSetChanged();
    }
}
