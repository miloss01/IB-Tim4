package com.IBTim4.CertificatesApp.appUser.service.interfaces;

import com.IBTim4.CertificatesApp.appUser.AppUser;
import com.IBTim4.CertificatesApp.appUser.PasswordRecord;

import java.util.ArrayList;

public interface IPasswordRecordService {

    public PasswordRecord save(PasswordRecord passwordRecord);
    public ArrayList<PasswordRecord> findAllPasswordRecordsByUser(AppUser user);

}
