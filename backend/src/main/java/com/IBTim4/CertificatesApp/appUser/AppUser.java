package com.IBTim4.CertificatesApp.appUser;

import com.IBTim4.CertificatesApp.appUser.dto.RegistrationRequestDTO;
import com.IBTim4.CertificatesApp.appUser.dto.UserExpandedDTO;
import lombok.*;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;

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

    public X500Name getX500Name() {
        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        builder.addRDN(BCStyle.CN, name + " " + lastName);
        builder.addRDN(BCStyle.SURNAME, name);
        builder.addRDN(BCStyle.GIVENNAME, lastName);
        builder.addRDN(BCStyle.E, email);
        builder.addRDN(BCStyle.UID, id.toString());
        return builder.build();
    }

}
