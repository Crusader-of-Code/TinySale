package com.example.tinysale;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    public void onProductsClicked(View view) {
        Intent intent = new Intent(this, ProductsActivity.class);
        startActivity(intent);
    }

    public void onNewSaleClicked(View view) {
        Intent intent = new Intent(this, NewSaleActivity.class);
        startActivity(intent);
    }

    public void onSalesHistoryClicked(View view) {
        Intent intent = new Intent(this, SalesHistoryActivity.class);
        startActivity(intent);
    }
}