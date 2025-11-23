package com.example.tinysale;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tinysale.ui.adapter.SaleAdapter;
import com.example.tinysale.ui.viewmodel.SaleViewModel;

public class SalesHistoryActivity extends AppCompatActivity {

    private SaleViewModel saleViewModel;
    private SaleAdapter saleAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_history);

        RecyclerView rvSales = findViewById(R.id.rvSales);
        rvSales.setLayoutManager(new LinearLayoutManager(this));

        saleAdapter = new SaleAdapter();
        rvSales.setAdapter(saleAdapter);

        saleViewModel = new ViewModelProvider(this).get(SaleViewModel.class);

        saleViewModel.getAllSales().observe(this, sales -> {
            if (sales != null) {
                saleAdapter.setSales(sales);
            }
        });
    }
}