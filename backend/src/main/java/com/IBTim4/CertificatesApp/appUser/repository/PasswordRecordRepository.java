package com.IBTim4.CertificatesApp.appUser.repository;

import com.IBTim4.CertificatesApp.appUser.AppUser;
import com.IBTim4.CertificatesApp.appUser.PasswordRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;

public interface PasswordRecordRepository extends JpaRepository<PasswordRecord, Long> {

    public ArrayList<PasswordRecord> findTop3ByUserOrderByTimestampDesc(AppUser user);

}
