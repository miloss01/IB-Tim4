package com.IBTim4.CertificatesApp.appUser.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequestDTO {

    @Size(max = 100, message = "Field (name) cannot be longer than 100 characters!")
    private String name;
    @Size(max = 100, message = "Field (lastName) cannot be longer than 100 characters!")
    private String lastName;
//    @Size(max = 18, message = "Field (phone) cannot be longer than 18 characters!")
    @Pattern(regexp = "^(\\+)(3816)([0-9]){6,9}$", message = "Field phone must be formatted like +381691234567")
    private String phone;
    @Email(message = "Field (email) does not have valid format.")
    @Size(max = 100, message = "Field (email) cannot be longer than 100 characters!")
    private String email;
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[!@#$%^&*])[a-zA-Z0-9!@#$%^&*\\s]{8,64}$", message = "Field password must be have 8 to 64 characters, 1 digit and 1 special character are required")
    private String password;

}
