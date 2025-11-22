package com.example.tinysale.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.tinysale.data.entity.Sale;
import com.example.tinysale.data.repository.SaleRepository;

import java.util.List;

public class SaleViewModel extends AndroidViewModel {

    private final SaleRepository repository;
    private final LiveData<List<Sale>> allSales;

    public SaleViewModel(@NonNull Application application) {
        super(application);
        repository = new SaleRepository(application);
        allSales = repository.getAllSales();
    }

    public LiveData<List<Sale>> getAllSales() {
        return allSales;
    }

    public LiveData<Sale> getSaleById(int id) {
        return repository.getSaleById(id);
    }

    public LiveData<List<Sale>> getSalesByCustomerEmail(String email) {
        return repository.getSalesByCustomerEmail(email);
    }

    public void insert(Sale sale) {
        repository.insert(sale);
    }
}