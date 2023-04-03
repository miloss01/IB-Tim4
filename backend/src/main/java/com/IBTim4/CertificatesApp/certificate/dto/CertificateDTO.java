package com.IBTim4.CertificatesApp.certificate.dto;

import com.IBTim4.CertificatesApp.appUser.dto.UserExpandedDTO;
import com.IBTim4.CertificatesApp.certificate.AppCertificate;
import lombok.*;
import javax.persistence.*;

import static com.IBTim4.CertificatesApp.helper.StringFormatting.dateTimeFormatter;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class CertificateDTO {


    private Long id;

    private String startTime;

    private String endTime;

    private UserExpandedDTO subject;

    private IssuerDTO issuer;

    private String type;

    public CertificateDTO(AppCertificate cert) {
        this.id = cert.getId();
        this.startTime = cert.getStartTime().format(dateTimeFormatter);
        this.endTime = cert.getEndTime().format(dateTimeFormatter);
        if (cert.getSubject() != null) this.subject = new UserExpandedDTO(cert.getSubject());
        if (cert.getIssuer() != null) this.issuer = new IssuerDTO(cert.getIssuer().getId(), cert.getIssuer().getType().toString());
        this.type = cert.getType().toString();
    }
}
