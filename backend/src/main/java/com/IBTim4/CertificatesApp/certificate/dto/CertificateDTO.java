package com.IBTim4.CertificatesApp.certificate.dto;

import com.IBTim4.CertificatesApp.appUser.dto.UserExpandedDTO;
import com.IBTim4.CertificatesApp.certificate.AppCertificate;
import lombok.*;
import javax.persistence.*;


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
    private String type;

    public CertificateDTO(AppCertificate certificate) {
        this.id = certificate.getId();
        this.startTime = certificate.getStartTime().toString();
        this.endTime = certificate.getEndTime().toString();
        this.subject = new UserExpandedDTO(certificate.getSubject());
        this.type = certificate.getType().toString();
    }
}
