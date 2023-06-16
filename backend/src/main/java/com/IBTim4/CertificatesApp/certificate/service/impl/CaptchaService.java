package com.IBTim4.CertificatesApp.certificate.service.impl;

import com.IBTim4.CertificatesApp.Constants;
import com.IBTim4.CertificatesApp.certificate.dto.GoogleResponse;
import com.IBTim4.CertificatesApp.exceptions.CustomExceptionWithMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Service
public class CaptchaService {

    @Autowired
    private RestTemplate restTemplate;

    public void processResponse(String response) {

        URI verifyUri = URI.create(String.format(
                "https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s",
                Constants.recaptchaSecretKey, response));

        GoogleResponse googleResponse = restTemplate.getForObject(verifyUri, GoogleResponse.class);

        if(googleResponse != null && !googleResponse.isSuccess()) {
            System.out.println(googleResponse.toString());
            throw new CustomExceptionWithMessage("reCaptcha was not successfully validated", HttpStatus.BAD_REQUEST);
        }
    }

}
