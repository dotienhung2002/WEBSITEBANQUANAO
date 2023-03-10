package com.application.fusamate.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@Entity
@Table(name = "product_detail")
public class ProductDetail {
    @ToString.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @ToString.Include
    @Column(nullable = false)
    @NotNull
    private int availAmount;

    @ToString.Include
    @Column(nullable = false)
    @NotNull
    private double originPrice;

    @ToString.Include
    @Column(nullable = false)
    @NotNull
    private double promotionPercentage;

    @ToString.Include
    @Column(nullable = false)
    @NotNull
    private double promotionPrice;

    @ToString.Include
    @Column(nullable = false)
    private int status;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnore
    private Product product;

    @ManyToOne(optional = false)
    @JoinColumn(name = "size_id", nullable = false)
    private Size size;

    @ManyToOne(optional = false)
    @JoinColumn(name = "color_id", nullable = false)
    private Color color;

    @OneToMany(mappedBy = "productDetail")
    @JsonIgnore
    private List<ComboDetail> listComboDetail = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "productDetail")
    @JsonIgnore
    private List<CartProduct> cartProducts = new ArrayList<>();

    @OneToMany(mappedBy = "productDetail", orphanRemoval = true)
    @JsonIgnore
    private List<StatsProduct> statsProducts = new ArrayList<>();
}
