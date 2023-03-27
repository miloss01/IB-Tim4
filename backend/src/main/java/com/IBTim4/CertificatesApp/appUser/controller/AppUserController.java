package com.IBTim4.CertificatesApp.appUser.controller;

import com.IBTim4.CertificatesApp.appUser.AppUser;
import com.IBTim4.CertificatesApp.appUser.dto.RegistrationRequestDTO;
import com.IBTim4.CertificatesApp.appUser.dto.UserExpandedDTO;
import com.IBTim4.CertificatesApp.appUser.service.interfaces.IAppUserService;
import com.IBTim4.CertificatesApp.exceptions.CustomExceptionWithMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/api/user")
@Validated
public class AppUserController {

    @Autowired
    IAppUserService appUserService;

    @PostMapping(produces = "application/json", consumes = "application/json")
    public ResponseEntity<UserExpandedDTO> registerUser(@Valid @RequestBody RegistrationRequestDTO userDTO) {

        Optional<AppUser> appUser = appUserService.findByEmail(userDTO.getEmail());

        if (appUser.isPresent()) {
            throw new CustomExceptionWithMessage("User with that email already exists!", HttpStatus.BAD_REQUEST);
        }

        AppUser saved = appUserService.saveAppUser(new AppUser(userDTO));

        return new ResponseEntity<>(new UserExpandedDTO(saved), HttpStatus.OK);
    }

}