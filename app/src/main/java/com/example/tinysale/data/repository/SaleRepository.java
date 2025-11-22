package com.example.tinysale.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.tinysale.data.dao.SaleDao;
import com.example.tinysale.data.db.AppDatabase;
import com.example.tinysale.data.entity.Sale;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SaleRepository {

    private final SaleDao saleDao;
    private final LiveData<List<Sale>> allSales;
    private final ExecutorService executorService;

    public SaleRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        saleDao = db.saleDao();
        allSales = saleDao.getAllSales();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<Sale>> getAllSales() {
        return allSales;
    }

    public LiveData<Sale> getSaleById(int id) {
        return saleDao.getSaleById(id);
    }

    public LiveData<List<Sale>> getSalesByCustomerEmail(String email) {
        return saleDao.getSalesByCustomerEmail(email);
    }

    public void insert(Sale sale) {
        executorService.execute(() -> saleDao.insert(sale));
    }
}