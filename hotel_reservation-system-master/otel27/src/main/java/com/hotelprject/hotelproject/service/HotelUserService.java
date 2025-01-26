package com.hotelprject.hotelproject.service;

import com.hotelprject.hotelproject.model.Customer;

import java.util.List;
import java.util.Optional;

public interface HotelUserService {

    boolean existsByEmail(String email);
    void save(Customer customer);
    Optional<Customer> findByEmailAndPassword(String email, String password);
    Optional<Customer> findAdminUserByEmailAndPassword(String email, String password);
    List<Customer> findAll();
    Customer findById(Long id);
}