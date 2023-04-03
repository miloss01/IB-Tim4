package com.IBTim4.CertificatesApp.certificate.service.impl;

import com.IBTim4.CertificatesApp.certificate.AppCertificate;
import com.IBTim4.CertificatesApp.certificate.CertificateType;
import com.IBTim4.CertificatesApp.certificate.repository.CertificateRepository;
import com.IBTim4.CertificatesApp.certificate.service.interfaces.ICertificateService;
import com.IBTim4.CertificatesApp.exceptions.CustomExceptionWithMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class CertificateService implements ICertificateService {

    @Autowired
    CertificateRepository certificateRepository;

    @Override
    public ArrayList<AppCertificate> getAllCertificates() {
        return null;
    }

    @Override
    public boolean isCertificateValid(String serialNumber) {
        Optional<AppCertificate> certOptional = certificateRepository.findById(Long.valueOf(serialNumber));

        if (!certOptional.isPresent())
            throw new CustomExceptionWithMessage("Certificate with given serial number does not exist.",
                    HttpStatus.NOT_FOUND);
        AppCertificate cert = certOptional.get();

        if (!cert.isValid()) return false;
        else if (cert.getType().equals(CertificateType.ROOT)) return true;

        AppCertificate parentCert = cert.getIssuer();
        while (!parentCert.getType().equals(CertificateType.ROOT)) {
            if (parentCert.isValid()) parentCert = parentCert.getIssuer();
            else return false;
        }
        return parentCert.isValid();

    }
}
