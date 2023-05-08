package com.IBTim4.CertificatesApp.auth;

import com.IBTim4.CertificatesApp.appUser.AppUser;
import com.IBTim4.CertificatesApp.appUser.service.interfaces.IAppUserService;
import com.IBTim4.CertificatesApp.exceptions.CustomExceptionWithMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component("userSecurity")
public class UserSecurity {

    @Autowired
    private IAppUserService appUserService;

    public boolean hasUserId(Authentication authentication, Long userId) {

        String email = authentication.getName();

        Optional<AppUser> appUser = appUserService.findByEmail(email);

        if (!appUser.isPresent())
            throw new CustomExceptionWithMessage("User does not exist!", HttpStatus.NOT_FOUND);

        if (appUser.get().getId() != userId)
            throw new CustomExceptionWithMessage("You don't have access to that endpoint!", HttpStatus.FORBIDDEN);

        return true;

    }

}
