package com.IBTim4.CertificatesApp.certificate.repository;

import com.IBTim4.CertificatesApp.appUser.AppUser;
import com.IBTim4.CertificatesApp.certificate.AppCertificate;
import com.IBTim4.CertificatesApp.certificate.CertificateRequest;
import com.IBTim4.CertificatesApp.certificate.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.Optional;

public interface CertificateRequestRepository extends JpaRepository<CertificateRequest, Long>  {

    ArrayList<CertificateRequest> findAllByIssuer(AppCertificate issuer);

    ArrayList<CertificateRequest> findAllByIssuerAndStatus(AppCertificate issuer, RequestStatus status);
    ArrayList<CertificateRequest> findAllByRequester(AppUser requester);

}
