package com.application.fusamate.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "customer_info")
public class CustomerInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "nvarchar(255)")
    private String name;

    @Column(columnDefinition = "nvarchar(1000)")
    private String address;

    @Column(columnDefinition = "nvarchar(100)")
    private String ward;

    @Column(columnDefinition = "nvarchar(100)")
    private String district;

    @Column(columnDefinition = "nvarchar(100)")
    private String province;

    @Column(length = 20)
    private String phone;

    private boolean isDefault;

    @ManyToOne()
    @JoinColumn(name = "customer_id",nullable = false)
    private Customer customer;
}
