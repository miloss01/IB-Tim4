package com.IBTim4.CertificatesApp.certificate.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class DownloadCertificateAndPrivateKeyDTO {

    @NotBlank(message = "Field (certificateEncoded) is required!")
    private String certificateEncoded;
    @NotBlank(message = "Field (privateKeyEncoded) is required!")
    private String privateKeyEncoded;

}
