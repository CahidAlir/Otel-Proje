package com.hotelprject.hotelproject.model;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userInfo;
    @ManyToOne
    private Product product;
    private LocalDate reservationDate;
    private LocalDate endDate;
    private String status;
}