package com.hotelprject.hotelproject.service;

import com.hotelprject.hotelproject.model.Customer;
import com.hotelprject.hotelproject.model.enums.UserRole;
import com.hotelprject.hotelproject.repository.HotelUserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HotelUserServiceImpl implements HotelUserService{

    private final HotelUserRepository hotelUserRepository;

    public HotelUserServiceImpl(HotelUserRepository hotelUserRepository) {
        this.hotelUserRepository = hotelUserRepository;
    }

    public boolean existsByEmail(String email) {
        return hotelUserRepository.existsByEmail(email);
    }

    public void save(Customer customer) {
        hotelUserRepository.save(customer);
    }

    public Optional<Customer> findByEmailAndPassword(String email, String password) {
        return hotelUserRepository.findByEmailAndPassword(email, password);
    }

    public Optional<Customer> findAdminUserByEmailAndPassword(String email, String password) {
        return hotelUserRepository.findByEmailAndPasswordAndUserRole(email, password, UserRole.ADMIN);
    }

    @Override
    public List<Customer> findAll() {
        return hotelUserRepository.findAll();
    }

    @Override
    public Customer findById(Long id) {
        return hotelUserRepository.findById(id).orElse(null);
    }

}