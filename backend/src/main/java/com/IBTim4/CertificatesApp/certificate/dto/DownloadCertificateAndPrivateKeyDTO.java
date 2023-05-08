package com.IBTim4.CertificatesApp.certificate.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class DownloadCertificateAndPrivateKeyDTO {

    private String certificateEncoded;
    private String privateKeyEncoded;

}
