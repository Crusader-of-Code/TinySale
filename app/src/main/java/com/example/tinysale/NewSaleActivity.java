package com.example.tinysale;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tinysale.data.entity.Product;
import com.example.tinysale.data.entity.Sale;
import com.example.tinysale.ui.adapter.CartAdapter;
import com.example.tinysale.ui.viewmodel.CartItem;
import com.example.tinysale.ui.viewmodel.ProductViewModel;
import com.example.tinysale.ui.viewmodel.SaleViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewSaleActivity extends AppCompatActivity {

    // UI
    private Button btnSelectProduct;
    private TextView tvSelectedProduct;
    private EditText etQuantity;
    private Button btnAddItem;
    private RecyclerView rvCartItems;
    private EditText etDiscountPercent;
    private EditText etTaxRate;
    private Spinner spinnerPaymentMethod;
    private EditText etCustomerEmail;
    private TextView tvSubtotal;
    private TextView tvFinalTotal;
    private Button btnCompleteSale;
    private EditText etSku;      // NEW: manual SKU field

    // ViewModels
    private ProductViewModel productViewModel;
    private SaleViewModel saleViewModel;

    // Request code for barcode scanner
    private static final int REQUEST_CODE_SCAN_BARCODE = 2001;

    // State
    private final List<Product> productList = new ArrayList<>();
    private Product selectedProduct = null;

    // Cart
    private final List<CartItem> cartItems = new ArrayList<>();
    private CartAdapter cartAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_sale);

        // ===== View References =====
        btnSelectProduct     = findViewById(R.id.btnSelectProduct);
        tvSelectedProduct    = findViewById(R.id.tvSelectedProduct);
        etQuantity           = findViewById(R.id.etQuantity);
        btnAddItem           = findViewById(R.id.btnAddItem);
        rvCartItems          = findViewById(R.id.rvCartItems);
        etDiscountPercent    = findViewById(R.id.etDiscountPercent);
        etTaxRate            = findViewById(R.id.etTaxRate);
        spinnerPaymentMethod = findViewById(R.id.spinnerPaymentMethod);
        etCustomerEmail      = findViewById(R.id.etCustomerEmail);
        tvSubtotal           = findViewById(R.id.tvSubtotal);
        tvFinalTotal         = findViewById(R.id.tvFinalTotal);
        btnCompleteSale      = findViewById(R.id.btnCompleteSale);
        etSku                = findViewById(R.id.etSku); // make sure this ID exists in XML

        // ===== Default Tax Rate (Orlando) =====
        etTaxRate.setText("6.5");

        // ===== Cart RecyclerView =====
        cartAdapter = new CartAdapter();
        rvCartItems.setLayoutManager(new LinearLayoutManager(this));
        rvCartItems.setAdapter(cartAdapter);

        // Swipe-to-delete for cart items
        ItemTouchHelper.SimpleCallback swipeCallback =
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView,
                                          RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        int position = viewHolder.getAdapterPosition();
                        if (position >= 0 && position < cartItems.size()) {
                            cartItems.remove(position);
                            cartAdapter.setItems(cartItems);
                            recalculateTotals();
                            Toast.makeText(NewSaleActivity.this,
                                    "Item removed from cart", Toast.LENGTH_SHORT).show();
                        }
                    }
                };

        new ItemTouchHelper(swipeCallback).attachToRecyclerView(rvCartItems);

        // ===== Payment Method Spinner =====
        ArrayAdapter<CharSequence> paymentAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.payment_methods,
                android.R.layout.simple_spinner_item
        );
        paymentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPaymentMethod.setAdapter(paymentAdapter);

        // ===== ViewModels =====
        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);
        saleViewModel = new ViewModelProvider(this).get(SaleViewModel.class);

        // Optional: load products (not required for scanning/typing to work)
        productViewModel.getAllProducts().observe(this, products -> {
            productList.clear();
            if (products != null) {
                productList.addAll(products);
            }
        });

        // ===== Scan Item Button =====
        btnSelectProduct.setText("Scan Item");
        btnSelectProduct.setOnClickListener(v -> {
            Intent intent = new Intent(NewSaleActivity.this, BarcodeScannerActivity.class);
            startActivityForResult(intent, REQUEST_CODE_SCAN_BARCODE);
        });

        // ===== Add Item to Cart =====
        btnAddItem.setOnClickListener(v -> addItemToCart());

        // ===== Complete Sale Button =====
        btnCompleteSale.setOnClickListener(v -> completeSale());

        // Initialize totals
        tvSubtotal.setText("Subtotal: $0.00");
        tvFinalTotal.setText("Total: $0.00");
    }

    // ===================== BARCODE RESULT =====================

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SCAN_BARCODE && resultCode == RESULT_OK && data != null) {
            String barcodeValue = data.getStringExtra(BarcodeScannerActivity.EXTRA_BARCODE_VALUE);

            if (barcodeValue != null && !barcodeValue.isEmpty()) {

                // Sync the typed SKU field with the scanned value
                etSku.setText(barcodeValue);

                // Lookup product by SKU == barcode
                productViewModel.getProductBySku(barcodeValue).observe(this, product -> {

                    if (product != null) {
                        selectedProduct = product;
                        tvSelectedProduct.setText(
                                selectedProduct.getProductDescription() +
                                        " - $" + String.format(Locale.getDefault(),
                                        "%.2f", selectedProduct.getPrice())
                        );
                    } else {
                        Toast.makeText(this,
                                "No product found with barcode: " + barcodeValue,
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    // ===================== CART LOGIC =====================

    private void addItemToCart() {

        // --- 1. Validate quantity first ---
        String qtyStr = etQuantity.getText().toString().trim();
        if (qtyStr.isEmpty()) {
            Toast.makeText(this, "Enter a quantity", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(qtyStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid quantity", Toast.LENGTH_SHORT).show();
            return;
        }

        if (quantity <= 0) {
            Toast.makeText(this, "Quantity must be greater than 0", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- 2. If we already have a selectedProduct (from scan or previous lookup), just use it ---
        if (selectedProduct != null) {
            addSelectedProductToCart(quantity);
            return;
        }

        // --- 3. Otherwise, try to look it up by typed SKU ---
        String typedSku = etSku.getText().toString().trim();
        if (typedSku.isEmpty()) {
            Toast.makeText(this,
                    "Scan a product or enter its SKU first",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        final int finalQuantity = quantity;

        productViewModel.getProductBySku(typedSku).observe(this, product -> {
            if (product != null) {
                selectedProduct = product;

                tvSelectedProduct.setText(
                        selectedProduct.getProductDescription() +
                                " - $" + String.format(Locale.getDefault(),
                                "%.2f", selectedProduct.getPrice())
                );

                addSelectedProductToCart(finalQuantity);
            } else {
                Toast.makeText(this,
                        "No product found with SKU: " + typedSku,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addSelectedProductToCart(int quantity) {
        CartItem item = new CartItem(selectedProduct, quantity);
        cartItems.add(item);
        cartAdapter.setItems(cartItems);

        recalculateTotals();

        // Clear quantity; keep SKU & selectedProduct so user can add again quickly
        etQuantity.setText("");
    }

    private void recalculateTotals() {
        double subtotal = 0.0;
        for (CartItem item : cartItems) {
            subtotal += item.getLineTotal();
        }

        String discountStr = etDiscountPercent.getText().toString().trim();
        String taxStr = etTaxRate.getText().toString().trim();

        double discountPercent = 0.0;
        double taxRate = 0.0;

        try {
            if (!discountStr.isEmpty()) {
                discountPercent = Double.parseDouble(discountStr);
            }
            if (!taxStr.isEmpty()) {
                taxRate = Double.parseDouble(taxStr);
            }
        } catch (NumberFormatException e) {
            // ignore here; completeSale() will do strict validation
        }

        double discountAmount = subtotal * (discountPercent / 100.0);
        double afterDiscount = subtotal - discountAmount;
        double taxAmount = afterDiscount * (taxRate / 100.0);
        double finalTotal = afterDiscount + taxAmount;

        tvSubtotal.setText(String.format(Locale.getDefault(), "Subtotal: $%.2f", subtotal));
        tvFinalTotal.setText(String.format(Locale.getDefault(), "Total: $%.2f", finalTotal));
    }

    // ===================== COMPLETE SALE / RECEIPT =====================

    private void completeSale() {
        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Add at least one item to the ticket", Toast.LENGTH_SHORT).show();
            return;
        }

        String discountStr   = etDiscountPercent.getText().toString().trim();
        String taxStr        = etTaxRate.getText().toString().trim();
        String customerEmail = etCustomerEmail.getText().toString().trim();
        String paymentMethod = spinnerPaymentMethod.getSelectedItem().toString();

        double discountPercent = 0.0;
        double taxRate = 0.0;

        if (!discountStr.isEmpty()) {
            try {
                discountPercent = Double.parseDouble(discountStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid discount percent", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (!taxStr.isEmpty()) {
            try {
                taxRate = Double.parseDouble(taxStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid tax percent", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Subtotal from cart
        double subtotal = 0.0;
        for (CartItem item : cartItems) {
            subtotal += item.getLineTotal();
        }

        double discountAmount = subtotal * (discountPercent / 100.0);
        double afterDiscount  = subtotal - discountAmount;
        double taxAmount      = afterDiscount * (taxRate / 100.0);
        double finalTotal     = afterDiscount + taxAmount;

        // Update UI
        tvSubtotal.setText(String.format(Locale.getDefault(), "Subtotal: $%.2f", subtotal));
        tvFinalTotal.setText(String.format(Locale.getDefault(), "Total: $%.2f", finalTotal));

        // Build receipt-style description
        StringBuilder receiptBuilder = new StringBuilder();
        receiptBuilder.append("Items:\n");
        for (CartItem item : cartItems) {
            Product p = item.getProduct();
            receiptBuilder.append(
                    String.format(
                            Locale.getDefault(),
                            "%dx %s @ $%.2f = $%.2f\n",
                            item.getQuantity(),
                            p.getProductDescription(),
                            p.getPrice(),
                            item.getLineTotal()
                    )
            );
        }
        receiptBuilder.append(String.format(Locale.getDefault(),
                "Subtotal: $%.2f\n", subtotal));

        if (discountPercent > 0) {
            receiptBuilder.append(String.format(Locale.getDefault(),
                    "Discount: %.1f%% (-$%.2f)\n", discountPercent, discountAmount));
        }

        receiptBuilder.append(String.format(Locale.getDefault(),
                "Tax (%.2f%%): $%.2f\n", taxRate, taxAmount));
        receiptBuilder.append(String.format(Locale.getDefault(),
                "Total: $%.2f\n", finalTotal));
        receiptBuilder.append("Payment: ").append(paymentMethod);

        if (!customerEmail.isEmpty()) {
            receiptBuilder.append("\nReceipt emailed to: ").append(customerEmail);
        }

        String receiptText = receiptBuilder.toString();

        // Date string
        String dateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                .format(new Date());

        String emailToStore = customerEmail.isEmpty() ? null : customerEmail;

        // Summary SKU can be first item SKU
        CartItem firstItem = cartItems.get(0);
        String summarySku = firstItem.getProduct().getSku();

        // We now store full receipt text in productDescription field
        Sale sale = new Sale(
                summarySku,
                receiptText,
                subtotal,
                dateStr,
                discountPercent,
                taxRate,
                paymentMethod,
                emailToStore,
                finalTotal
        );

        // Save sale
        saleViewModel.insert(sale);

        // Decrement stock for each product
        for (CartItem item : cartItems) {
            productViewModel.decrementStock(
                    item.getProduct().getSku(),
                    item.getQuantity()
            );
        }

        Toast.makeText(this, "Sale saved", Toast.LENGTH_SHORT).show();

        // Clear cart + fields
        cartItems.clear();
        cartAdapter.setItems(cartItems);
        etQuantity.setText("");
        etDiscountPercent.setText("");
        etTaxRate.setText("6.5");
        etCustomerEmail.setText("");
        etSku.setText("");
        selectedProduct = null;
        tvSelectedProduct.setText("No product selected");
        tvSubtotal.setText("Subtotal: $0.00");
        tvFinalTotal.setText("Total: $0.00");
    }
}