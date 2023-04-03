package com.IBTim4.CertificatesApp.certificate.controller;

import com.IBTim4.CertificatesApp.certificate.AppCertificate;
import com.IBTim4.CertificatesApp.certificate.dto.CertificateDTO;
import com.IBTim4.CertificatesApp.certificate.service.interfaces.ICertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@CrossOrigin
@RestController
@RequestMapping("/api/cert")
public class CertificateController {

    @Autowired
    ICertificateService certificateService;

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
}
