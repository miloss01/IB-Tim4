package com.IBTim4.CertificatesApp.appUser.controller;

import com.IBTim4.CertificatesApp.appUser.AppUser;
import com.IBTim4.CertificatesApp.appUser.PasswordRecord;
import com.IBTim4.CertificatesApp.appUser.dto.LoginDTO;
import com.IBTim4.CertificatesApp.appUser.dto.RegistrationRequestDTO;
import com.IBTim4.CertificatesApp.appUser.dto.TokenResponseDTO;
import com.IBTim4.CertificatesApp.appUser.dto.UserExpandedDTO;
import com.IBTim4.CertificatesApp.appUser.dto.*;
import com.IBTim4.CertificatesApp.appUser.service.interfaces.IAppUserService;
import com.IBTim4.CertificatesApp.appUser.service.interfaces.IPasswordRecordService;
import com.IBTim4.CertificatesApp.auth.SavedJwt;
import com.IBTim4.CertificatesApp.auth.JwtTokenUtil;
import com.IBTim4.CertificatesApp.exceptions.CustomExceptionWithMessage;
import com.IBTim4.CertificatesApp.helper.TwilloConstants;
import com.twilio.Twilio;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.regex.Pattern;

@CrossOrigin(origins = "https://localhost:4200")
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

    @Autowired
    SavedJwt savedJwt;

    Logger logger = LoggerFactory.getLogger(AppUserController.class);

    @PostMapping(produces = "application/json", consumes = "application/json")
    public ResponseEntity<UserExpandedDTO> registerUser(@Valid @RequestBody RegistrationRequestDTO userDTO) {

        logger.info("Registration started.");

        Optional<AppUser> appUser = appUserService.findByEmail(userDTO.getEmail());

        if (appUser.isPresent()) {
            throw new CustomExceptionWithMessage("User with that email already exists!", HttpStatus.BAD_REQUEST);
        }
        userDTO.setPassword(new BCryptPasswordEncoder().encode(userDTO.getPassword()));
        AppUser saved = appUserService.saveAppUser(new AppUser(userDTO));

        logger.info("User successfully registered.");

        PasswordRecord passwordRecord = new PasswordRecord();
        passwordRecord.setPassword(new BCryptPasswordEncoder().encode(userDTO.getPassword()));
        passwordRecord.setUser(saved);
        passwordRecord.setTimestamp(LocalDateTime.now());
        PasswordRecord savedPasswordRecord = passwordRecordService.save(passwordRecord);

        return new ResponseEntity<>(new UserExpandedDTO(saved), HttpStatus.OK);
    }

    @PostMapping(value = "/login", consumes = "application/json", produces = "application/json")
    public ResponseEntity<TokenResponseDTO> login(@Valid @RequestBody LoginDTO loginDTO) {
        System.out.println("LOGIN");

        logger.info("Login started.");

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

        logger.info("Trying to log in user with ID: " + user.getId());

        String token = jwtTokenUtil.generateToken(
                loginDTO.getEmail(),
                user.getRole(),
                user.getId(),
                user.getPhone());

        Boolean refreshPassword = false;

        ArrayList<PasswordRecord> passwordRecords = passwordRecordService.findAllPasswordRecordsByUser(user);
        LocalDateTime lastChanged = passwordRecords.get(0).getTimestamp();
        System.out.println("lastChanged");
        System.out.println(lastChanged);
        LocalDateTime now = LocalDateTime.now();

        if (now.isAfter(lastChanged.plusDays(7L)))
            refreshPassword = true;

        logger.info("Login successful.");
        logger.info("Password rotation " + (refreshPassword ? "is" : "is not") + " required.");

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
    public ResponseEntity<String> generateOTP(@PathVariable String phoneNum) {

        logger.info("Generating OTP.");

        Twilio.init(TwilloConstants.accountSid, TwilloConstants.authToken);

        Verification verification = Verification.creator(
                        TwilloConstants.serviceSid, // this is your verification sid
                        phoneNum, //this is your Twilio verified recipient phone number
                        "sms") // this is your channel type
                .create();

        System.out.println(verification.getStatus());

        log.info("OTP has been successfully generated, and awaits your verification {}", LocalDateTime.now());
        logger.info("OTP successfully generated and sent.");

        return new ResponseEntity<>("Your OTP has been sent to your verified phone number", HttpStatus.OK);
    }

    @PostMapping("/verifyOTP/")
    public ResponseEntity<?> verifyUserOTP(@Valid @RequestBody TwilloDTO twilloDTO) {
        Twilio.init(TwilloConstants.accountSid, TwilloConstants.authToken);

        logger.info("Verifying OTP started.");

        try {

            VerificationCheck verificationCheck = VerificationCheck.creator(
                            TwilloConstants.serviceSid)
                    .setTo(twilloDTO.getPhone())
                    .setCode(twilloDTO.getCode())
                    .create();

            System.out.println(verificationCheck.getStatus());

        } catch (Exception e) {
            logger.error("Error occurred while verifying.", e);
            return new ResponseEntity<>("Verification failed.", HttpStatus.BAD_REQUEST);
        }
        logger.info("Verification is successful.");
        return new ResponseEntity<>("This user's verification has been completed successfully", HttpStatus.OK);
    }

    @GetMapping(value = "/generateEmailOTP/{email}")
    public ResponseEntity<String> generateEmailOTP(@PathVariable String email) {

        logger.info("Generating email OTP started.");

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
        logger.info("OTP successfully generated and sent.");

        return new ResponseEntity<>("Your OTP has been sent to your verified email", HttpStatus.OK);
    }

    @GetMapping("/verifyEmailOTP/{email}/{code}")
    public ResponseEntity<?> verifyUserEmailOTP(@PathVariable String email, @PathVariable String code) {
        Twilio.init(TwilloConstants.accountSid, TwilloConstants.authToken);

        logger.info("OTP email verification started.");

        try {

            VerificationCheck verificationCheck = VerificationCheck.creator(
                            TwilloConstants.serviceSid)
                    .setTo(email)
                    .setCode(code)
                    .create();

            System.out.println(verificationCheck.getSid());

        } catch (Exception e) {
            logger.info("Error occurred while verifying email OTP", e);
            return new ResponseEntity<>("Verification failed.", HttpStatus.BAD_REQUEST);
        }
        logger.info("OTP email verification is successful.");
        return new ResponseEntity<>("This user's verification has been completed successfully", HttpStatus.OK);

    }

    @PostMapping("/changePassword")
    public ResponseEntity<?> changePassword(@Valid @RequestBody PasswordChangeDTO twilloDTO) {

        Twilio.init(TwilloConstants.accountSid, TwilloConstants.authToken);

        logger.info("Changing password started.");

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

            logger.info("Password is successfully changed for user with ID: " + appUser.get().getId());

            PasswordRecord passwordRecord = new PasswordRecord();
            passwordRecord.setPassword(new BCryptPasswordEncoder().encode(twilloDTO.getPassword()));
            passwordRecord.setUser(appUser.get());
            passwordRecord.setTimestamp(LocalDateTime.now());
            PasswordRecord savedPasswordRecord = passwordRecordService.save(passwordRecord);

        } catch (Exception e) {
            System.out.println(e);
            logger.error("Error occurred while changing password.", e);
            return new ResponseEntity<>("Verification failed.", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Password change has been completed successfully", HttpStatus.OK);

    }

    @PostMapping(value = "/refreshPassword")
    public ResponseEntity refreshPassword(@RequestBody String newPassword) {

        if (!Pattern.matches("^(?=.*[0-9])(?=.*[!@#$%^&*])[a-zA-Z0-9!@#$%^&*\\s]{8,64}$", newPassword))
            throw new CustomExceptionWithMessage("Password must have 8 to 64 characters, 1 digit and 1 special character are required", HttpStatus.BAD_REQUEST);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Optional<AppUser> loggedIn = appUserService.findByEmail(email);

        logger.info("Password rotation started for user with ID: " + loggedIn.get().getId());

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

        logger.info("Password successfully rotated.");

        return new ResponseEntity(HttpStatus.OK);

    }

    @GetMapping("/google/oauth")
    public void redirectToGoogleLogin(HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorization/google");
    }

    @PostMapping("/google/login")
    public ResponseEntity<TokenResponseDTO> loginWithGoogle() {
        String jwt = savedJwt.getJwt();
        savedJwt.setJwt(null);
        TokenResponseDTO tokenResponseDTO = new TokenResponseDTO(jwt, "", false);
        return new ResponseEntity<>(tokenResponseDTO, HttpStatus.OK);
    }

}