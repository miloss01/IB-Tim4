package com.IBTim4.CertificatesApp.certificate;


import com.IBTim4.CertificatesApp.appUser.AppUser;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
public class CertificateRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private RequestStatus status;
    private LocalDateTime creationTime;
    private String description;
    private CertificateType certificateType;
    @ManyToOne
    @JoinColumn(name = "issuer_id")
    private AppCertificate issuer;
    @ManyToOne
    @JoinColumn(name = "requester_id")
    private AppUser requester;
    private LocalDateTime expirationTime;
}
