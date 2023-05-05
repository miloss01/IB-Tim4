package com.IBTim4.CertificatesApp.certificate.repository;

import com.IBTim4.CertificatesApp.certificate.Rejection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RejectionRepository extends JpaRepository<Rejection, Long> {
}

