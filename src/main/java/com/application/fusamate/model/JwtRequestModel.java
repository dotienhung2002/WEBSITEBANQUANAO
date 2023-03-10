package com.application.fusamate.model;
import com.application.fusamate.configuration.Constants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class JwtRequestModel implements Serializable{
    
    @NotBlank()@Size(min = 3,max = 30)
    private String username;
    @NotBlank() @Size(min = 8)
    @Pattern(regexp = Constants.REGEX_PASSWORD)
    private String password;
}
