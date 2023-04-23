package com.IBTim4.CertificatesApp.certificate.controller;

import com.IBTim4.CertificatesApp.Constants;
import com.IBTim4.CertificatesApp.certificate.AppCertificate;
import com.IBTim4.CertificatesApp.certificate.dto.CertificateDTO;
import com.IBTim4.CertificatesApp.certificate.dto.DownloadCertificateAndPrivateKeyDTO;
import com.IBTim4.CertificatesApp.certificate.service.interfaces.ICertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
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
//        Certificate certificate = certificateService.downloadCertificate(serialNumber);
//        Optional<AppCertificate> appCertificate = certificateService.findBySerialNumber(serialNumber);
//        AppCertificate issuer = appCertificate.get().getIssuer();
//        System.out.println("Issuer" + issuer.getId());
//
//        Certificate issuerCertificate = certificateService.downloadCertificate(issuer.getSerialNumber());
//        PublicKey issuerPublicKey = issuerCertificate.getPublicKey();
//
//        Boolean valid = true;
//        try {
//            certificate.verify(issuerPublicKey);
//            return new ResponseEntity<>(valid, HttpStatus.OK);
//        } catch (CertificateException e) {
//            throw new RuntimeException(e);
//        } catch (NoSuchAlgorithmException e) {
//            throw new RuntimeException(e);
//        } catch (InvalidKeyException e) {
//            throw new RuntimeException(e);
//        } catch (NoSuchProviderException e) {
//            throw new RuntimeException(e);
//        } catch (SignatureException e) {
//            throw new RuntimeException(e);
//        }

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

        LocalDateTime expirationTime = LocalDateTime.parse(certificateRequestDTO.getExpirationTime());

        if (!certificateRequestDTO.getCertificateType().equals(CertificateType.ROOT.name())) {

            Optional<AppCertificate> issuerCertificate = certificateService.findBySerialNumber(certificateRequestDTO.getIssuerSN());

            if (!issuerCertificate.isPresent())
                throw new CustomExceptionWithMessage("Certificate with that serial number does not exist!", HttpStatus.BAD_REQUEST);

            if (issuerCertificate.get().getType().equals(CertificateType.END))
                throw new CustomExceptionWithMessage("End certificate cannot be used as issuer!", HttpStatus.BAD_REQUEST);

            if (issuerCertificate.get().isRetracted())
                throw new CustomExceptionWithMessage("Issuer cannot be retracted certificate!", HttpStatus.BAD_REQUEST);

            certificateService.checkCertificateExpirationTime(expirationTime, CertificateType.valueOf(certificateRequestDTO.getCertificateType()));

            if (issuerCertificate.get().getEndTime().isBefore(expirationTime))
                throw new CustomExceptionWithMessage("Issuer will expire before the new certificate expires!", HttpStatus.BAD_REQUEST);

            issuer = issuerCertificate.get();

        } else {
            certificateService.checkCertificateExpirationTime(expirationTime, CertificateType.valueOf(certificateRequestDTO.getCertificateType()));
        }

        CertificateRequest certificateRequest = new CertificateRequest();
        certificateRequest.setCertificateType(CertificateType.valueOf(certificateRequestDTO.getCertificateType()));
        certificateRequest.setRequester(requester.get());
        certificateRequest.setIssuer(issuer);
        certificateRequest.setStatus(RequestStatus.PENDING);
        certificateRequest.setCreationTime(LocalDateTime.now());
        certificateRequest.setDescription(null);
        certificateRequest.setExpirationTime(LocalDateTime.parse(certificateRequestDTO.getExpirationTime()));

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

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Optional<AppUser> loggedIn = appUserService.findByEmail(email);

        if (loggedIn.get().getId() != req.getRequester().getId())
            throw new CustomExceptionWithMessage("You don't have access to that endpoint!", HttpStatus.FORBIDDEN);

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

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Optional<AppUser> loggedIn = appUserService.findByEmail(email);

        if (loggedIn.get().getId() != req.getRequester().getId())
            throw new CustomExceptionWithMessage("You don't have access to that endpoint!", HttpStatus.FORBIDDEN);

        if (!req.getStatus().equals(RequestStatus.PENDING))
            throw new CustomExceptionWithMessage("Cannot deny request that is not in pending state!", HttpStatus.BAD_REQUEST);

        req.setDescription(reason);
        req.setStatus(RequestStatus.DENIED);
        certificateRequestService.save(req);

        return new ResponseEntity<>(HttpStatus.OK);

    }

    @GetMapping(value = "/request/{requesterId}")
    @PreAuthorize(value = "hasRole('ADMIN') or @userSecurity.hasUserId(authentication, #requesterId)")
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

    @GetMapping(value = "/subject/{subjectId}")
    @PreAuthorize(value = "hasRole('ADMIN') or @userSecurity.hasUserId(authentication, #subjectId)")
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

    @GetMapping(value = "/retracted/{serialNumber}")
    public ResponseEntity<Boolean> checkIfCertificateIsRetracted(@PathVariable String serialNumber) {

        Optional<AppCertificate> certificate = certificateService.findBySerialNumber(serialNumber);

        if (!certificate.isPresent())
            throw new CustomExceptionWithMessage("Certificate with that serial number does not exist!", HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(certificate.get().isRetracted(), HttpStatus.OK);

    }

    @PostMapping(value = "/retract/{serialNumber}")
    public ResponseEntity<Void> retractCertificate(@PathVariable String serialNumber, @RequestBody String reason) {

        Optional<AppCertificate> certificate = certificateService.findBySerialNumber(serialNumber);

        if (!certificate.isPresent())
            throw new CustomExceptionWithMessage("Certificate with that serial number does not exist!", HttpStatus.BAD_REQUEST);

        if (certificate.get().isRetracted())
            throw new CustomExceptionWithMessage("You cannot retract certificate that is already retracted!", HttpStatus.BAD_REQUEST);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Optional<AppUser> loggedIn = appUserService.findByEmail(email);

        if (certificate.get().getSubject().getId() != loggedIn.get().getId() && loggedIn.get().getRole() != Role.ADMIN)
            throw new CustomExceptionWithMessage("You don't have access to that endpoint!", HttpStatus.FORBIDDEN);

        certificateService.retractCertificate(certificate.get(), reason);

        return new ResponseEntity<>(HttpStatus.OK);

    }

    @GetMapping(value = "/request")
    @PreAuthorize(value = "hasRole('ADMIN')")
    public ResponseEntity<ArrayList<CertificateRequestDTO>> getAllCertificateRequests() {

        ArrayList<CertificateRequestDTO> ret = new ArrayList<>();

        for (CertificateRequest cr : certificateRequestService.findAll())
            ret.add(new CertificateRequestDTO(cr));

        return new ResponseEntity<>(ret, HttpStatus.OK);

    }

    @GetMapping(value = "/download/{serialNumber}")
    public ResponseEntity<DownloadCertificateAndPrivateKeyDTO> getAllCertificateRequests(@PathVariable String serialNumber) {

        Optional<AppCertificate> certificate = certificateService.findBySerialNumber(serialNumber);

        if (!certificate.isPresent())
            throw new CustomExceptionWithMessage("Certificate with that serial number does not exist!", HttpStatus.BAD_REQUEST);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Optional<AppUser> loggedIn = appUserService.findByEmail(email);

        if (certificate.get().getSubject().getId() != loggedIn.get().getId() && loggedIn.get().getRole() != Role.ADMIN)
            throw new CustomExceptionWithMessage("You don't have access to that endpoint!", HttpStatus.FORBIDDEN);

        Certificate cert = certificateService.downloadCertificate(serialNumber);
        PrivateKey privateKey = certificateService.downloadPrivateKey(serialNumber);

        try {
//            ByteArrayResource resource = new ByteArrayResource(cert.getEncoded());
            String certEncoded = Arrays.toString(Base64.getEncoder().encode(cert.getEncoded()));
            String privateKeyEncoded = Arrays.toString(Base64.getEncoder().encode(privateKey.getEncoded()));

//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
//            headers.set("Content-Disposition", "attachment; filename=certificate.cert");

            return new ResponseEntity<>(
                    new DownloadCertificateAndPrivateKeyDTO(certEncoded, privateKeyEncoded),
                    HttpStatus.OK);
        } catch (CertificateEncodingException e) {
            throw new RuntimeException(e);
        }

    }

}
