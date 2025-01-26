package com.hotelprject.hotelproject.repository;
import com.hotelprject.hotelproject.model.Customer;
import com.hotelprject.hotelproject.model.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface HotelUserRepository extends JpaRepository<Customer, Long> {
    boolean existsByEmail(String email);
    Optional<Customer> findByEmailAndPassword(String email, String password);
    Optional<Customer> findByEmailAndPasswordAndUserRole(String email, String password, UserRole userRole);
}