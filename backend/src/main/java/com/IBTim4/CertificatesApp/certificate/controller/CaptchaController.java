package com.IBTim4.CertificatesApp.certificate.controller;

import com.IBTim4.CertificatesApp.certificate.service.impl.CaptchaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/captcha")
@Validated
public class CaptchaController {

    @Autowired
    private CaptchaService captchaService;

    @GetMapping(value = "/validate", produces = "application/json")
    public ResponseEntity<Void> checkValidity(@RequestParam String token) {
        captchaService.processResponse(token);
        return new ResponseEntity<>(HttpStatus.OK);

    }

}
