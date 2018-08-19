package de.gym_kirchseeon.aktienspielboerse;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CompanyAdapter extends RecyclerView.Adapter<CompanyAdapter.viewHolder> {

    private ArrayList<JSONObject> jObjList;

    public CompanyAdapter(ArrayList<JSONObject> jObjList) {
        this.jObjList = jObjList;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.company_recycler_row, parent, false);

        return new viewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        JSONObject jObj = jObjList.get(position);

        try {
            String name = jObj.getString("name");
            double worth = jObj.getDouble("worth");
            double change = jObj.getDouble("change");

            holder.companyNameTxt.setText(name);
            holder.shareWorthTxt.setText(String.format("%.2f€", worth));
            holder.changeCompaniesTxt.setText(String.format("%.1f%%", change));
            if (change > 0) {
                holder.changeCompaniesTxt.setTextColor(Color.parseColor("#00c853"));
            } else if (change < 0) {
                holder.changeCompaniesTxt.setTextColor(Color.parseColor("#d50000"));
            }
        } catch (JSONException e) {
            holder.companyNameTxt.setText("Fehler");
            holder.shareWorthTxt.setText("Fehler");
            holder.changeCompaniesTxt.setText("Fehler");
        }
    }

    @Override
    public int getItemCount() {
        if (jObjList != null) {
            return jObjList.size();
        }
        return 0;
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        public TextView companyNameTxt, shareWorthTxt, changeCompaniesTxt;

        public viewHolder(View view) {
            super(view);
            companyNameTxt = view.findViewById(R.id.companyNameTxt);
            shareWorthTxt = view.findViewById(R.id.shareWorthTxt);
            changeCompaniesTxt = view.findViewById(R.id.changeCompaniesTxt);
        }
    }
}
