package com.example.tinysale.ui.viewmodel;

import com.example.tinysale.data.entity.Product;

public class CartItem {

    private final Product product;
    private final int quantity;
    private final double lineTotal;

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        this.lineTotal = product.getPrice() * quantity;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getLineTotal() {
        return lineTotal;
    }
}