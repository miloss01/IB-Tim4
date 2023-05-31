package com.IBTim4.CertificatesApp.appUser.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Pattern;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TwilloDTO {
    @Pattern(regexp = "^(\\+)(3816)([0-9]){6,9}$", message = "Field phone must be formatted like +381691234567")
    private String phone;
    @Pattern(regexp = "^([0-9]){6}$", message = "Field code must be formatted like 123456")
    private String code;
}
