package com.IBTim4.CertificatesApp.certificate.controller;

import com.IBTim4.CertificatesApp.certificate.AppCertificate;
import com.IBTim4.CertificatesApp.certificate.dto.CertificateDTO;
import com.IBTim4.CertificatesApp.certificate.service.interfaces.ICertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.IBTim4.CertificatesApp.appUser.AppUser;
import com.IBTim4.CertificatesApp.appUser.Role;
import com.IBTim4.CertificatesApp.appUser.service.interfaces.IAppUserService;
import com.IBTim4.CertificatesApp.certificate.CertificateRequest;
import com.IBTim4.CertificatesApp.certificate.CertificateType;
import com.IBTim4.CertificatesApp.certificate.RequestStatus;
import com.IBTim4.CertificatesApp.certificate.dto.CertificateRequestDTO;
import com.IBTim4.CertificatesApp.certificate.service.interfaces.ICertificateRequestService;
import com.IBTim4.CertificatesApp.exceptions.CustomExceptionWithMessage;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/api/certificate")
@Validated
public class CertificateController {

    @Autowired
    private IAppUserService appUserService;
    @Autowired
    private ICertificateService certificateService;
    @Autowired
    private ICertificateRequestService certificateRequestService;

    @GetMapping(value="/valid", produces = "application/json")
    public ResponseEntity<Boolean> checkValidity(@RequestParam String serialNumber) {
        boolean valid = certificateService.isCertificateValid(serialNumber);
        return new ResponseEntity<>(valid, HttpStatus.OK);
    }

    @GetMapping(value="/all", produces = "application/json")
    public ResponseEntity<ArrayList<CertificateDTO>> getAll() {
        ArrayList<CertificateDTO> certificateDTOS = new ArrayList<>();
        for (AppCertificate cert : certificateService.getAllCertificates()) {
            certificateDTOS.add(new CertificateDTO(cert));
        }
        return new ResponseEntity<>(certificateDTOS, HttpStatus.OK);
    }
    @PostMapping(value = "/request", consumes = "application/json")
    public ResponseEntity<Void> createCertificateRequest(@Valid @RequestBody CertificateRequestDTO certificateRequestDTO) {

        Optional<AppUser> requester = appUserService.findByEmail(certificateRequestDTO.getRequesterEmail());

        if (!requester.isPresent())
            throw new CustomExceptionWithMessage("User with that email does not exist!", HttpStatus.BAD_REQUEST);

        AppCertificate issuer = null;

        if (certificateRequestDTO.getCertificateType().equals(CertificateType.ROOT.name()) && !requester.get().getRole().equals(Role.ADMIN))
            throw new CustomExceptionWithMessage("Only admin can create root certificate!", HttpStatus.BAD_REQUEST);

        if (!certificateRequestDTO.getCertificateType().equals(CertificateType.ROOT.name())) {

            Optional<AppCertificate> issuerCertificate = certificateService.findBySerialNumber(certificateRequestDTO.getIssuerSN());

            if (!issuerCertificate.isPresent())
                throw new CustomExceptionWithMessage("Certificate with that serial number does not exist!", HttpStatus.BAD_REQUEST);

            if (issuerCertificate.get().getType().equals(CertificateType.END))
                throw new CustomExceptionWithMessage("End certificate cannot be used as issuer!", HttpStatus.BAD_REQUEST);

            issuer = issuerCertificate.get();

        }

        CertificateRequest certificateRequest = new CertificateRequest();
        certificateRequest.setCertificateType(CertificateType.valueOf(certificateRequestDTO.getCertificateType()));
        certificateRequest.setRequester(requester.get());
        certificateRequest.setIssuer(issuer);
        certificateRequest.setStatus(RequestStatus.PENDING);
        certificateRequest.setCreationTime(LocalDateTime.now());
        certificateRequest.setDescription(null);

        certificateRequestService.save(certificateRequest);

        return new ResponseEntity<>(HttpStatus.OK);

    }

    @PostMapping(value = "/request/accept/{requestId}")
    public ResponseEntity<Void> acceptCertificateRequest(@PathVariable Long requestId) {

        Optional<CertificateRequest> certificateRequest = certificateRequestService.findById(requestId);

        if (!certificateRequest.isPresent())
            throw new CustomExceptionWithMessage("Certificate request with that id does not exist!", HttpStatus.BAD_REQUEST);

        CertificateRequest req = certificateRequest.get();

        if (!req.getStatus().equals(RequestStatus.PENDING))
            throw new CustomExceptionWithMessage("Cannot accept request that is not in pending state!", HttpStatus.BAD_REQUEST);

        AppCertificate cert = certificateService.createCertificate(req);

        req.setStatus(RequestStatus.APPROVED);
        certificateRequestService.save(req);

        return new ResponseEntity<>(HttpStatus.OK);

    }

    @PostMapping(value = "/request/deny/{requestId}")
    public ResponseEntity<Void> denyCertificateRequest(@PathVariable Long requestId, @RequestBody String reason) {

        Optional<CertificateRequest> certificateRequest = certificateRequestService.findById(requestId);

        if (!certificateRequest.isPresent())
            throw new CustomExceptionWithMessage("Certificate request with that id does not exist!", HttpStatus.BAD_REQUEST);

        CertificateRequest req = certificateRequest.get();

        if (!req.getStatus().equals(RequestStatus.PENDING))
            throw new CustomExceptionWithMessage("Cannot deny request that is not in pending state!", HttpStatus.BAD_REQUEST);

        req.setDescription(reason);
        req.setStatus(RequestStatus.DENIED);
        certificateRequestService.save(req);

        return new ResponseEntity<>(HttpStatus.OK);

    }

    @GetMapping(value = "/request/{requesterId}")
    public ResponseEntity<ArrayList<CertificateRequestDTO>> getAllCertificateRequestsForRequester(@PathVariable Long requesterId) {

        Optional<AppUser> requester = appUserService.findById(requesterId);

        if (!requester.isPresent())
            throw new CustomExceptionWithMessage("User with that id does not exist!", HttpStatus.BAD_REQUEST);

        ArrayList<CertificateRequest> requests = certificateRequestService.findByRequester(requester.get());

        ArrayList<CertificateRequestDTO> ret = new ArrayList<>();
        for (CertificateRequest request : requests)
            ret.add(new CertificateRequestDTO(request));

        return new ResponseEntity<>(ret, HttpStatus.OK);

    }

    @GetMapping(value = "/{subjectId}")
    public ResponseEntity<ArrayList<CertificateDTO>> getAllCertificatesForSubject(@PathVariable Long subjectId) {

        Optional<AppUser> subject = appUserService.findById(subjectId);

        if (!subject.isPresent())
            throw new CustomExceptionWithMessage("User with that id does not exist!", HttpStatus.BAD_REQUEST);

        ArrayList<AppCertificate> certificates = certificateService.findByAllBySubject(subject.get());

        ArrayList<CertificateDTO> ret = new ArrayList<>();
        for (AppCertificate certificate : certificates)
            ret.add(new CertificateDTO(certificate));

        return new ResponseEntity<>(ret, HttpStatus.OK);

    }

}
