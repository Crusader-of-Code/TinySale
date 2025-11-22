package com.example.tinysale.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "sales")
public class Sale {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "sku")
    private String sku;

    @ColumnInfo(name = "product_description")
    private String productDescription;

    // Base price before tax/discount (could be item price or subtotal)
    @ColumnInfo(name = "price")
    private double price;

    // ISO string like "2025-11-21T20:30"
    @ColumnInfo(name = "date")
    private String date;

    @ColumnInfo(name = "discount_percent")
    private double discountPercent;

    @ColumnInfo(name = "tax_rate")
    private double taxRate;

    // e.g. "CASH", "CARD", "ZELLE"
    @ColumnInfo(name = "payment_method")
    private String paymentMethod;

    // can be null if customer doesn't give one
    @ColumnInfo(name = "customer_email")
    private String customerEmail;

    // Final total after discount and tax
    @ColumnInfo(name = "final_sale_price")
    private double finalSalePrice;

    // --- Constructor ---
    public Sale(String sku,
                String productDescription,
                double price,
                String date,
                double discountPercent,
                double taxRate,
                String paymentMethod,
                String customerEmail,
                double finalSalePrice) {

        this.sku = sku;
        this.productDescription = productDescription;
        this.price = price;
        this.date = date;
        this.discountPercent = discountPercent;
        this.taxRate = taxRate;
        this.paymentMethod = paymentMethod;
        this.customerEmail = customerEmail;
        this.finalSalePrice = finalSalePrice;
    }

    // --- Getters & Setters ---
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(double discountPercent) {
        this.discountPercent = discountPercent;
    }

    public double getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(double taxRate) {
        this.taxRate = taxRate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public double getFinalSalePrice() {
        return finalSalePrice;
    }

    public void setFinalSalePrice(double finalSalePrice) {
        this.finalSalePrice = finalSalePrice;
    }
}