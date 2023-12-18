package com.pilog.mdm.model;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Entity
@Table(name = "S_PERS_DETAIL")
public class SPersDetail extends CommonFields implements UserDetails {

	@Column(name = "AUDIT_ID", length = 4000)
    private String auditId;
	@Id
    @Column(name = "PERS_ID", unique = true, nullable = false, columnDefinition = "raw(16)")
    private String persId;
    @Column(name = "COUNTRY", nullable = false)
    private String country;
    @Column(name = "USER_NAME", unique = true, length = 200)
    private String userName;
    @Column(name = "FIRST_NAME", length = 200)
    private String firstName;
    @Column(name = "LAST_NAME", length = 200)
    private String lastName;
    @Column(name = "EMAIL", nullable = false, length = 300)
    private String email;
    @Column(name = "MOBILE", nullable = false, length = 128)
    private String mobile;
    @Column(name = "USER_AUTH", length =8000)
    private String userAuth;

//    @OneToOne(mappedBy = "SPersDetail")
//    private SAuthorisation sAuthorisations = new SAuthorisation();

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "SPersDetail",cascade = CascadeType.ALL)
    private Set<SAuthorisation> SAuthorisations = new HashSet<SAuthorisation>(0);

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "SPersDetail")
    private Set<SPersProfile> SPersProfiles = new HashSet<SPersProfile>(0);
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<String> roles = SPersProfiles.stream().map(SPersProfile::getRoleId).collect(Collectors.toList());
        List<SimpleGrantedAuthority> authorities=  roles.stream().map((role)-> new SimpleGrantedAuthority(role)).collect(Collectors.toList());
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getPassword() {
        String s = SAuthorisations.stream()
                .findFirst()
                .map(userCredentials -> userCredentials.getId().getPassPhrase())
                .orElse(null);
        System.out.println(s);
        return s;
    }

    @Override
    public String getUsername() {
        return userName;
    }

}
