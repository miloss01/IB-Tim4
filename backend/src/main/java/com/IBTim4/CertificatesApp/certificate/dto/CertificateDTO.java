package com.IBTim4.CertificatesApp.certificate.dto;

import com.IBTim4.CertificatesApp.appUser.dto.UserExpandedDTO;
import com.IBTim4.CertificatesApp.certificate.AppCertificate;
import lombok.*;

import static com.IBTim4.CertificatesApp.helper.StringFormatting.dateTimeFormatter;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class CertificateDTO {


    private Long serialNumber;
    private String startTime;
    private String endTime;
    private UserExpandedDTO subject;

    private IssuerDTO issuer;

    private String type;

    public CertificateDTO(AppCertificate cert) {
        this.serialNumber = Long.valueOf(cert.getSerialNumber());
        this.startTime = cert.getStartTime().format(dateTimeFormatter);
        this.endTime = cert.getEndTime().format(dateTimeFormatter);
        if (cert.getSubject() != null) this.subject = new UserExpandedDTO(cert.getSubject());
        if (cert.getIssuer() != null)
            this.issuer = new IssuerDTO(Long.valueOf(cert.getIssuer().getSerialNumber()), cert.getIssuer().getType().toString());
        this.type = cert.getType().toString();
    }
//    public CertificateDTO(AppCertificate certificate) {
//        this.id = certificate.getId();
//        this.startTime = certificate.getStartTime().toString();
//        this.endTime = certificate.getEndTime().toString();
//        this.subject = new UserExpandedDTO(certificate.getSubject());
//        this.issuer = new CertificateDTO();
//        this.type = certificate.getType().toString();
//    }
}
