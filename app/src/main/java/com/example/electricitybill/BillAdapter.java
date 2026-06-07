package com.example.electricitybill;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class BillAdapter extends ArrayAdapter<BillModel> {

    private final Context context;
    private final List<BillModel> bills;

    public BillAdapter(Context context, List<BillModel> bills) {
        super(context, 0, bills);
        this.context = context;
        this.bills = bills;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_bill, parent, false);
            holder = new ViewHolder();
            holder.tvMonth = convertView.findViewById(R.id.tvItemMonth);
            holder.tvFinalCost = convertView.findViewById(R.id.tvItemFinalCost);
            holder.tvUnit = convertView.findViewById(R.id.tvItemUnit);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        BillModel bill = bills.get(position);
        holder.tvMonth.setText(bill.getMonth());
        holder.tvFinalCost.setText(String.format("RM %.2f", bill.getFinalCost()));
        holder.tvUnit.setText(bill.getUnit() + " kWh");

        return convertView;
    }

    static class ViewHolder {
        TextView tvMonth;
        TextView tvFinalCost;
        TextView tvUnit;
    }
}
