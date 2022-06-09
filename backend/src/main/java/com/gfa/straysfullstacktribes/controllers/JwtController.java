package com.gfa.straysfullstacktribes.controllers;

import com.gfa.straysfullstacktribes.models.ErrorMessage;
import com.gfa.straysfullstacktribes.models.dtos.AuthenticationRequestDTO;
import com.gfa.straysfullstacktribes.models.dtos.AuthenticationResponseDTO;
import com.gfa.straysfullstacktribes.services.AppUserService;
import com.gfa.straysfullstacktribes.utilities.JwtUtilities;
import com.nimbusds.jose.shaded.json.JSONObject;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class JwtController {

    private JwtUtilities jwtUtilities;
    private AppUserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody(required = false) AuthenticationRequestDTO authenticationRequestDTO) {

        if (authenticationRequestDTO == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorMessage("Provide username and password in JSON!"));
        } else if (authenticationRequestDTO.getUsername() == null || authenticationRequestDTO.getPassword() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorMessage("Provide username and password!"));
        } else if (!userService.userExists(authenticationRequestDTO.getUsername())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorMessage("Provided credentials are not valid!"));
        } else if (!userService.userPasswordMismatch(authenticationRequestDTO.getUsername(), authenticationRequestDTO.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorMessage("Provided credentials are not valid!"));
        } else if (!userService.getUserByName(authenticationRequestDTO.getUsername()).isActive()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorMessage("Account not yet activated, check your e-mail"));
        } else if (!userService.hasKingdom(authenticationRequestDTO.getUsername())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorMessage("You do not have any Kingdoms yet!"));
        }

        final UserDetails userDetails = userService
                .loadUserByUsername(authenticationRequestDTO.getUsername());

        final String accessToken = jwtUtilities.generateToken(userDetails);

        return ResponseEntity.ok(new AuthenticationResponseDTO(accessToken));
    }
}