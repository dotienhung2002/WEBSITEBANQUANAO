package com.application.fusamate.entity.stats;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "top_sales_product")
public class TopSalesProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private int year;

    private int month;

    private Long productId;

    private String productName;

    private Long total_sales;

    private Double total_payment;

    public TopSalesProduct(int year, int month, Long productId, String productName, Long total_sales, Double total_payment) {
        this.year = year;
        this.month = month;
        this.productId = productId;
        this.productName = productName;
        this.total_sales = total_sales;
        this.total_payment = total_payment;
    }
}