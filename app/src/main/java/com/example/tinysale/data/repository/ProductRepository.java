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
    private final LiveData<List<Product>> allProducts;
    private final ExecutorService executorService;

    public ProductRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        productDao = db.productDao();
        allProducts = productDao.getAllProducts();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<Product>> getAllProducts() {
        return allProducts;
    }

    public LiveData<Product> getProductBySku(String sku) {
        return productDao.getProductBySku(sku);
    }

    public LiveData<Product> getProductById(int id) {
        return productDao.getProductById(id);
    }

    public void insert(Product product) {
        executorService.execute(() -> productDao.insert(product));
    }

    public void update(Product product) {
        executorService.execute(() -> productDao.update(product));
    }

    public void delete(Product product) {
        executorService.execute(() -> productDao.delete(product));
    }
}