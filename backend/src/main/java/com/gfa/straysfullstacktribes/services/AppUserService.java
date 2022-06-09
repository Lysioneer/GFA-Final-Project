package com.gfa.straysfullstacktribes.services;

import com.gfa.straysfullstacktribes.exceptions.WrongConfirmationTokenException;
import com.gfa.straysfullstacktribes.models.AppUser;
import com.gfa.straysfullstacktribes.models.AppUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface AppUserService extends UserDetailsService {

    AppUser getUserByUsernameAndPassword(String username, String password);

    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

    Boolean userExists(String username);

    Boolean userPasswordMismatch(String username, String password);

    AppUser getUserByName(String username);

    Boolean hasKingdom(String username);

    Long extractAppUserIdFromToken(String token);
  
    AppUser getUserByRegistrationToken(String registrationToken) throws WrongConfirmationTokenException;

    String getRegistrationToken (String registrationToken) throws WrongConfirmationTokenException;

    void saveUser (AppUser user);

    Boolean emailExists(String email);

    AppUser getUserByUsernameEmailPassword(String username, String email, String password);

    AppUser getUserByEmail(String email);

    ResponseEntity<?> identifyUser(String token);
}