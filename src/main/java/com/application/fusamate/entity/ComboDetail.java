package com.application.fusamate.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "combo_detail")
public class ComboDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "color_id", nullable = false)
    private Color color;

    @ManyToOne()
    @JoinColumn(name = "product_detail_id", nullable = false)
    private ProductDetail productDetail;

    @ManyToOne()
    @JoinColumn(name = "combo_id", nullable = false)
    private Combo combo;

    @OneToMany(mappedBy = "comboDetail")
    @JsonIgnore
    private List<ComboImage> listComboImage = new java.util.ArrayList<>();
}