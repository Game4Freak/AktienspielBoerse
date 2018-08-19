package de.gym_kirchseeon.aktienspielboerse;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SharesAdapter extends RecyclerView.Adapter<SharesAdapter.viewHolder> {

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.shares_recycler_row, parent, false);

        return new viewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        String change = "-1,5%";

        holder.companySharesNameTxt.setText("Unternehmen");
        holder.shareMultiplicationTxt.setText("5 x 200€");
        holder.changeTxt.setText(change);
        if (change.contains("+")) {
            holder.changeTxt.setTextColor(Color.parseColor("#00c853"));
        } else if (change.contains("-")) {
            holder.changeTxt.setTextColor(Color.parseColor("#d50000"));
        }
        holder.sharesSumWorthTxt.setText("1000€");
    }

    @Override
    public int getItemCount() {
        return 50;
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
