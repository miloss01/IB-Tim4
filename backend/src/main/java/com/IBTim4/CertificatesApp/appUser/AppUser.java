package com.IBTim4.CertificatesApp.appUser;

import com.IBTim4.CertificatesApp.appUser.dto.RegistrationRequestDTO;
import com.IBTim4.CertificatesApp.appUser.dto.UserExpandedDTO;
import lombok.*;

import javax.persistence.*;

import static javax.persistence.InheritanceType.JOINED;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
@TableGenerator(name="appUser_id_generator", table="primary_keys", pkColumnName="key_pk", pkColumnValue="appUser", valueColumnName="value_pk")
@Inheritance(strategy=JOINED)
@Table(name = "app_user")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    private String name;

    private String lastName;

    private String phone;

    private String email;

    private String password;

    private Role role;

    public AppUser(RegistrationRequestDTO userDTO) {
        this.setName(userDTO.getName());
        this.setLastName(userDTO.getLastName());
        this.setPhone(userDTO.getPhone());
        this.setEmail(userDTO.getEmail());
        this.setPassword(userDTO.getPassword());
        this.setRole(Role.AUTHENTICATED_USER);
    }

}
