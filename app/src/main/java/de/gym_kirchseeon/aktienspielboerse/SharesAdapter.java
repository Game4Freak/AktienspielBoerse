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

public class SharesAdapter extends RecyclerView.Adapter<SharesAdapter.viewHolder> {

    private ArrayList<JSONObject> jObjList;

    public SharesAdapter(ArrayList<JSONObject> jObjList) {
        this.jObjList = jObjList;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.shares_recycler_row, parent, false);

        return new viewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        JSONObject jObj = jObjList.get(position);

        try {
            String name = jObj.getString("name");
            double worth = jObj.getDouble("worth");
            double change = jObj.getDouble("change");
            int count = jObj.getInt("count");

            holder.companySharesNameTxt.setText(name);
            holder.sharesSumWorthTxt.setText(String.format("%.2f€", worth * count));
            holder.shareMultiplicationTxt.setText(String.format("%d x %.2f€", count, worth));
            holder.changeTxt.setText(String.format("%.1f%%", change));
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
        }
    }

    @Override
    public int getItemCount() {
        return jObjList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        public TextView companySharesNameTxt, shareMultiplicationTxt, changeTxt, sharesSumWorthTxt;

        public viewHolder(View view) {
            super(view);
            companySharesNameTxt = view.findViewById(R.id.companySharesNameTxt);
            shareMultiplicationTxt = view.findViewById(R.id.shareMultiplicationTxt);
            changeTxt = view.findViewById(R.id.changeTxt);
            sharesSumWorthTxt = view.findViewById(R.id.sharesSumWorthTxt);
        }
    }
}
