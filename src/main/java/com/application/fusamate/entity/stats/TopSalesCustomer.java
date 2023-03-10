package com.application.fusamate.entity.stats;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "top_sales_customer")
public class TopSalesCustomer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private int year;

    private int month;

    private String email;

    private Double totalPayment;

    public TopSalesCustomer(int year, int month, String email, Double totalPayment) {
        this.year = year;
        this.month = month;
        this.email = email;
        this.totalPayment = totalPayment;
    }
}