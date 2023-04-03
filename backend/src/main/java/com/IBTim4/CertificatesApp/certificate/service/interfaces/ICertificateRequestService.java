package com.IBTim4.CertificatesApp.certificate.service.interfaces;

import com.IBTim4.CertificatesApp.appUser.AppUser;
import com.IBTim4.CertificatesApp.certificate.CertificateRequest;
import com.IBTim4.CertificatesApp.certificate.CertificateType;
import com.IBTim4.CertificatesApp.certificate.Rejection;

import java.time.LocalDateTime;

public interface ICertificateRequestService {
    public CertificateRequest getCertificateById(Long id);
    public void save(Rejection rejection);
    public void save(CertificateRequest certificateRequest);
    public LocalDateTime getEndTime(LocalDateTime start, CertificateType certificateType);
    public void userAuthenticity(AppUser subject);
}
