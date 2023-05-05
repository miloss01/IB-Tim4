package com.IBTim4.CertificatesApp.certificate.service.interfaces;

import com.IBTim4.CertificatesApp.appUser.AppUser;
import com.IBTim4.CertificatesApp.certificate.AppCertificate;
import com.IBTim4.CertificatesApp.certificate.CertificateType;
import org.springframework.stereotype.Service;
import com.IBTim4.CertificatesApp.certificate.CertificateRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

@Service
public interface ICertificateService {
    public ArrayList<AppCertificate> getAllCertificates();
    public boolean isCertificateValid(String serialNumber);

    public Optional<AppCertificate> findBySerialNumber(String serialNumber);
    public ArrayList<AppCertificate> findByAllBySubject(AppUser subject);
    public AppCertificate save(AppCertificate appCertificate);
    public AppCertificate createCertificate(CertificateRequest certificateRequest);
    public Boolean checkCertificateExpirationTime(LocalDateTime expirationTime, CertificateType type);
    public Boolean retractCertificate(AppCertificate certificate, String reason);
}
