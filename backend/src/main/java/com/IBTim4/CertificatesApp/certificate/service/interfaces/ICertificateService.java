package com.IBTim4.CertificatesApp.certificate.service.interfaces;

import com.IBTim4.CertificatesApp.appUser.AppUser;
import com.IBTim4.CertificatesApp.certificate.AppCertificate;
import com.IBTim4.CertificatesApp.certificate.CertificateRequest;

import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Optional;

public interface ICertificateService {
    public ArrayList<AppCertificate> getAllCertificates();
    public Optional<AppCertificate> findBySerialNumber(String serialNumber);
    public ArrayList<AppCertificate> findByAllBySubject(AppUser subject);
    public AppCertificate save(AppCertificate appCertificate);
    public AppCertificate createCertificate(CertificateRequest certificateRequest);
}
