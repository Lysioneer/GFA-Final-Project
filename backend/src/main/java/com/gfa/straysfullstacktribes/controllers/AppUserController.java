package com.gfa.straysfullstacktribes.controllers;

import com.gfa.straysfullstacktribes.services.AppUserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class AppUserController {

    private final AppUserService appUserService;

    @GetMapping("/identify")
    public ResponseEntity<?> identifyUser(@RequestHeader(value = "Authorization", required = false) String token) {
        return appUserService.identifyUser(token);
    }

}
