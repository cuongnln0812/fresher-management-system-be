package com.example.phase1_fams.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PasswordChangeReq {
    @NotBlank(message = "Old password must not be blank")
    private String oldPassword;
    @NotBlank(message = "New password must not be blank")
    private String newPassword;
}
