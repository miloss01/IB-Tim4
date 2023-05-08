package com.IBTim4.CertificatesApp.appUser.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class PasswordChangeDTO {
    private String phone;
    private String code;
    private String email;
    private String password;
}
