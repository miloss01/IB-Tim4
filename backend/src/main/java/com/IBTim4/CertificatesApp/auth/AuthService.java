package com.IBTim4.CertificatesApp.auth;

import com.IBTim4.CertificatesApp.appUser.AppUser;
import com.IBTim4.CertificatesApp.appUser.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class AuthService implements UserDetailsService {

    @Autowired
    AppUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<AppUser> ret = userRepository.findByEmail(username);
        if (ret.isPresent()) {
            return org.springframework.security.core.userdetails.User
                    .withUsername(username)
                    .password(ret.get().getPassword())
                    .roles(ret.get().getRole().toString())
                    .build();
        }
        throw new UsernameNotFoundException("User not found with this username: " + username);
    }

}