package com.IBTim4.CertificatesApp.certificate.controller;

import com.IBTim4.CertificatesApp.certificate.service.interfaces.ICertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

}
