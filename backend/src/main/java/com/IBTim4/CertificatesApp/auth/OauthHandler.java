package com.IBTim4.CertificatesApp.auth;

import com.IBTim4.CertificatesApp.appUser.AppUser;
import com.IBTim4.CertificatesApp.appUser.PasswordRecord;
import com.IBTim4.CertificatesApp.appUser.Role;
import com.IBTim4.CertificatesApp.appUser.repository.AppUserRepository;
import com.IBTim4.CertificatesApp.appUser.service.interfaces.IPasswordRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OauthHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private final SavedJwt savedJwt;
    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private IPasswordRecordService passwordRecordService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        logger.info("Started OAuth login process");

        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;

            OAuth2User oauth2User = oauthToken.getPrincipal();
            String jwt = "";
            if (oauth2User instanceof DefaultOAuth2User) {
                DefaultOAuth2User defaultOAuth2User = (DefaultOAuth2User) oauth2User;
                String email = defaultOAuth2User.getAttribute("email");

                AppUser user;

                Optional<AppUser> userEntityOptional = appUserRepository.findByEmail(email);

                if (userEntityOptional.isPresent()) {
                    user = userEntityOptional.get();

                } else {

                    user = new AppUser();
                    user.setName(defaultOAuth2User.getAttribute("given_name"));
                    user.setLastName(defaultOAuth2User.getAttribute("family_name"));
                    user.setPhone("+381692532201");
                    user.setEmail(defaultOAuth2User.getAttribute("email"));
                    user.setPassword(new BCryptPasswordEncoder().encode("sifraza1!"));
                    user.setRole(Role.AUTHENTICATED_USER);

                    user = appUserRepository.save(user);

                    PasswordRecord passwordRecord = new PasswordRecord();
                    passwordRecord.setPassword(user.getPassword());
                    passwordRecord.setUser(user);
                    passwordRecord.setTimestamp(LocalDateTime.now());
                    PasswordRecord savedPasswordRecord = passwordRecordService.save(passwordRecord);
                }

                jwt = jwtTokenUtil.generateToken(user.getEmail(), user.getRole(), user.getId(), user.getPhone());
            }

            savedJwt.setJwt(jwt);

            logger.info("Successfully generated a jwt token with OAuth credentials");

            response.sendRedirect("https://localhost:4200/oauth-login");
        } else {
            logger.error("Unable to login with OAuth");
            OAuth2Error oauth2Error = new OAuth2Error(OAuth2ErrorCodes.INVALID_REQUEST);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
        }
    }

}
