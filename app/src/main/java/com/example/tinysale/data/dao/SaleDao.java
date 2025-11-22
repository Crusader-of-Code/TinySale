package com.example.tinysale.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.tinysale.data.entity.Sale;

import java.util.List;

@Dao
public interface SaleDao {

    @Insert
    void insert(Sale sale);

    @Query("SELECT * FROM sales ORDER BY date DESC")
    LiveData<List<Sale>> getAllSales();

    @Query("SELECT * FROM sales WHERE id = :id LIMIT 1")
    LiveData<Sale> getSaleById(int id);

    @Query("SELECT * FROM sales WHERE customer_email = :email ORDER BY date DESC")
    LiveData<List<Sale>> getSalesByCustomerEmail(String email);
}