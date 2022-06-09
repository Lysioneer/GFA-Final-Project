package com.gfa.straysfullstacktribes.services;

import com.gfa.straysfullstacktribes.exceptions.WrongConfirmationTokenException;
import com.gfa.straysfullstacktribes.models.AppUser;
import com.gfa.straysfullstacktribes.models.Kingdom;
import com.gfa.straysfullstacktribes.models.dtos.KingdomIdentifyDTO;
import com.gfa.straysfullstacktribes.models.dtos.UserIdentifyDTO;
import com.gfa.straysfullstacktribes.repositories.AppUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.*;

@Service
@AllArgsConstructor
public class AppUserServiceImpl implements AppUserService {

    private final static String USER_NOT_FOUND = "user %s not found";
    private final AppUserRepository appUserRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return appUserRepository.findAppUserByUsername(username).orElseThrow(() -> new
                UsernameNotFoundException(String.format(USER_NOT_FOUND, username)));
    }

    @Override
    public Boolean userExists(String username) {
        return appUserRepository.existsByUsername(username);
    }

    public String signUpUser(AppUser appUser) {
        boolean userExists = appUserRepository.existsByUsername(appUser.getUsername());

        if (userExists) {
            throw new IllegalStateException("Email is already taken");
        }

        String encodedPassword = bCryptPasswordEncoder.encode(appUser.getPassword());
        String token = UUID.randomUUID().toString();
        appUser.setPassword(encodedPassword);
        appUser.setRegistrationToken(token);
        appUserRepository.save(appUser);

        return token;
    }

    @Override
    public Boolean userPasswordMismatch(String username, String password) {
        AppUser user = appUserRepository.findByUsername(username);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.matches(password, user.getPassword());
    }

    @Override
    public AppUser getUserByName(String username) {
        Optional<AppUser> userOptional = appUserRepository.findAppUserByUsername(username);
        if (userOptional.isEmpty()) {
            return null;
        }
        return userOptional.get();
    }

    @Override
    public Boolean hasKingdom(String username) {
        AppUser user = appUserRepository.findByUsername(username);
        return user.getKingdom().size() >= 1;
    }

    @Override
    public AppUser getUserByRegistrationToken(String registrationToken) throws WrongConfirmationTokenException {
        Optional<AppUser> userOptional = appUserRepository
                .findAppUserByRegistrationToken(registrationToken);
        if (userOptional.isEmpty()) {
            throw new WrongConfirmationTokenException();
        }
        return userOptional.get();
    }

    @Override
    public String getRegistrationToken(String registrationToken) throws WrongConfirmationTokenException {
        return getUserByRegistrationToken(registrationToken).getRegistrationToken();
    }

    @Override
    public void saveUser(AppUser user) {
        appUserRepository.save(user);
    }

    @Override
    public AppUser getUserByUsernameAndPassword(String username, String password) {
        AppUser user = appUserRepository.findByUsername(username);
        if (user == null){
            return null;
        }
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if(encoder.matches(password, user.getPassword())) {
            return user;
        }else{
            return null;
        }
    }

    @Override
    public Boolean emailExists(String email) {
        return appUserRepository.existsByEmail(email);
    }

    @Override
    public AppUser getUserByUsernameEmailPassword(String username, String email, String password) {
        return appUserRepository.findByUsernameAndEmailAndPassword(username, email, password).get();
    }

    @Override
    public AppUser getUserByEmail(String email) {
        Optional<AppUser> userOptional = appUserRepository.findAppUserByEmail(email);
        if (userOptional.isEmpty()) {
            return null;
        }
        return userOptional.get();
    }

    //This will decode JwtToken and extract appUserName from it, then it will find appUserId from database
    @Override
    public Long extractAppUserIdFromToken(String token) {
        String subToken = token.substring(7);           //this will take the token and removes "Bearer " from it
        String[] chunks = subToken.split("\\.");  //This will split the actual token to two parts, header and payload
        Base64.Decoder decoder = Base64.getDecoder();   //used for decoding jwt token
        String decoded = new String(decoder.decode(chunks[1]));//this will decode the payload
        String[] splitDecoded = decoded.split("\"");//splits the payload
        String appUserName = splitDecoded[3];           //finds appUserName from splitDecoded
        AppUser appUser = appUserRepository.findByUsername(appUserName);
        return appUser.getId();
    }

    @Override
    public ResponseEntity<?> identifyUser(String token){
        if (token == null) {
            Map<String, String> body = new HashMap<>();
            body.put("error", "Access token provided was invalid!");
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }
        //decode token
        String subToken = token.substring(7);
        String[] chunks = subToken.split("\\.");
        Base64.Decoder decoder = Base64.getDecoder();
        String header = new String(decoder.decode(chunks[0]));
        String payload = new String(decoder.decode(chunks[1]));
        String[] splitDecoded = payload.split("\"");
        String appUserName = splitDecoded[3];
        AppUser appUser = appUserRepository.findByUsername(appUserName);

        UserIdentifyDTO dtoUser = new UserIdentifyDTO();
        dtoUser.setUsername(appUser.getUsername());

        for (Kingdom kingdom : appUser.getKingdom()) {
            KingdomIdentifyDTO dto = new KingdomIdentifyDTO();
            dto.setId(kingdom.getId());
            dto.setKingdomName(kingdom.getName());
            dtoUser.addKingdom(dto);
        }
        return ResponseEntity.ok().body(dtoUser);
    }
}
