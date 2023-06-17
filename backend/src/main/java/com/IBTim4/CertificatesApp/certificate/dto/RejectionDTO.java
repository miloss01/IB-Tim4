package com.IBTim4.CertificatesApp.certificate.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RejectionDTO {
    @NotBlank(message = "Field (reason) is required!")
    @Size(max = 200, message = "Field (reason) cannot be longer than 200 characters!")
    private String reason;
}
