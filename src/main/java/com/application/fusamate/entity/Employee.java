package com.application.fusamate.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "employee")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String username;
    @JsonIgnore
    @NotBlank
    private String password;
    @Column(columnDefinition = "nvarchar(255)")
    private String name;
    private int gender;
    @Column(unique = true)
    private String phone;
    @Column(unique = true, length = 20)
    private String identityCard;
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date birthDay;
    private int role;
    @Column(columnDefinition = "nvarchar(255)")
    private String note;
    private int status;
    @Column(unique = true, length = 100)
    private String email;

    @Column(length = 1000)
    private String image;
    @JsonFormat(pattern = "dd/MM/yyyy hh:mm:ss")
    private Date createdAt = new Date();
    @JsonFormat(pattern = "dd/MM/yyyy hh:mm:ss")

    private Date updatedAt = new Date();
    @Column(length = 30)
    private String updatedBy;
}
