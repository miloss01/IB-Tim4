package com.IBTim4.CertificatesApp.certificate.service.interfaces;

import com.IBTim4.CertificatesApp.appUser.AppUser;
import com.IBTim4.CertificatesApp.certificate.AppCertificate;
import com.IBTim4.CertificatesApp.certificate.CertificateRequest;
import com.IBTim4.CertificatesApp.certificate.CertificateType;
import com.IBTim4.CertificatesApp.certificate.Rejection;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

public interface ICertificateRequestService {

    Optional<CertificateRequest> findById(Long id);
    ArrayList<CertificateRequest> findByIssuer(AppCertificate issuer);
    ArrayList<CertificateRequest> findByRequester(AppUser requester);
    CertificateRequest save(CertificateRequest certificateRequest);
    CertificateRequest saveForCreation(CertificateRequest certificateRequest);
    ArrayList<CertificateRequest> findAll();
    public CertificateRequest getCertificateById(Long id);
    public void save(Rejection rejection);
    public LocalDateTime getEndTime(LocalDateTime start, CertificateType certificateType);
    public void userAuthenticity(AppUser subject);

    ArrayList<CertificateRequest> findBySubjectPending(AppUser subject);
}
