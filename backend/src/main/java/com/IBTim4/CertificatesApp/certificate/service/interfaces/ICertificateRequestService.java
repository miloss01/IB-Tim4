package com.IBTim4.CertificatesApp.certificate.service.interfaces;

import com.IBTim4.CertificatesApp.appUser.AppUser;
import com.IBTim4.CertificatesApp.certificate.AppCertificate;
import com.IBTim4.CertificatesApp.certificate.CertificateRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface ICertificateRequestService {

    Optional<CertificateRequest> findById(Long id);
    ArrayList<CertificateRequest> findByIssuer(AppCertificate issuer);
    ArrayList<CertificateRequest> findByRequester(AppUser requester);
    CertificateRequest save(CertificateRequest certificateRequest);
    ArrayList<CertificateRequest> findAll();

}
