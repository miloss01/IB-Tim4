package com.IBTim4.CertificatesApp.certificate.dto;

import com.IBTim4.CertificatesApp.appUser.dto.UserExpandedDTO;
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

    private CertificateDTO issuer;

    private String type;

}
