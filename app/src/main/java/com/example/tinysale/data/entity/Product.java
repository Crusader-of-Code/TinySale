package com.example.tinysale.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "products")
public class Product {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "sku")
    private String sku;

    @ColumnInfo(name = "product_description")
    private String productDescription;

    @ColumnInfo(name = "price")
    private double price;

    @ColumnInfo(name = "stock_amount")
    private int stockAmount;

    // --- Constructor ---
    public Product(String sku, String productDescription, double price, int stockAmount) {
        this.sku = sku;
        this.productDescription = productDescription;
        this.price = price;
        this.stockAmount = stockAmount;
    }

    // --- Getters & Setters ---
    public int getId() {
        return id;
    }

    public void setId(int id) {  // set by Room when auto-generated
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

    public int getStockAmount() {
        return stockAmount;
    }

    public void setStockAmount(int stockAmount) {
        this.stockAmount = stockAmount;
    }
}