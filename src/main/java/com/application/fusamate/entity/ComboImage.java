package com.application.fusamate.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@Entity
@Table(name = "combo_image")
public class ComboImage {

    @ToString.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer id;

    @ToString.Include
    @NotBlank
    @Column(columnDefinition = "varchar(1000)")
    private String image;

    @ManyToOne()
    @JoinColumn(name = "combo_detail_id", nullable = false)
    private ComboDetail comboDetail;

}
