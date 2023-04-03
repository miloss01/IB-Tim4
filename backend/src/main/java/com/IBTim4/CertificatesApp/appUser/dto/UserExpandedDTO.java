package com.IBTim4.CertificatesApp.appUser.dto;

import com.IBTim4.CertificatesApp.appUser.AppUser;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserExpandedDTO {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long id;
    private String name;
    private String lastName;
    private String phone;
    private String email;

    public UserExpandedDTO(AppUser user) {
        this(user.getId(), user.getName(), user.getLastName(), user.getPhone(), user.getEmail());
    }
}