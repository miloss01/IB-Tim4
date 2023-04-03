package com.IBTim4.CertificatesApp.certificate.service.impl;

import com.IBTim4.CertificatesApp.Constants;
import com.IBTim4.CertificatesApp.appUser.AppUser;
import com.IBTim4.CertificatesApp.certificate.AppCertificate;
import com.IBTim4.CertificatesApp.certificate.CertificateRequest;
import com.IBTim4.CertificatesApp.certificate.CertificateType;
import com.IBTim4.CertificatesApp.certificate.keystore.KeyStoreReader;
import com.IBTim4.CertificatesApp.certificate.keystore.KeyStoreWriter;
import com.IBTim4.CertificatesApp.certificate.repository.CertificateRepository;
import com.IBTim4.CertificatesApp.certificate.service.interfaces.ICertificateService;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class CertificateService implements ICertificateService {

    @Autowired
    private CertificateRepository certificateRepository;
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

    @Override
    public AppCertificate createCertificate(CertificateRequest req) {

        JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256WithRSAEncryption");
        builder.setProvider("BC");

        Random rnd = new Random();
        String newSerialNumber = String.valueOf(rnd.nextInt() % 100000);

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

            Calendar cal = Calendar.getInstance();
            if (req.getCertificateType().equals(CertificateType.ROOT))
                cal.add(Calendar.MONTH, Constants.ROOT_DURATION);
            else if (req.getCertificateType().equals(CertificateType.INTERMEDIATE))
                cal.add(Calendar.MONTH, Constants.INTERMEDIATE_DURATION);
            else
                cal.add(Calendar.MONTH, Constants.END_DURATION);
            Date endDate = cal.getTime();

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

            AppCertificate saved = certificateRepository.save(appCertificate);

            return saved;

        } catch (OperatorCreationException | CertificateException e) {
            throw new RuntimeException(e);
        }

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
}
