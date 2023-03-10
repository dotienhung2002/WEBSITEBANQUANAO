package com.application.fusamate.entity.stats;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@Entity
@Table(name = "customer_growth_report")
public class CustomerGrowthReport implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer year;

    private Integer month;

    private Long count;

    public CustomerGrowthReport(Integer year, Integer month, Long count) {
        this.year = year;
        this.month = month;
        this.count = count;
    }
}
