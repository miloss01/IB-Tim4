package com.IBTim4.CertificatesApp.auth;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class SavedJwt {
    private String jwt;
}
