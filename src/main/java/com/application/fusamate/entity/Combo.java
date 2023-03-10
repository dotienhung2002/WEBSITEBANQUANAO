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
@Table(name = "combo")
public class Combo {
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
    @NotNull
    @Column(nullable = false)
    private double origin_price;

    @ToString.Include
    @NotNull
    @Column(nullable = false)
    private double sale_percentage;

    @ToString.Include
    @NotNull
    @Column(nullable = false)
    private double sale_price;

    @ManyToOne()
    @JoinColumn(name = "combo_set_id", nullable = false)
    private ComboSet comboSet;

    @OneToMany(mappedBy = "combo")
    @JsonIgnore
    private List<ComboDetail> listComboDetail = new java.util.ArrayList<>();
}