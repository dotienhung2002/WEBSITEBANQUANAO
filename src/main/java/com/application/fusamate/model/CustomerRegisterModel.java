package com.application.fusamate.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import com.application.fusamate.configuration.Constants;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class CustomerRegisterModel  {
    @Email()
    @NotBlank()
    private String email;
    @NotBlank()
    private String name;
    @NotBlank()
    @Pattern(regexp = Constants.REGEX_PHONE)
    private String phone;
    @NotBlank()
    private String password;
}