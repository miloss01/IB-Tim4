package com.IBTim4.CertificatesApp;

import com.IBTim4.CertificatesApp.certificate.keystore.KeyStoreWriter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.security.Security;

@SpringBootApplication
public class CertificatesAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(CertificatesAppApplication.class, args);
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

		File f = new File(Constants.keyStoreFile);
		if(!f.exists()) {
			KeyStoreWriter keyStoreWriter = new KeyStoreWriter();
			keyStoreWriter.loadKeyStore(null, Constants.keyStorePass.toCharArray());
			keyStoreWriter.saveKeyStore(Constants.keyStoreFile, Constants.keyStorePass.toCharArray());
		}
	}

}
