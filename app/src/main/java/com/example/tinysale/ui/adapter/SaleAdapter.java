package com.example.tinysale.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tinysale.R;
import com.example.tinysale.data.entity.Sale;

import java.util.ArrayList;
import java.util.List;

public class SaleAdapter extends RecyclerView.Adapter<SaleAdapter.SaleViewHolder> {

    private List<Sale> sales = new ArrayList<>();

    @NonNull
    @Override
    public SaleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sale, parent, false);
        return new SaleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SaleViewHolder holder, int position) {
        Sale sale = sales.get(position);

        holder.tvDate.setText(sale.getDate());
        holder.tvTotal.setText(String.format("$%.2f", sale.getFinalSalePrice()));

        // productDescription now holds the full receipt-style text
        String receiptText = sale.getProductDescription();
        holder.tvReceiptPreview.setText(receiptText);
    }

    @Override
    public int getItemCount() {
        return sales.size();
    }

    public void setSales(List<Sale> sales) {
        this.sales = sales;
        notifyDataSetChanged();
    }

    static class SaleViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvTotal, tvReceiptPreview;

        SaleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvSaleDate);
            tvTotal = itemView.findViewById(R.id.tvSaleTotal);
            tvReceiptPreview = itemView.findViewById(R.id.tvSaleReceiptPreview);
        }
    }
}