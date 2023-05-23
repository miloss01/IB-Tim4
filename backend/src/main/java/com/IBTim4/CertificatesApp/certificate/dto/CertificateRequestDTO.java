package com.IBTim4.CertificatesApp.certificate.dto;

import com.IBTim4.CertificatesApp.appUser.dto.UserExpandedDTO;
import com.IBTim4.CertificatesApp.certificate.CertificateRequest;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

import static com.IBTim4.CertificatesApp.helper.StringFormatting.dateTimeFormatter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class CertificateRequestDTO {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String id;
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

    private String recaptchaToken;

    public CertificateRequestDTO(CertificateRequest request) {
        this.id = request.getId().toString();
        this.certificateType = request.getCertificateType().toString();
        if (request.getIssuer() != null)
            this.issuerSN = request.getIssuer().getSerialNumber();
        else
            this.issuerSN = "";
        this.status = request.getStatus().toString();
        this.description = request.getDescription();
        this.creationTime = request.getCreationTime().format(dateTimeFormatter);
        this.requesterEmail = request.getRequester().getEmail();
        this.expirationTime = request.getExpirationTime().format(dateTimeFormatter) ;
    }
}
