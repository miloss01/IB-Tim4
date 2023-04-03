package com.IBTim4.CertificatesApp.certificate.service.impl;

import com.IBTim4.CertificatesApp.certificate.AppCertificate;
import com.IBTim4.CertificatesApp.certificate.repository.CertificateRepository;
import com.IBTim4.CertificatesApp.certificate.service.interfaces.ICertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
@Service
public class CertificateService implements ICertificateService {
    @Autowired
    private CertificateRepository certificateRepository;
    @Override
    public ArrayList<AppCertificate> getAllCertificates() {
        return null;
    }

    @Override
    public AppCertificate saveCertificate(AppCertificate certificate) {
        return certificateRepository.save(certificate);
    }

    @Override
    public void createCertificate(AppCertificate appCertificate) {

    }
}
