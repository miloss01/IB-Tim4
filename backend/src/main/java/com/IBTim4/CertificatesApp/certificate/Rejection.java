package com.IBTim4.CertificatesApp.certificate;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "rejection")
public class Rejection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    private CertificateRequest certificateRequest;

    private String reason;

    private LocalDateTime rejectionTime;

}