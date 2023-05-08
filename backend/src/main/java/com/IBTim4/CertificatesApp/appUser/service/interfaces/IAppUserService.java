package com.IBTim4.CertificatesApp.appUser.service.interfaces;

import com.IBTim4.CertificatesApp.appUser.AppUser;

import java.util.Optional;

public interface IAppUserService {

    Optional<AppUser> findByEmail(String email);
    Optional<AppUser> findById(Long id);
    AppUser saveAppUser(AppUser appUser);

    AppUser changePassword(AppUser appUser, String newPassword);

}
