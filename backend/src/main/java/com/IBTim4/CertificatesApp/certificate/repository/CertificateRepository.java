package com.IBTim4.CertificatesApp.certificate.repository;

import com.IBTim4.CertificatesApp.appUser.AppUser;
import com.IBTim4.CertificatesApp.certificate.AppCertificate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.Optional;

public interface CertificateRepository extends JpaRepository<AppCertificate, Long> {

    Optional<AppCertificate> findBySerialNumber(String serialNumber);
    ArrayList<AppCertificate> findAllBySubject(AppUser subject);
    ArrayList<AppCertificate> findAllByIssuer(AppCertificate issuer);

}
