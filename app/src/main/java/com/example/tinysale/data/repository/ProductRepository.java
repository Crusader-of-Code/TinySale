package com.example.tinysale.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.tinysale.data.dao.ProductDao;
import com.example.tinysale.data.db.AppDatabase;
import com.example.tinysale.data.entity.Product;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProductRepository {

    private final ProductDao productDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public ProductRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        productDao = db.productDao();
    }

    public LiveData<List<Product>> getAllProducts() {
        return productDao.getAllProducts();
    }

    public LiveData<Product> getProductBySku(String sku) {
        return productDao.getProductBySku(sku);
    }

    public void insert(Product product) {
        executor.execute(() -> productDao.insert(product));
    }

    public void update(Product product) {
        executor.execute(() -> productDao.update(product));
    }

    public void delete(Product product) {
        executor.execute(() -> productDao.delete(product));
    }

    // NEW: decrement stock
    public void decrementStock(String sku, int quantity) {
        executor.execute(() -> productDao.decrementStock(sku, quantity));
    }
}