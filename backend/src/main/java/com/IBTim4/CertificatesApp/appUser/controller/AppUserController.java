package com.IBTim4.CertificatesApp.appUser.controller;

import com.IBTim4.CertificatesApp.appUser.AppUser;
import com.IBTim4.CertificatesApp.appUser.Role;
import com.IBTim4.CertificatesApp.appUser.dto.LoginDTO;
import com.IBTim4.CertificatesApp.appUser.dto.RegistrationRequestDTO;
import com.IBTim4.CertificatesApp.appUser.dto.TokenResponseDTO;
import com.IBTim4.CertificatesApp.appUser.dto.UserExpandedDTO;
import com.IBTim4.CertificatesApp.appUser.service.interfaces.IAppUserService;
import com.IBTim4.CertificatesApp.auth.JwtTokenUtil;
import com.IBTim4.CertificatesApp.exceptions.CustomExceptionWithMessage;
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
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/api/user")
@Validated
public class AppUserController {

    @Autowired
    JwtTokenUtil jwtTokenUtil;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    IAppUserService appUserService;

    @PostMapping(produces = "application/json", consumes = "application/json")
    public ResponseEntity<UserExpandedDTO> registerUser(@Valid @RequestBody RegistrationRequestDTO userDTO) {

        Optional<AppUser> appUser = appUserService.findByEmail(userDTO.getEmail());

        if (appUser.isPresent()) {
            throw new CustomExceptionWithMessage("User with that email already exists!", HttpStatus.BAD_REQUEST);
        }
        userDTO.setPassword(new BCryptPasswordEncoder().encode(userDTO.getPassword()));
        AppUser saved = appUserService.saveAppUser(new AppUser(userDTO));

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
                user.getId());

        return new ResponseEntity<>(
                new TokenResponseDTO(token, ""),
                HttpStatus.OK
        );
    }

    @PostMapping(value = "/login2", consumes = "application/json", produces = "application/json")
    public ResponseEntity<TokenResponseDTO> login2(@RequestBody LoginDTO loginDTO){
        System.out.println("LOGIN");
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword());
//        System.out.println(authRequest.getCredentials());
//        System.out.println(authRequest.getPrincipal());
        Authentication authentication = authenticationManager.authenticate(authRequest);
        System.out.println(authentication);
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);

        String role = securityContext.getAuthentication().getAuthorities().toString();
        Optional<AppUser> optionalAppUser= appUserService.findByEmail(loginDTO.getEmail());
        if (!optionalAppUser.isPresent()){
            throw new CustomExceptionWithMessage("User does not exist!", HttpStatus.NOT_FOUND);
        }
        AppUser user = optionalAppUser.get();
        String token = jwtTokenUtil.generateToken(user.getEmail(),
                Role.valueOf(role.substring(role.indexOf("_") + 1, role.length() - 1)),
                user.getId());

        return new ResponseEntity<>(
                new TokenResponseDTO(token, ""),
                HttpStatus.OK
        );
    }

}