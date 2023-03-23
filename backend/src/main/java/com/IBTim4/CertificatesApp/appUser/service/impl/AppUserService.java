package com.IBTim4.CertificatesApp.appUser.service.impl;

import com.IBTim4.CertificatesApp.appUser.repository.AppUserRepository;
import com.IBTim4.CertificatesApp.appUser.service.interfaces.IAppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppUserService implements IAppUserService {

    @Autowired
    AppUserRepository appUserRepository;

}