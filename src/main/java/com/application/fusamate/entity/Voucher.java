package com.application.fusamate.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Data
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@Entity
@Table(name = "voucher")
public class Voucher {
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
    private String code;

    @ToString.Include
    @Column(nullable = false)
    private Double money;

    @ToString.Include
    @Column(nullable = false)
    private Integer slot;

    @ToString.Include
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();

    @ToString.Include
    @Temporal(TemporalType.DATE)
    private Date startDate;

    @ToString.Include
    @Temporal(TemporalType.DATE)
    private Date endDate;

    @ToString.Include
    @Column(nullable = false)
    private int active;

    @ToString.Include
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt = new Date();

    @ToString.Include
    private String updatedBy;

    @ToString.Include
    @Column(columnDefinition = "nvarchar(1000)")
    private String note;

    @ManyToOne()
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne()
    @JoinColumn(name = "order_id")
    @JsonIgnore
    private Orders orders;
}
