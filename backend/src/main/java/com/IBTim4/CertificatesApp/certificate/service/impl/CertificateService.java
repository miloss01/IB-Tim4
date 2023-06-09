package com.IBTim4.CertificatesApp.certificate.service.impl;

import com.IBTim4.CertificatesApp.Constants;
import com.IBTim4.CertificatesApp.appUser.AppUser;
import com.IBTim4.CertificatesApp.certificate.AppCertificate;
import com.IBTim4.CertificatesApp.certificate.CertificateType;
import com.IBTim4.CertificatesApp.certificate.repository.CertificateRepository;
import com.IBTim4.CertificatesApp.certificate.service.interfaces.ICertificateService;
import com.IBTim4.CertificatesApp.exceptions.CustomExceptionWithMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.security.cert.Certificate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import com.IBTim4.CertificatesApp.certificate.CertificateRequest;
import com.IBTim4.CertificatesApp.certificate.keystore.KeyStoreReader;
import com.IBTim4.CertificatesApp.certificate.keystore.KeyStoreWriter;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.ZoneId;
import java.util.*;

@Service
public class CertificateService implements ICertificateService {

    @Autowired
    CertificateRepository certificateRepository;
    private KeyStoreReader keyStoreReader = new KeyStoreReader(Constants.keyStoreFile);

    @Override
    public ArrayList<AppCertificate> getAllCertificates() {
        return new ArrayList<>(certificateRepository.findAll());
    }

    @Override
    public Optional<AppCertificate> findBySerialNumber(String serialNumber) {
        return certificateRepository.findBySerialNumber(serialNumber);
    }

    @Override
    public ArrayList<AppCertificate> findByAllBySubject(AppUser subject) {
        return certificateRepository.findAllBySubject(subject);
    }

    @Override
    public AppCertificate save(AppCertificate appCertificate) {
        return certificateRepository.save(appCertificate);
    }

    private String generateSerialNumber() {
        Boolean isPresent = true;
        Random rnd = new Random();
        Integer sn = -1;

        while (isPresent) {
            sn = rnd.nextInt() % 10000000;
            if (sn < 0)
                sn *= -1;
            Optional<AppCertificate> cert = findBySerialNumber(String.valueOf(sn));
            isPresent = cert.isPresent();
        }

        return String.valueOf(sn);
    }

