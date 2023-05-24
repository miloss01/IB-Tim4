package com.IBTim4.CertificatesApp.appUser.controller;

import com.IBTim4.CertificatesApp.appUser.AppUser;
import com.IBTim4.CertificatesApp.appUser.PasswordRecord;
import com.IBTim4.CertificatesApp.appUser.Role;
import com.IBTim4.CertificatesApp.appUser.dto.LoginDTO;
import com.IBTim4.CertificatesApp.appUser.dto.RegistrationRequestDTO;
import com.IBTim4.CertificatesApp.appUser.dto.TokenResponseDTO;
import com.IBTim4.CertificatesApp.appUser.dto.UserExpandedDTO;
import com.IBTim4.CertificatesApp.appUser.dto.*;
import com.IBTim4.CertificatesApp.appUser.service.interfaces.IAppUserService;
import com.IBTim4.CertificatesApp.appUser.service.interfaces.IPasswordRecordService;
import com.IBTim4.CertificatesApp.auth.JwtTokenUtil;
import com.IBTim4.CertificatesApp.exceptions.CustomExceptionWithMessage;
import com.IBTim4.CertificatesApp.helper.TwilloConstants;
import com.twilio.Twilio;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/api/user")
@Validated
@Slf4j
public class AppUserController {

    @Autowired
    JwtTokenUtil jwtTokenUtil;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    IAppUserService appUserService;
    @Autowired
    IPasswordRecordService passwordRecordService;


    @PostMapping(produces = "application/json", consumes = "application/json")
    public ResponseEntity<UserExpandedDTO> registerUser(@Valid @RequestBody RegistrationRequestDTO userDTO) {

        Optional<AppUser> appUser = appUserService.findByEmail(userDTO.getEmail());

        if (appUser.isPresent()) {
            throw new CustomExceptionWithMessage("User with that email already exists!", HttpStatus.BAD_REQUEST);
        }
        userDTO.setPassword(new BCryptPasswordEncoder().encode(userDTO.getPassword()));
        AppUser saved = appUserService.saveAppUser(new AppUser(userDTO));

        PasswordRecord passwordRecord = new PasswordRecord();
        passwordRecord.setPassword(new BCryptPasswordEncoder().encode(userDTO.getPassword()));
        passwordRecord.setUser(saved);
        passwordRecord.setTimestamp(LocalDateTime.now());
        PasswordRecord savedPasswordRecord = passwordRecordService.save(passwordRecord);

        return new ResponseEntity<>(new UserExpandedDTO(saved), HttpStatus.OK);
    }

    @PostMapping(value = "/login", consumes = "application/json", produces = "application/json")
    public ResponseEntity<TokenResponseDTO> login(@RequestBody LoginDTO loginDTO) {
        System.out.println("LOGIN");

        UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword());


        Authentication authentication = authenticationManager.authenticate(authReq);
        System.out.println(authentication);
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);
        String role = securityContext.getAuthentication().getAuthorities().toString();


        Optional<AppUser> optionalAppUser= appUserService.findByEmail(loginDTO.getEmail());
        if (!optionalAppUser.isPresent()){
            throw new CustomExceptionWithMessage("User does not exist!", HttpStatus.NOT_FOUND);
        }
        AppUser user = optionalAppUser.get();

        String token = jwtTokenUtil.generateToken(
                loginDTO.getEmail(),
                user.getRole(),
                user.getId(),
                user.getPhone());

        Boolean refreshPassword = false;

        ArrayList<PasswordRecord> passwordRecords = passwordRecordService.findAllPasswordRecordsByUser(user);
        LocalDateTime lastChanged = passwordRecords.get(0).getTimestamp();
        LocalDateTime now = LocalDateTime.now();

        if (now.isAfter(lastChanged.plusDays(7L)))
            refreshPassword = true;

        return new ResponseEntity<>(
                new TokenResponseDTO(token, "", refreshPassword),
                HttpStatus.OK
        );
    }

