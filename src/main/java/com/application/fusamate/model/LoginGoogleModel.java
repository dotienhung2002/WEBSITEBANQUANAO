package com.application.fusamate.model;

import java.io.Serializable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LoginGoogleModel implements Serializable {
    @Email()
    @NotBlank()
    private String email;
    @NotBlank()

    private String password;
    @NotBlank()

    private String name;
}