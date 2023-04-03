package com.IBTim4.CertificatesApp.certificate.controller;

import com.IBTim4.CertificatesApp.certificate.AppCertificate;
import com.IBTim4.CertificatesApp.certificate.CertificateRequest;
import com.IBTim4.CertificatesApp.certificate.Rejection;
import com.IBTim4.CertificatesApp.certificate.RequestStatus;
import com.IBTim4.CertificatesApp.certificate.dto.CertificateDTO;
import com.IBTim4.CertificatesApp.certificate.dto.RejectionDTO;
import com.IBTim4.CertificatesApp.certificate.service.impl.CertificateRequestService;
import com.IBTim4.CertificatesApp.certificate.service.impl.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping(value = "api/certificate")
@Validated
public class CertificateController {
    @Autowired
    private CertificateService certificateService;
    @Autowired
    private CertificateRequestService certificateRequestService;

    @PostMapping(value = "/approveRequest/{requestId}")
    public ResponseEntity<CertificateDTO> approveCertificate(@PathVariable Integer requestId){
        CertificateRequest certificateRequest = certificateRequestService.getCertificateById(Long.valueOf(requestId));
//        certificateRequestService.authentify(certificateRequest.getIssuer().getSubject());
        certificateRequest.setStatus(RequestStatus.approved);
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = certificateRequestService.getEndTime(start, certificateRequest.getCertificateType());

        AppCertificate certificate = new AppCertificate(0L, start, end, certificateRequest.getRequester(),
                certificateRequest.getIssuer(), certificateRequest.getCertificateType());
        certificateService.saveCertificate(certificate);
        return new ResponseEntity<>(new CertificateDTO(certificate), HttpStatus.OK);
    }
//
    @PostMapping(value = "/declineRequest/{requestId}")
    public ResponseEntity<RejectionDTO> declineCertificate(@PathVariable Integer requestId, @RequestBody RejectionDTO rejectionDTO){
        CertificateRequest certificateRequest = certificateRequestService.getCertificateById(Long.valueOf(requestId));
        certificateRequest.setStatus(RequestStatus.denied);
        certificateRequest.setDescription(rejectionDTO.getReason());
//        Rejection rejection = new Rejection(0L, certificateRequest, rejectionDTO.getReason(), LocalDateTime.now());
//        certificateRequestService.save(rejection);
        certificateRequestService.save(certificateRequest);
        return new ResponseEntity<>(new RejectionDTO(), HttpStatus.OK);
    }
}
