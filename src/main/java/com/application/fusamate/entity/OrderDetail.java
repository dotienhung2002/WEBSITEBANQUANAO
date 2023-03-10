package com.application.fusamate.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@Entity
@Table(name = "order_detail")
public class OrderDetail {
    @ToString.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @ToString.Include
    @Column(columnDefinition = "nvarchar(255)")
    private String name;

    @ToString.Include
    @Column(columnDefinition = "nvarchar(1000)")
    private String image;

    @ToString.Include
    @Column(columnDefinition = "nvarchar(255)")
    private String variant;

    @ToString.Include
    @Column(nullable = false)
    private int quantity;

    @ToString.Include
    @Column(nullable = false)
    private double listedPrice;

    @ToString.Include
    @Column(nullable = false)
    private double subPrice;

    @ManyToOne(optional = false)
    @JoinColumn(name = "order_id",nullable = false)
    @JsonIgnore
    private Orders orders;

    @OneToMany(mappedBy = "orderDetail", orphanRemoval = true)
    @JsonIgnore
    private List<StatsProduct> statsProducts = new ArrayList<>();
}