package com.example.tinysale.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.tinysale.data.dao.ProductDao;
import com.example.tinysale.data.db.AppDatabase;
import com.example.tinysale.data.entity.Product;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProductViewModel extends AndroidViewModel {

    private final ProductDao productDao;
    private final ExecutorService executorService;

    public ProductViewModel(@NonNull Application application) {
        super(application);

        AppDatabase db = AppDatabase.getInstance(application);
        productDao = db.productDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    // ---------- QUERIES ----------

    public LiveData<List<Product>> getAllProducts() {
        return productDao.getAllProducts();
    }

    public LiveData<Product> getProductBySku(String sku) {
        return productDao.getProductBySku(sku);
    }

    public LiveData<Product> getProductById(int id) {
        return productDao.getProductById(id);
    }

    // ---------- INSERT / UPDATE / DELETE ----------

    public void insert(Product product) {
        executorService.execute(() -> productDao.insert(product));
    }

    public void update(Product product) {
        executorService.execute(() -> productDao.update(product));
    }

    public void delete(Product product) {
        executorService.execute(() -> productDao.delete(product));
    }

    // ---------- INVENTORY ----------

    /** Decrement stock after a sale. */
    public void decrementStock(String sku, int amount) {
        executorService.execute(() -> productDao.decrementStock(sku, amount));
    }
}