    @Override
    public AppCertificate createCertificate(CertificateRequest req) {

        JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256WithRSAEncryption");
        builder.setProvider("BC");

        String newSerialNumber = generateSerialNumber();
//        String newSerialNumber = UUID.randomUUID().toString();

        KeyPair keyPairSubject = generateKeyPair();
        PrivateKey privateKey;

        AppCertificate issuer = null;

        if (req.getCertificateType().equals(CertificateType.ROOT)) {
            privateKey = keyPairSubject.getPrivate();
        } else {
            String issuerSN = req.getIssuer().getSerialNumber();
            System.out.println(issuerSN);
            privateKey = keyStoreReader.readPrivateKey(Constants.keyStorePass, issuerSN, Constants.aliasPass);
            issuer = req.getIssuer();
        }

        try {
            Date startDate = new Date();

//            Calendar cal = Calendar.getInstance();
//            if (req.getCertificateType().equals(CertificateType.ROOT))
//                cal.add(Calendar.MONTH, Constants.ROOT_DURATION);
//            else if (req.getCertificateType().equals(CertificateType.INTERMEDIATE))
//                cal.add(Calendar.MONTH, Constants.INTERMEDIATE_DURATION);
//            else
//                cal.add(Calendar.MONTH, Constants.END_DURATION);
//            Date endDate = cal.getTime();
            Date endDate = java.sql.Timestamp.valueOf(req.getExpirationTime());

            ContentSigner contentSigner = builder.build(privateKey);

            X500Name issuerName;
            if (issuer == null)
                issuerName = req.getRequester().getX500Name();
            else
                issuerName = req.getIssuer().getSubject().getX500Name();

            X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(
                    issuerName,
                    new BigInteger(newSerialNumber),
                    startDate,
                    endDate,
                    req.getRequester().getX500Name(),
                    keyPairSubject.getPublic());

            X509CertificateHolder certHolder = certGen.build(contentSigner);

            JcaX509CertificateConverter certConverter = new JcaX509CertificateConverter();
            certConverter.setProvider("BC");

            X509Certificate cert = certConverter.getCertificate(certHolder);

            KeyStoreWriter keyStoreWriter = new KeyStoreWriter();
            keyStoreWriter.loadKeyStore(Constants.keyStoreFile, Constants.keyStorePass.toCharArray());
            keyStoreWriter.write(newSerialNumber, keyPairSubject.getPrivate(), Constants.aliasPass.toCharArray(), cert);
            keyStoreWriter.saveKeyStore(Constants.keyStoreFile, Constants.keyStorePass.toCharArray());

            AppCertificate appCertificate = new AppCertificate();
            appCertificate.setIssuer(req.getIssuer());
            appCertificate.setType(req.getCertificateType());
            appCertificate.setSubject(req.getRequester());
            appCertificate.setStartTime(startDate.toInstant()
                                                .atZone(ZoneId.systemDefault())
                                                .toLocalDateTime());
            appCertificate.setEndTime(endDate.toInstant()
                                            .atZone(ZoneId.systemDefault())
                                            .toLocalDateTime());
            appCertificate.setSerialNumber(newSerialNumber);
            appCertificate.setReasonForRetracting(null);

            AppCertificate saved = certificateRepository.save(appCertificate);

            return saved;

        } catch (OperatorCreationException | CertificateException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Boolean checkCertificateExpirationTime(LocalDateTime expirationTime, CertificateType type) {
        LocalDateTime now = LocalDateTime.now();
        if (type == CertificateType.ROOT) {
            if (expirationTime.isAfter(now.plusMonths(Constants.ROOT_DURATION)))
                throw new CustomExceptionWithMessage("Root certificate can last at most 12 months!", HttpStatus.BAD_REQUEST);
        } else if (type == CertificateType.INTERMEDIATE) {
            if (expirationTime.isAfter(now.plusMonths(Constants.INTERMEDIATE_DURATION)))
                throw new CustomExceptionWithMessage("Intermediate certificate can last at most 6 months!", HttpStatus.BAD_REQUEST);
        } else {
            if (expirationTime.isAfter(now.plusMonths(Constants.END_DURATION)))
                throw new CustomExceptionWithMessage("End certificate can last at most 3 months!", HttpStatus.BAD_REQUEST);
        }

        return true;
    }

    @Override
    public Boolean retractCertificate(AppCertificate certificate, String reason) {

        certificate.setRetracted(true);
        certificate.setReasonForRetracting(reason);
        certificateRepository.save(certificate);

        ArrayList<AppCertificate> issued = certificateRepository.findAllByIssuer(certificate);

        for (AppCertificate cert : issued)
            retractCertificate(cert, reason);

        return true;
    }

    @Override
    public Certificate downloadCertificate(String serialNumber) {
        return keyStoreReader.readCertificate(Constants.keyStorePass, serialNumber);
    }

    @Override
    public PrivateKey downloadPrivateKey(String serialNumber) {
        return keyStoreReader.readPrivateKey(Constants.keyStorePass, serialNumber, Constants.aliasPass);
    }

    private KeyPair generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            keyGen.initialize(2048, random);
            return keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean isCertificateValid(String serialNumber) {
        Optional<AppCertificate> certOptional = certificateRepository.findBySerialNumber(serialNumber);

        if (!certOptional.isPresent())
            throw new CustomExceptionWithMessage("Certificate with given serial number does not exist.",
                    HttpStatus.NOT_FOUND);
        AppCertificate cert = certOptional.get();

        if (!cert.isValid() || !checkPublicKeyValidity(serialNumber)) return false;
        else if (cert.getType().equals(CertificateType.ROOT)) return true;

        AppCertificate parentCert = cert.getIssuer();
        while (!parentCert.getType().equals(CertificateType.ROOT)) {
            if (parentCert.isValid() && checkPublicKeyValidity(parentCert.getSerialNumber()))
                parentCert = parentCert.getIssuer();
            else return false;
        }

        return parentCert.isValid() && checkPublicKeyValidity(parentCert.getSerialNumber());

    }

    private Boolean checkPublicKeyValidity(String serialNumber) {
        Certificate certificate = downloadCertificate(serialNumber);
        Optional<AppCertificate> appCertificate = findBySerialNumber(serialNumber);

        AppCertificate issuer = appCertificate.get().getIssuer();
        PublicKey issuerPublicKey = null;

        if (issuer == null)
            issuerPublicKey = certificate.getPublicKey();
        else {
            Certificate issuerCertificate = downloadCertificate(issuer.getSerialNumber());
            issuerPublicKey = issuerCertificate.getPublicKey();
        }

        try {
            certificate.verify(issuerPublicKey);
            return true;
        } catch (CertificateException |
                 NoSuchAlgorithmException |
                 InvalidKeyException |
                 NoSuchProviderException |
                 SignatureException e) {
            return false;
        }
    }

    public AppCertificate saveCertificate(AppCertificate certificate) {
        return certificateRepository.save(certificate);
    }

    @Override
    public void createCertificate(AppCertificate appCertificate) {

    }
}
