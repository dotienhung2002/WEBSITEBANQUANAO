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
@Table(name = "category")
public class Category {
    @ToString.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer id;

    @ToString.Include
    @NotBlank
    @Column(columnDefinition = "nvarchar(255)")
    private String name;

    @ToString.Include
    @Column(nullable = false)
    @NotNull
    private int status = 1;

    @ToString.Include
    @JsonFormat(pattern = "dd/MM/yyyy hh:mm:ss")
    private Date createdAt = new Date();

    @ToString.Include
    @JsonFormat(pattern = "dd/MM/yyyy hh:mm:ss")
    private Date updatedAt = new Date();

    @ToString.Include
    @NotBlank
    @Column(length = 30)
    private String updatedBy;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_set_id", nullable = false)
    private ProductSet productSet;

    @JsonIgnore
    @OneToMany(mappedBy = "category")
    private List<PromotionCategory> listPromotionCategory = new java.util.ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "category")
    private List<Product> listProduct = new java.util.ArrayList<>();
}