//    @PostMapping(value = "/login2", consumes = "application/json", produces = "application/json")
//    public ResponseEntity<TokenResponseDTO> login2(@RequestBody LoginDTO loginDTO){
//        System.out.println("LOGIN");
//        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword());
////        System.out.println(authRequest.getCredentials());
////        System.out.println(authRequest.getPrincipal());
//        Authentication authentication = authenticationManager.authenticate(authRequest);
//        System.out.println(authentication);
//        SecurityContext securityContext = SecurityContextHolder.getContext();
//        securityContext.setAuthentication(authentication);
//
//        String role = securityContext.getAuthentication().getAuthorities().toString();
//        Optional<AppUser> optionalAppUser= appUserService.findByEmail(loginDTO.getEmail());
//        if (!optionalAppUser.isPresent()){
//            throw new CustomExceptionWithMessage("User does not exist!", HttpStatus.NOT_FOUND);
//        }
//        AppUser user = optionalAppUser.get();
//        String token = jwtTokenUtil.generateToken(user.getEmail(),
//                Role.valueOf(role.substring(role.indexOf("_") + 1, role.length() - 1)),
//                user.getId());
//
//        return new ResponseEntity<>(
//                new TokenResponseDTO(token, ""),
//                HttpStatus.OK
//        );
//    }
    @GetMapping(value = "/generateOTP/{phoneNum}")
    public ResponseEntity<String> generateOTP(@PathVariable String phoneNum){

        Twilio.init(TwilloConstants.accountSid, TwilloConstants.authToken);

        Verification verification = Verification.creator(
                        TwilloConstants.serviceSid, // this is your verification sid
                        phoneNum, //this is your Twilio verified recipient phone number
                        "sms") // this is your channel type
                .create();

        System.out.println(verification.getStatus());

        log.info("OTP has been successfully generated, and awaits your verification {}", LocalDateTime.now());

        return new ResponseEntity<>("Your OTP has been sent to your verified phone number", HttpStatus.OK);
    }

    @PostMapping("/verifyOTP/")
    public ResponseEntity<?> verifyUserOTP(@RequestBody TwilloDTO twilloDTO) {
        Twilio.init(TwilloConstants.accountSid, TwilloConstants.authToken);

        try {

            VerificationCheck verificationCheck = VerificationCheck.creator(
                            TwilloConstants.serviceSid)
                    .setTo(twilloDTO.getPhone())
                    .setCode(twilloDTO.getCode())
                    .create();

            System.out.println(verificationCheck.getStatus());

        } catch (Exception e) {
            return new ResponseEntity<>("Verification failed.", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("This user's verification has been completed successfully", HttpStatus.OK);
    }

    @GetMapping(value = "/generateEmailOTP/{email}")
    public ResponseEntity<String> generateEmailOTP(@PathVariable String email){

        Twilio.init(TwilloConstants.accountSid, TwilloConstants.authToken);
        Verification verification = Verification.creator(
                        TwilloConstants.serviceSid,
                        email,
                        "email")
                .setChannelConfiguration(
                        new HashMap<String, Object>()
                        {{
                            put("template_id", "d-f1e644294d944c5d82c6577692b25d58");
                            put("from", "milutin.sv39.2020@uns.ac.rs");
                            put("from_name", "IB2023");
                        }})
                .create();

        log.info("OTP has been successfully generated, and awaits your verification {}", LocalDateTime.now());

        return new ResponseEntity<>("Your OTP has been sent to your verified email", HttpStatus.OK);
    }

    @GetMapping("/verifyEmailOTP/{email}/{code}")
    public ResponseEntity<?> verifyUserEmailOTP(@PathVariable String email, @PathVariable String code) {
        Twilio.init(TwilloConstants.accountSid, TwilloConstants.authToken);

        try {

            VerificationCheck verificationCheck = VerificationCheck.creator(
                            TwilloConstants.serviceSid)
                    .setTo(email)
                    .setCode(code)
                    .create();

            System.out.println(verificationCheck.getSid());

        } catch (Exception e) {
            return new ResponseEntity<>("Verification failed.", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("This user's verification has been completed successfully", HttpStatus.OK);

    }

    @PostMapping("/changePassword")
    public ResponseEntity<?> changePassword(@RequestBody PasswordChangeDTO twilloDTO) {

        Twilio.init(TwilloConstants.accountSid, TwilloConstants.authToken);

        try {
            System.out.println("ovde");
            VerificationCheck verificationCheck = VerificationCheck.creator(
                            TwilloConstants.serviceSid)
                    .setTo(twilloDTO.getPhone())
                    .setCode(twilloDTO.getCode())
                    .create();

            System.out.println(verificationCheck.getStatus());

            Optional<AppUser> appUser = appUserService.findByEmail(twilloDTO.getEmail());

            if (!appUser.isPresent()) {
                throw new CustomExceptionWithMessage("User with that email doesn't exists!", HttpStatus.BAD_REQUEST);
            }

            ArrayList<PasswordRecord> passwordRecords = passwordRecordService.findAllPasswordRecordsByUser(appUser.get());

            for (PasswordRecord passwordRecord : passwordRecords)
                if (new BCryptPasswordEncoder().matches(twilloDTO.getPassword(), passwordRecord.getPassword()))
                    throw new CustomExceptionWithMessage("New password cannot be the same as any of the last three!", HttpStatus.BAD_REQUEST);

            appUserService.changePassword(appUser.get(), twilloDTO.getPassword());

            PasswordRecord passwordRecord = new PasswordRecord();
            passwordRecord.setPassword(new BCryptPasswordEncoder().encode(twilloDTO.getPassword()));
            passwordRecord.setUser(appUser.get());
            passwordRecord.setTimestamp(LocalDateTime.now());
            PasswordRecord savedPasswordRecord = passwordRecordService.save(passwordRecord);

        } catch (Exception e) {
            System.out.println(e);
            return new ResponseEntity<>("Verification failed.", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Password change has been completed successfully", HttpStatus.OK);

    }

    @PostMapping(value = "/refreshPassword")
    public ResponseEntity refreshPassword(@RequestBody String newPassword) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Optional<AppUser> loggedIn = appUserService.findByEmail(email);

        ArrayList<PasswordRecord> passwordRecords = passwordRecordService.findAllPasswordRecordsByUser(loggedIn.get());

        for (PasswordRecord passwordRecord : passwordRecords) {
            System.out.println(passwordRecord.getPassword());
            if (new BCryptPasswordEncoder().matches(newPassword, passwordRecord.getPassword()))
                throw new CustomExceptionWithMessage("New password cannot be the same as any of the last three!", HttpStatus.BAD_REQUEST);
        }

        String newPass = new BCryptPasswordEncoder().encode(newPassword);

        PasswordRecord passwordRecord = new PasswordRecord();
        passwordRecord.setPassword(newPass);
        passwordRecord.setUser(loggedIn.get());
        passwordRecord.setTimestamp(LocalDateTime.now());
        PasswordRecord savedPasswordRecord = passwordRecordService.save(passwordRecord);

        loggedIn.get().setPassword(newPass);
        appUserService.saveAppUser(loggedIn.get());

        return new ResponseEntity(HttpStatus.OK);

    }

}