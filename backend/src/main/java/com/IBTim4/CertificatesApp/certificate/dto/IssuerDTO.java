package com.IBTim4.CertificatesApp.certificate.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class IssuerDTO {

    private Long serialNumber;
    private String type;

}
