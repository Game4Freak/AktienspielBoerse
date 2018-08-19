package de.gym_kirchseeon.aktienspielboerse;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CompanyAdapter extends RecyclerView.Adapter<CompanyAdapter.viewHolder> {

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.company_recycler_row, parent, false);

        return new viewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        String change = "-1,5%";

        holder.companyNameTxt.setText("Unternehmen");
        holder.shareWorthTxt.setText("0€");
        holder.changeCompaniesTxt.setText(change);
        if (change.contains("+")) {
            holder.changeCompaniesTxt.setTextColor(Color.parseColor("#00c853"));
        } else if (change.contains("-")) {
            holder.changeCompaniesTxt.setTextColor(Color.parseColor("#d50000"));
        }
    }

    @Override
    public int getItemCount() {
        return 50;
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
