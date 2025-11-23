package com.example.tinysale;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import android.widget.Button;

import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tinysale.data.entity.Product;
import com.example.tinysale.ui.adapter.ProductAdapter;
import com.example.tinysale.ui.viewmodel.ProductViewModel;

public class ProductsActivity extends AppCompatActivity {

    private ProductViewModel productViewModel;
    private ProductAdapter adapter;

    private static final int REQUEST_CODE_SCAN_SKU = 3001;
    private EditText skuEditTextForScan;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        RecyclerView rvProducts = findViewById(R.id.rvProducts);
        View btnAddProduct = findViewById(R.id.btnAddProduct);

        adapter = new ProductAdapter();
        rvProducts.setLayoutManager(new LinearLayoutManager(this));
        rvProducts.setAdapter(adapter);

        productViewModel = new ViewModelProvider(this)
                .get(ProductViewModel.class);

        // Observe LiveData from ViewModel
        productViewModel.getAllProducts().observe(this, products -> {
            if (products != null) {
                adapter.setProducts(products);
            }
        });

        btnAddProduct.setOnClickListener(v -> showAddProductDialog());
    }

    private void showAddProductDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_add_product, null);

        EditText etSku = dialogView.findViewById(R.id.etDialogSku);
        EditText etDescription = dialogView.findViewById(R.id.etDialogDescription);
        EditText etPrice = dialogView.findViewById(R.id.etDialogPrice);
        EditText etStock = dialogView.findViewById(R.id.etDialogStock);
        Button btnScanSku = dialogView.findViewById(R.id.btnScanSku);

        // NEW: scan SKU button
        btnScanSku.setOnClickListener(v -> {
            skuEditTextForScan = etSku;  // remember which field to fill
            Intent intent = new Intent(ProductsActivity.this, BarcodeScannerActivity.class);
            startActivityForResult(intent, REQUEST_CODE_SCAN_SKU);
        });

        new AlertDialog.Builder(this)
                .setTitle("Add Product")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String sku = etSku.getText().toString().trim();
                    String description = etDescription.getText().toString().trim();
                    String priceStr = etPrice.getText().toString().trim();
                    String stockStr = etStock.getText().toString().trim();

                    if (sku.isEmpty() || description.isEmpty()
                            || priceStr.isEmpty() || stockStr.isEmpty()) {
                        Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    double price;
                    int stock;

                    try {
                        price = Double.parseDouble(priceStr);
                        stock = Integer.parseInt(stockStr);
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Invalid price or stock", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Product product = new Product(sku, description, price, stock);
                    productViewModel.insert(product);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SCAN_SKU && resultCode == RESULT_OK && data != null) {
            String barcodeValue = data.getStringExtra(BarcodeScannerActivity.EXTRA_BARCODE_VALUE);
            if (barcodeValue != null && !barcodeValue.isEmpty() && skuEditTextForScan != null) {
                skuEditTextForScan.setText(barcodeValue);
                // optional: move cursor to end
                skuEditTextForScan.setSelection(barcodeValue.length());
            }
            // clear reference so we don't accidentally reuse it
            skuEditTextForScan = null;
        }
    }
}