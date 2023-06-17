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
@Table(name = "certificate")
public class AppCertificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private String serialNumber;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private AppUser subject;
    @ManyToOne
    @JoinColumn(name = "issuer_id")
    private AppCertificate issuer;

    private CertificateType type;

    private boolean retracted;
    private String reasonForRetracting;

    public AppCertificate(long l, LocalDateTime start, LocalDateTime end, AppUser requester, AppCertificate issuer, CertificateType certificateType) {
    }

    public boolean isValid() {
        return !retracted && endTime.isAfter(LocalDateTime.now());
    }

}
