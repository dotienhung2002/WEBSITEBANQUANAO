package com.application.fusamate.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Orders {
    @ToString.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @ToString.Include
    @Column(columnDefinition = "nvarchar(255)", nullable = false)
    private String name;

    @ToString.Include
    @Column(nullable = false)
    private String email;

    @ToString.Include
    @Column(nullable = false)
    private String phone;

    @ToString.Include
    @Column(columnDefinition = "nvarchar(1000)", nullable = false)
    private String address;

    @ToString.Include
    @Column(columnDefinition = "nvarchar(255)", nullable = false)
    private String ward;

    @ToString.Include
    @Column(columnDefinition = "nvarchar(255)", nullable = false)
    private String district;

    @ToString.Include
    @Column(columnDefinition = "nvarchar(255)", nullable = false)
    private String province;

    @ToString.Include
    @Column(nullable = false)
    private int shipType;

    @ToString.Include
    @Column(nullable = false)
    private int paymentType;

    @ToString.Include
    private Boolean paymentStatus;

    @ToString.Include
    private double voucher;

    @ToString.Include
    private double totalPrice;

    @ToString.Include
    @Column(nullable = false)
    private double shipCost;

    @ToString.Include
    private double totalPayment;

    @ToString.Include
    @Column(nullable = false)
    private int status;

    @ToString.Include
    @JsonFormat(pattern = "dd/MM/yyyy hh:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date confirmedAt;

    @ToString.Include
    @Column(columnDefinition = "nvarchar(1000)")
    private String note;

    @ToString.Include
    @JsonFormat(pattern = "dd/MM/yyyy hh:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();

    @ToString.Include
    @JsonFormat(pattern = "dd/MM/yyyy hh:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt = new Date();

    @ToString.Include
    private String updatedBy;

    @OneToMany(mappedBy = "orders")
    private List<OrderDetail> listOrderDetail = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "orders")
    private List<Voucher> listVoucher = new java.util.ArrayList<>();
}