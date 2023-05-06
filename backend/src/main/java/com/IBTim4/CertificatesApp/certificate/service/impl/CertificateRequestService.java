package com.IBTim4.CertificatesApp.certificate.service.impl;

import com.IBTim4.CertificatesApp.appUser.AppUser;
import com.IBTim4.CertificatesApp.appUser.Role;
import com.IBTim4.CertificatesApp.certificate.*;
import com.IBTim4.CertificatesApp.certificate.repository.CertificateRepository;
import com.IBTim4.CertificatesApp.certificate.repository.CertificateRequestRepository;
import com.IBTim4.CertificatesApp.certificate.service.interfaces.ICertificateRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import com.IBTim4.CertificatesApp.appUser.service.interfaces.IAppUserService;
import com.IBTim4.CertificatesApp.certificate.repository.RejectionRepository;
import com.IBTim4.CertificatesApp.exceptions.CustomExceptionWithMessage;
import org.springframework.http.HttpStatus;

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

    @Autowired
    private CertificateService certificateService;

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
    public CertificateRequest save(CertificateRequest req) {
        return certificateRequestRepository.save(req);
    }

    @Override
    public CertificateRequest saveForCreation(CertificateRequest req) {
        if (req.getRequester().getRole().equals(Role.ADMIN) ||
                (req.getIssuer().getSubject() != null && req.getRequester().getEmail().equals(req.getIssuer().getSubject().getEmail()))) {
            req.setStatus(RequestStatus.APPROVED);
            certificateService.createCertificate(req);
        }
        return certificateRequestRepository.save(req);
    }

    @Override
    public ArrayList<CertificateRequest> findAll() {
        return new ArrayList<>(certificateRequestRepository.findAll());
    }
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
    public LocalDateTime getEndTime(LocalDateTime start, CertificateType certificateType) {
        if (certificateType == CertificateType.END){return start.plusMonths(1);}
        if (certificateType == CertificateType.INTERMEDIATE){return start.plusMonths(3);}
        throw new CustomExceptionWithMessage("Certificate can not be root", HttpStatus.BAD_REQUEST);    }

    @Override
    public void userAuthenticity(AppUser subject) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        AppUser loggedUser = appUserService.findByEmail(authentication.getName());
    }

    @Override
    public ArrayList<CertificateRequest> findBySubjectPending(AppUser subject) {
        ArrayList<AppCertificate> subjectsCertificates = certificateService.findByAllBySubject(subject);
        ArrayList<CertificateRequest> requestsForSubjectsCertificates = new ArrayList<>();

        for (AppCertificate c : subjectsCertificates) {
            requestsForSubjectsCertificates.addAll(certificateRequestRepository.findAllByIssuerAndStatus(c, RequestStatus.PENDING));
        }

        return requestsForSubjectsCertificates;
    }
}
