package com.IBTim4.CertificatesApp.certificate.repository;

import com.IBTim4.CertificatesApp.certificate.CertificateRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CertificateRequestRepository extends JpaRepository<CertificateRequest, Long> {
}

