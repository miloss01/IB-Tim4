package com.IBTim4.CertificatesApp.appUser.service.interfaces;

import com.IBTim4.CertificatesApp.appUser.AppUser;

import java.util.Optional;

public interface IAppUserService {

    Optional<AppUser> findByEmail(String email);
    AppUser saveAppUser(AppUser appUser);

}
