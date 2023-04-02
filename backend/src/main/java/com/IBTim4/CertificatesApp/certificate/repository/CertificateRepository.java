package com.IBTim4.CertificatesApp.certificate.repository;

import com.IBTim4.CertificatesApp.certificate.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {
}
