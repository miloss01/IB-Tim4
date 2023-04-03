package com.IBTim4.CertificatesApp.certificate.service.impl;

import com.IBTim4.CertificatesApp.appUser.AppUser;
import com.IBTim4.CertificatesApp.appUser.repository.AppUserRepository;
import com.IBTim4.CertificatesApp.appUser.service.interfaces.IAppUserService;
import com.IBTim4.CertificatesApp.certificate.CertificateRequest;
import com.IBTim4.CertificatesApp.certificate.CertificateType;
import com.IBTim4.CertificatesApp.certificate.Rejection;
import com.IBTim4.CertificatesApp.certificate.repository.CertificateRequestRepository;
import com.IBTim4.CertificatesApp.certificate.repository.RejectionRepository;
import com.IBTim4.CertificatesApp.certificate.service.interfaces.ICertificateRequestService;
import com.IBTim4.CertificatesApp.exceptions.CustomExceptionWithMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CertificateRequestService implements ICertificateRequestService {
    @Autowired
    private IAppUserService appUserService;
    @Autowired
    private RejectionRepository rejectionRepository;
    @Autowired
    private CertificateRequestRepository certificateRequestRepository;

    @Override
    public CertificateRequest getCertificateById(Long id){
        Optional<CertificateRequest> optional = certificateRequestRepository.findById(id);
        if (!optional.isPresent()) {
            throw new CustomExceptionWithMessage("Certificate request does not exist", HttpStatus.BAD_REQUEST);
        }
        return optional.get();
    }

    @Override
    public void save(Rejection rejection) {
        rejectionRepository.save(rejection);
    }

    @Override
    public void save(CertificateRequest certificateRequest) {
        certificateRequestRepository.save(certificateRequest);
    }

    @Override
    public LocalDateTime getEndTime(LocalDateTime start, CertificateType certificateType) {
        if (certificateType == CertificateType.END){return start.plusMonths(1);}
        if (certificateType == CertificateType.INTERMEDIATE){return start.plusMonths(3);}
        throw new CustomExceptionWithMessage("Certificate can not be root", HttpStatus.BAD_REQUEST);    }

    @Override
    public void userAuthenticity(AppUser subject) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        AppUser loggedUser = appUserService.findByEmail(authentication.getName());
    }
}
