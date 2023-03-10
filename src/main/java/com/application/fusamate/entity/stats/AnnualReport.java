package com.application.fusamate.entity.stats;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@Entity
@Table(name = "annual_report")
public class AnnualReport implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int year;

    private int month;

    private double revenue;

    private long orderCount;

    private long customerCount;

    private long productCount;

    public AnnualReport(int year, int month, double revenue, long orderCount, long customerCount, long productCount) {
        this.year = year;
        this.month = month;
        this.revenue = revenue;
        this.orderCount = orderCount;
        this.customerCount = customerCount;
        this.productCount = productCount;
    }
}
