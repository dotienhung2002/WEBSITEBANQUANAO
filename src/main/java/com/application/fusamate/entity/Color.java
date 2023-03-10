package com.application.fusamate.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@Entity(name = "color")
public class Color {
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
    @Column(columnDefinition = "nvarchar(255)")
    private String description;

    @ToString.Include
    @Column(columnDefinition = "varchar(1000)")
    private String image;

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

    @JsonIgnore
    @OneToMany(mappedBy = "color")
    private List<ProductDetail> listProductDetail = new java.util.ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "color")
    private List<ComboDetail> listComboDetail = new java.util.ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "color")
    private List<ProductImage> listProductImage = new java.util.ArrayList<>();
}