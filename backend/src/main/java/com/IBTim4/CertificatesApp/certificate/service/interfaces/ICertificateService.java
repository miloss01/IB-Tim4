package com.IBTim4.CertificatesApp.certificate.service.interfaces;

import com.IBTim4.CertificatesApp.certificate.AppCertificate;

import java.util.ArrayList;

public interface ICertificateService {
    public ArrayList<AppCertificate> getAllCertificates();
    public AppCertificate saveCertificate(AppCertificate certificate);
    public void createCertificate(AppCertificate appCertificate);
}
