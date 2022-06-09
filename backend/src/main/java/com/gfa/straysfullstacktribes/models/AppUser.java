package com.gfa.straysfullstacktribes.models;

import com.gfa.straysfullstacktribes.exceptions.defaultValuesExceptions.DefaultValueNotFoundException;
import com.gfa.straysfullstacktribes.exceptions.defaultValuesExceptions.DefaultValuesFileMissingException;
import com.gfa.straysfullstacktribes.exceptions.defaultValuesExceptions.IncorrectDefaultValueTypeException;
import com.gfa.straysfullstacktribes.models.enums.UserRole;
import com.gfa.straysfullstacktribes.utilities.DefaultValueReaderUtil;
import com.sun.istack.NotNull;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Entity
@Getter
public class AppUser implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true, nullable = false)
    private String username;

    @NotNull
    @Column(unique = true, nullable = false)
    private String email;

    @NotNull
    @Column(nullable = false)
    private String password;
    private boolean active;

    @NotNull
    @Column(unique = true, nullable = false)
    private String registrationToken;
    @NotNull
    @Column(nullable = false)
    private Long registrationTokenExpirationDate;

    @Column(unique = true)
    private String forgottenPasswordToken;
    private Long forgottenPasswordTokenExpirationDate;

    // I have added this for UserDetails - used for JWT Token
    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @OneToMany(mappedBy = "appUser", fetch = FetchType.EAGER)
    private List<Kingdom> kingdom;

    @OneToMany(mappedBy = "authorId")
    private List<Message> messages;

    @OneToMany(mappedBy = "ownerId")
    private List<Chat> chat;

    @OneToMany(mappedBy = "memberId")
    private List<ChatMember> chatMembers;

    public AppUser() {
        this.active = false;
        this.userRole = UserRole.PLAYER;
        this.kingdom = new ArrayList<>();
    }

    public AppUser(String username,
                   String email,
                   String password, //hashed form
                   String registrationToken) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.active = false;
        this.userRole = UserRole.PLAYER;
        this.registrationToken = registrationToken;
        try {
            this.registrationTokenExpirationDate = System.currentTimeMillis()/1000
                    + DefaultValueReaderUtil.getInt("tokenExpirationTime.registrationToken");
        } catch (DefaultValueNotFoundException | IncorrectDefaultValueTypeException | DefaultValuesFileMissingException e) {
            e.printStackTrace();
        }
        this.kingdom = new ArrayList<>();
    }

    public void setForgottenPasswordToken(String forgottenPasswordToken) {
        this.forgottenPasswordToken = forgottenPasswordToken;
        try {
            this.forgottenPasswordTokenExpirationDate = System.currentTimeMillis()/1000
                    + DefaultValueReaderUtil.getInt("tokenExpirationTime.forgottenPasswordToken");
        } catch (DefaultValueNotFoundException | IncorrectDefaultValueTypeException | DefaultValuesFileMissingException e) {
            e.printStackTrace();
        }
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRegistrationToken(String token) { this.registrationToken = token; }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setKingdoms(List<Kingdom> kingdoms) {
        this.kingdom = kingdoms;
    }

    public void addKingdom(Kingdom kingdom) {
        this.kingdom.add(kingdom);
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority =
                new SimpleGrantedAuthority(userRole.name());
        return Collections.singletonList(authority);
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !active;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}