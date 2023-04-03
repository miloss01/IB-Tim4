package com.IBTim4.CertificatesApp.certificate.service.interfaces;

import com.IBTim4.CertificatesApp.certificate.AppCertificate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public interface ICertificateService {
    public ArrayList<AppCertificate> getAllCertificates();

    public boolean isCertificateValid(String serialNumber);
}
