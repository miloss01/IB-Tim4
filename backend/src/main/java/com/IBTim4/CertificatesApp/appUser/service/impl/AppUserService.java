package com.IBTim4.CertificatesApp.appUser.service.impl;

import com.IBTim4.CertificatesApp.appUser.AppUser;
import com.IBTim4.CertificatesApp.appUser.repository.AppUserRepository;
import com.IBTim4.CertificatesApp.appUser.service.interfaces.IAppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AppUserService implements IAppUserService {

    @Autowired
    AppUserRepository appUserRepository;

    @Override
    public Optional<AppUser> findByEmail(String email) {
        return appUserRepository.findByEmail(email);
    }

    @Override
    public Optional<AppUser> findById(Long id) {
        return appUserRepository.findById(id);
    }

    @Override
    public AppUser saveAppUser(AppUser appUser) {
        return appUserRepository.save(appUser);
    }

    @Override
    public AppUser changePassword(AppUser appUser, String newPassword) {
        appUser.setPassword(new BCryptPasswordEncoder().encode(newPassword));
        return appUserRepository.save(appUser);
    }

}