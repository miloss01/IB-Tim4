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

    public boolean isValid() {
        return retracted && endTime.isBefore(LocalDateTime.now());
    }

}
