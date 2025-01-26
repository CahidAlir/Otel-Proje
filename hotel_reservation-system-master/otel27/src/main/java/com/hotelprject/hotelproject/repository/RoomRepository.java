package com.hotelprject.hotelproject.repository;
import com.hotelprject.hotelproject.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
public interface RoomRepository extends JpaRepository<Product, Long > {
    List<Product> findAllByAvailableIsTrue();
}