package com.IBTim4.CertificatesApp;

import com.IBTim4.CertificatesApp.certificate.keystore.KeyStoreWriter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CertificatesAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(CertificatesAppApplication.class, args);
		KeyStoreWriter keyStoreWriter = new KeyStoreWriter();
		System.out.println("aaaaaaaaa");
	}

}
