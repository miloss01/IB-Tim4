package com.IBTim4.CertificatesApp.appUser.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TwilloDTO {
    private String phone;
    private String code;
}
