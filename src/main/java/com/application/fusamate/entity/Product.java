package com.application.fusamate.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@Entity
@Table(name = "product")
public class Product {
    @ToString.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @ToString.Include
    @NotBlank
    @Column(columnDefinition = "nvarchar(255)")
    private String name;

    @ToString.Include
    @Column(nullable = false)
    @NotNull
    private int gender;

    @ToString.Include
    @Column(nullable = false)
    @NotNull
    private int status = 0;

    @ToString.Include
    @NotBlank
    @Column(columnDefinition = "nvarchar(3000)")
    private String description;

    @ToString.Include
    @Column(nullable = false)
    @NotNull
    private int totalAmount;

    @ToString.Include
    @Column(nullable = false)
    @NotNull
    private int available = 0;

    @ToString.Include
    @JsonFormat(pattern = "dd/MM/yyyy hh:mm:ss")
    private Date createdAt = new Date();

    @ToString.Include
    @JsonFormat(pattern = "dd/MM/yyyy hh:mm:ss")
    private Date updatedAt = new Date();

    @ToString.Include
    @Column(length = 30)
    private String updatedBy;

    @ManyToOne(optional = false)
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    @ManyToOne(optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(optional = false)
    @JoinColumn(name = "made_in_id", nullable = false)
    private MadeIn madeIn;

    @OneToMany(mappedBy = "product")
    @JsonIgnore
    private List<PromotionProduct> listPromotionProduct = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "product")
    private List<ProductDetail> listProductDetail = new java.util.ArrayList<>();

    @OneToMany(mappedBy = "product")
    private List<ProductImage> listProductImage = new java.util.ArrayList<>();

}
