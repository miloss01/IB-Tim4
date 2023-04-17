package com.IBTim4.CertificatesApp.certificate.dto;

import com.IBTim4.CertificatesApp.appUser.dto.UserExpandedDTO;
import com.IBTim4.CertificatesApp.certificate.CertificateRequest;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class CertificateRequestDTO {
    private String certificateType;
    private String issuerSN;
    private String requesterEmail;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String status;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String description;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String creationTime;
    private String expirationTime;

    public CertificateRequestDTO(CertificateRequest request) {
        this.certificateType = request.getCertificateType().toString();
        if (request.getIssuer() != null)
            this.issuerSN = request.getIssuer().getSerialNumber();
        else
            this.issuerSN = "";
        this.status = request.getStatus().toString();
        this.description = request.getDescription();
        this.creationTime = request.getCreationTime().toString();
        this.requesterEmail = request.getRequester().getEmail();
        this.expirationTime = request.getExpirationTime().toString();
    }
}
