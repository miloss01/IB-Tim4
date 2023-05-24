package com.IBTim4.CertificatesApp.appUser.service.impl;

import com.IBTim4.CertificatesApp.appUser.AppUser;
import com.IBTim4.CertificatesApp.appUser.PasswordRecord;
import com.IBTim4.CertificatesApp.appUser.repository.PasswordRecordRepository;
import com.IBTim4.CertificatesApp.appUser.service.interfaces.IPasswordRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class PasswordRecordService implements IPasswordRecordService {

    @Autowired
    private PasswordRecordRepository passwordRecordRepository;

    @Override
    public PasswordRecord save(PasswordRecord passwordRecord) {
        return passwordRecordRepository.save(passwordRecord);
    }
    @Override
    public ArrayList<PasswordRecord> findAllPasswordRecordsByUser(AppUser user) {
        return passwordRecordRepository.findTop3ByUserOrderByTimestampDesc(user);
    }
}
