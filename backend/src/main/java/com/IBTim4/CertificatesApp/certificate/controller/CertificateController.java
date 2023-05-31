package com.IBTim4.CertificatesApp.certificate.controller;

import com.IBTim4.CertificatesApp.appUser.controller.AppUserController;
import com.IBTim4.CertificatesApp.certificate.AppCertificate;
import com.IBTim4.CertificatesApp.certificate.dto.CertificateDTO;
import com.IBTim4.CertificatesApp.certificate.dto.DownloadCertificateAndPrivateKeyDTO;
import com.IBTim4.CertificatesApp.certificate.dto.RejectionDTO;
import com.IBTim4.CertificatesApp.certificate.service.interfaces.ICertificateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StreamUtils;
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

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.*;
import java.security.cert.Certificate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@CrossOrigin(origins = "http://localhost:4200")
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

    Logger logger = LoggerFactory.getLogger(CertificateController.class);

    @GetMapping(value = "/valid", produces = "application/json")
    public ResponseEntity<Boolean> checkValidity(@RequestParam String serialNumber) {
        logger.info("Stated validating certificate with ID: " + serialNumber);
        boolean valid = certificateService.isCertificateValid(serialNumber);
        logger.info("Certificate is " + (valid ? "" : "not") + " valid.");
        return new ResponseEntity<>(valid, HttpStatus.OK);
    }

    @GetMapping(value = "/all", produces = "application/json")
    public ResponseEntity<ArrayList<CertificateDTO>> getAll() {
        logger.info("Started getting all certificates.");
        ArrayList<CertificateDTO> certificateDTOS = new ArrayList<>();
        for (AppCertificate cert : certificateService.getAllCertificates()) {
            certificateDTOS.add(new CertificateDTO(cert));
        }
        return new ResponseEntity<>(certificateDTOS, HttpStatus.OK);
    }

    @GetMapping(value = "/allSN", produces = "application/json")
    public ResponseEntity<ArrayList<String>> getAllSerialNumbers() {
        logger.info("Started getting all certificate serial numbers.");
        ArrayList<String> serialNumbers = new ArrayList<>();
        for (AppCertificate cert : certificateService.getAllCertificates()) {
            serialNumbers.add(cert.getSerialNumber().toString());
        }
        return new ResponseEntity<>(serialNumbers, HttpStatus.OK);
    }

    @GetMapping(value = "/subject/{subjectId}")
    @PreAuthorize(value = "hasRole('ADMIN') or @userSecurity.hasUserId(authentication, #subjectId)")
    public ResponseEntity<ArrayList<CertificateDTO>> getAllCertificatesForSubject(@PathVariable Long subjectId) {
        logger.info("Started getting all certificates for subject with ID: " + subjectId);

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

        logger.info("Started check if certificate with serial number: " + serialNumber + " is revoked.");
        Optional<AppCertificate> certificate = certificateService.findBySerialNumber(serialNumber);

        if (!certificate.isPresent())
            throw new CustomExceptionWithMessage("Certificate with that serial number does not exist!", HttpStatus.BAD_REQUEST);

        logger.info("Certificate is " + (certificate.get().isRetracted() ? "" : "not") + " revoked.");
        return new ResponseEntity<>(certificate.get().isRetracted(), HttpStatus.OK);

    }

    @PostMapping(value = "/retract/{serialNumber}")
    public ResponseEntity<Void> retractCertificate(@PathVariable String serialNumber, @RequestBody String reason) {
        logger.info("Started revoking certificate with serial number: " + serialNumber);

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

    @PostMapping(value = "/request", consumes = "application/json")
    public ResponseEntity<Void> createCertificateRequest(@Valid @RequestBody CertificateRequestDTO certificateRequestDTO) {

        Optional<AppUser> requester = appUserService.findByEmail(certificateRequestDTO.getRequesterEmail());

        logger.info("Creating request for user with ID: " + requester.get().getId());

        if (!requester.isPresent())
            throw new CustomExceptionWithMessage("User with that email does not exist!", HttpStatus.BAD_REQUEST);

        AppCertificate issuer = null;

        if (certificateRequestDTO.getCertificateType().equals(CertificateType.ROOT.name()) && !requester.get().getRole().equals(Role.ADMIN))
            throw new CustomExceptionWithMessage("Only admin can create root certificate!", HttpStatus.BAD_REQUEST);

        LocalDateTime expirationTime = LocalDateTime.parse(certificateRequestDTO.getExpirationTime().substring(0, certificateRequestDTO.getExpirationTime().length()-2));

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
        certificateRequest.setExpirationTime(expirationTime);

        certificateRequestService.saveForCreation(certificateRequest);

        logger.info("Request created.");
        return new ResponseEntity<>(HttpStatus.OK);

    }

    @PostMapping(value = "/request/accept/{requestId}")
    public ResponseEntity<Void> acceptCertificateRequest(@PathVariable Long requestId) {

        logger.info("Accepting request with ID: " + requestId);

        Optional<CertificateRequest> certificateRequest = certificateRequestService.findById(requestId);

        if (!certificateRequest.isPresent())
            throw new CustomExceptionWithMessage("Certificate request with that id does not exist!", HttpStatus.BAD_REQUEST);

        CertificateRequest req = certificateRequest.get();

        if (!req.getStatus().equals(RequestStatus.PENDING))
            throw new CustomExceptionWithMessage("Cannot accept request that is not in pending state!", HttpStatus.BAD_REQUEST);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Optional<AppUser> loggedIn = appUserService.findByEmail(email);

        if ( req.getIssuer().getSubject() != null && (loggedIn.get().getId() != req.getIssuer().getSubject().getId()))
            throw new CustomExceptionWithMessage("You don't have access to that endpoint!", HttpStatus.FORBIDDEN);

        AppCertificate cert = certificateService.createCertificate(req);

        req.setStatus(RequestStatus.APPROVED);
        certificateRequestService.save(req);

        logger.info("Request accepted.");
        return new ResponseEntity<>(HttpStatus.OK);

    }

    @PostMapping(value = "/request/deny/{requestId}")
    public ResponseEntity<Void> denyCertificateRequest(@PathVariable Long requestId, @RequestBody String reason) {

        logger.info("Denying request with ID: " + requestId);

        Optional<CertificateRequest> certificateRequest = certificateRequestService.findById(requestId);

        if (!certificateRequest.isPresent())
            throw new CustomExceptionWithMessage("Certificate request with that id does not exist!", HttpStatus.BAD_REQUEST);

        CertificateRequest req = certificateRequest.get();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Optional<AppUser> loggedIn = appUserService.findByEmail(email);

        if (loggedIn.get().getId() != req.getIssuer().getSubject().getId())
            throw new CustomExceptionWithMessage("You don't have access to that endpoint!", HttpStatus.FORBIDDEN);

        if (!req.getStatus().equals(RequestStatus.PENDING))
            throw new CustomExceptionWithMessage("Cannot deny request that is not in pending state!", HttpStatus.BAD_REQUEST);

        req.setDescription(reason);
        req.setStatus(RequestStatus.DENIED);
        certificateRequestService.save(req);

        logger.info("Request accepted.");
        return new ResponseEntity<>(HttpStatus.OK);

    }

    @GetMapping(value = "/request/{requesterId}")
    @PreAuthorize(value = "hasRole('ADMIN') or @userSecurity.hasUserId(authentication, #requesterId)")
    public ResponseEntity<ArrayList<CertificateRequestDTO>> getAllCertificateRequestsForRequester(@PathVariable Long requesterId) {

        logger.info("Getting requests for user with ID: " + requesterId);

        Optional<AppUser> requester = appUserService.findById(requesterId);

        if (!requester.isPresent())
            throw new CustomExceptionWithMessage("User with that id does not exist!", HttpStatus.BAD_REQUEST);

        ArrayList<CertificateRequest> requests = certificateRequestService.findByRequester(requester.get());

        ArrayList<CertificateRequestDTO> ret = new ArrayList<>();
        for (CertificateRequest request : requests)
            ret.add(new CertificateRequestDTO(request));

        return new ResponseEntity<>(ret, HttpStatus.OK);

    }

    @GetMapping(value = "/request")
    @PreAuthorize(value = "hasRole('ADMIN')")
    public ResponseEntity<ArrayList<CertificateRequestDTO>> getAllCertificateRequests() {

        logger.info("Getting all requests.");

        ArrayList<CertificateRequestDTO> ret = new ArrayList<>();

        for (CertificateRequest cr : certificateRequestService.findAll())
            ret.add(new CertificateRequestDTO(cr));

        return new ResponseEntity<>(ret, HttpStatus.OK);

    }

    @PostMapping(value = "/approveRequest/{requestId}")
    public ResponseEntity<CertificateDTO> approveCertificate(@PathVariable Integer requestId) {

        logger.info("Approving request with ID: " + requestId);

        CertificateRequest certificateRequest = certificateRequestService.getCertificateById(Long.valueOf(requestId));
//        certificateRequestService.authentify(certificateRequest.getIssuer().getSubject());
        certificateRequest.setStatus(RequestStatus.APPROVED);
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = certificateRequestService.getEndTime(start, certificateRequest.getCertificateType());

        AppCertificate certificate = new AppCertificate(0L, start, end, certificateRequest.getRequester(),
                certificateRequest.getIssuer(), certificateRequest.getCertificateType());
        certificateService.saveCertificate(certificate);

        logger.info("Request approved.");
        return new ResponseEntity<>(new CertificateDTO(certificate), HttpStatus.OK);
    }

    //
    @PostMapping(value = "/declineRequest/{requestId}")
    public ResponseEntity<RejectionDTO> declineCertificate(@PathVariable Integer requestId, @Valid @RequestBody RejectionDTO rejectionDTO) {

        logger.info("Declining request with ID: " + requestId);

        CertificateRequest certificateRequest = certificateRequestService.getCertificateById(Long.valueOf(requestId));
        certificateRequest.setStatus(RequestStatus.DENIED);
        certificateRequest.setDescription(rejectionDTO.getReason());
//        Rejection rejection = new Rejection(0L, certificateRequest, rejectionDTO.getReason(), LocalDateTime.now());
//        certificateRequestService.save(rejection);
        certificateRequestService.save(certificateRequest);
        logger.info("Request approved.");
        return new ResponseEntity<>(new RejectionDTO(), HttpStatus.OK);
    }

    @GetMapping(value = "/request/manage/{userId}")
    public ResponseEntity<ArrayList<CertificateRequestDTO>> getAllPendingCertificateRequestsWhereUserIsSubjectInIssuer(@PathVariable Integer userId) {

        logger.info("Getting all pending requests for user with ID: " + userId);

        Optional<AppUser> subject = appUserService.findById(Long.valueOf(userId));

        if (!subject.isPresent())
            throw new CustomExceptionWithMessage("User with that id does not exist!", HttpStatus.BAD_REQUEST);

        ArrayList<CertificateRequest> requests = certificateRequestService.findWhereSubjectInIssuerAndStatusPending(subject.get());

        ArrayList<CertificateRequestDTO> ret = new ArrayList<>();
        for (CertificateRequest request : requests)  ret.add(new CertificateRequestDTO(request));

        return new ResponseEntity<>(ret, HttpStatus.OK);

    }

    @GetMapping(value = "/request/manage-admin/{userId}")
    @PreAuthorize(value = "hasRole('ADMIN')")
    public ResponseEntity<ArrayList<CertificateRequestDTO>> getAllPendingCertificateRequestsFromRootCertificates(@PathVariable Integer userId) {

        logger.info("Getting all pending requests for admin with ID: " + userId);

        Optional<AppUser> subject = appUserService.findById(Long.valueOf(userId));

        if (!subject.isPresent())
            throw new CustomExceptionWithMessage("User with that id does not exist!", HttpStatus.BAD_REQUEST);

        ArrayList<CertificateRequest> requests = certificateRequestService.findRequestsFromRootAndPending(subject.get());

        ArrayList<CertificateRequestDTO> ret = new ArrayList<>();
        for (CertificateRequest request : requests)
            ret.add(new CertificateRequestDTO(request));

        return new ResponseEntity<>(ret, HttpStatus.OK);

    }


    @GetMapping(value = "/download/{serialNumber}", produces="application/zip")
    public void getAllCertificateRequests(@PathVariable String serialNumber, HttpServletResponse response) {

        logger.info("Downloading certificate with serial number: " + serialNumber);

        Optional<AppCertificate> certificate = certificateService.findBySerialNumber(serialNumber);

        if (!certificate.isPresent())
            throw new CustomExceptionWithMessage("Certificate with that serial number does not exist!", HttpStatus.BAD_REQUEST);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Optional<AppUser> loggedIn = appUserService.findByEmail(email);

        Boolean certAndPk = false;

        if (certificate.get().getSubject().getId() == loggedIn.get().getId() || loggedIn.get().getRole() == Role.ADMIN)
            certAndPk = true;

        Certificate cert = certificateService.downloadCertificate(serialNumber);
        PrivateKey privateKey = certificateService.downloadPrivateKey(serialNumber);

        try {
            ByteArrayResource certResource = new ByteArrayResource(cert.getEncoded());
            ByteArrayResource pkResource = new ByteArrayResource(privateKey.getEncoded());
//            String certEncoded = new String(Base64.getEncoder().encode(cert.getEncoded()));
//            String privateKeyEncoded = new String(Base64.getEncoder().encode(privateKey.getEncoded()));

            ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream());

            ZipEntry zipEntry = new ZipEntry("cert" + serialNumber + ".crt");
            zipEntry.setSize(certResource.contentLength());
            zipOut.putNextEntry(zipEntry);
            StreamUtils.copy(certResource.getInputStream(), zipOut);
            zipOut.closeEntry();

            if (certAndPk) {
                ZipEntry zipEntry2 = new ZipEntry("pk" + serialNumber + ".pk");
                zipEntry.setSize(pkResource.contentLength());
                zipOut.putNextEntry(zipEntry2);
                StreamUtils.copy(pkResource.getInputStream(), zipOut);
                zipOut.closeEntry();
            }

            zipOut.finish();
            zipOut.close();

            response.setStatus(HttpServletResponse.SC_OK);
            response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + "zipovano.zip" + "\"");

        } catch (CertificateEncodingException | IOException e) {
            logger.error("Error occurred while download certificate.", e);
            throw new RuntimeException(e);
        }

    }

    @PostMapping(value = "/valid/upload")
    public ResponseEntity<Boolean> checkValidityFromUploadedCertificate(@RequestBody String certificateEncoded) {

        logger.info("Validating certificate by upload.");

        byte[] certificateBytes = Base64.getDecoder().decode(certificateEncoded);

        try {
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            InputStream in = new ByteArrayInputStream(certificateBytes);
            X509Certificate cert = (X509Certificate) certFactory.generateCertificate(in);
            String serialNumber = cert.getSerialNumber().toString();
            System.out.println(serialNumber);

            Optional<AppCertificate> appCertificate = certificateService.findBySerialNumber(serialNumber);

            if (!appCertificate.isPresent())
                throw new CustomExceptionWithMessage("Certificate with that serial number does not exist!", HttpStatus.BAD_REQUEST);

            Boolean valid = certificateService.isCertificateValid(serialNumber);

            return new ResponseEntity<>(valid, HttpStatus.OK);

        } catch (CertificateException e) {
            System.out.println(e.getMessage());
            logger.error("Error occurred while validating certificate by upload.", e);
            return new ResponseEntity<>(false, HttpStatus.OK);
        }

    }

}
