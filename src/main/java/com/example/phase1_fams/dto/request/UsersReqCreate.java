package com.example.phase1_fams.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;



@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UsersReqCreate {
    @NotBlank(message = "Name must not be blank")
    private String name;
    @NotBlank(message = "Email must not be blank")
    private String email;

    @Size(min = 10, max = 12, message = "Phone number must be between 10 and 12 digits")
    @Pattern(regexp = "^\\d+$", message = "Phone number must be numeric")
    @NotBlank(message = "Phone must not be blank")
    private String phone;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Date of birth must not be null")
    private LocalDate dob;
    @NotBlank(message = "Gender must not be blank")
    private String gender;
    @NotNull(message = "Status must not be null")
    private boolean status;
    @NotNull(message = "Role must not be null")
    private int roleId;


}
