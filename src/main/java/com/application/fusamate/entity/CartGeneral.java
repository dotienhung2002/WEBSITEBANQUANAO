package com.application.fusamate.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@Entity
@Table(name = "cart_general")
public class CartGeneral {

    @ToString.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Include
    @Column(unique = true)
    private String userAuthToken;

    @ToString.Include
    private boolean registeredUser;

    @OneToMany(mappedBy = "cartGeneral")
    private List<CartProduct> cartProducts = new ArrayList<>();

}
