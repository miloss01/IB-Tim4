package com.IBTim4.CertificatesApp.certificate.dto;

import com.IBTim4.CertificatesApp.appUser.dto.UserExpandedDTO;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class CertificateRequestDTO {
    private Long id;

    private String status;

    private String creationTime;

    private String description;

    private String certificateType;

    private CertificateDTO issuer;

    private UserExpandedDTO requester;
}
