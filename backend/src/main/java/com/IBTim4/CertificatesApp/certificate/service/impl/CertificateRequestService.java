package com.IBTim4.CertificatesApp.certificate.service.impl;

import com.IBTim4.CertificatesApp.appUser.AppUser;
import com.IBTim4.CertificatesApp.certificate.AppCertificate;
import com.IBTim4.CertificatesApp.certificate.CertificateRequest;
import com.IBTim4.CertificatesApp.certificate.repository.CertificateRepository;
import com.IBTim4.CertificatesApp.certificate.repository.CertificateRequestRepository;
import com.IBTim4.CertificatesApp.certificate.service.interfaces.ICertificateRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class CertificateRequestService implements ICertificateRequestService {

    @Autowired
    private CertificateRequestRepository certificateRequestRepository;

    @Override
    public Optional<CertificateRequest> findById(Long id) {
        return certificateRequestRepository.findById(id);
    }

    @Override
    public ArrayList<CertificateRequest> findByIssuer(AppCertificate issuer) {
        return certificateRequestRepository.findAllByIssuer(issuer);
    }

    @Override
    public ArrayList<CertificateRequest> findByRequester(AppUser requester) {
        return certificateRequestRepository.findAllByRequester(requester);
    }

    @Override
    public CertificateRequest save(CertificateRequest certificateRequest) {
        return certificateRequestRepository.save(certificateRequest);
    }

    @Override
    public ArrayList<CertificateRequest> findAll() {
        return new ArrayList<>(certificateRequestRepository.findAll());
    }
}
