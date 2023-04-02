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
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private LocalDateTime issuedTime;

    // TODO - kako tacno povezujemo subject/koje inf od njega
    @ManyToOne
    @JoinColumn(name = "subject_id")
    private AppUser subject;

    @ManyToOne
    @JoinColumn(name = "issuer_id")
    private AppUser issuer;

    private CertificateType type;

}
