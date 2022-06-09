package com.gfa.straysfullstacktribes.models;

import com.gfa.straysfullstacktribes.models.dtos.RegistrationRequestDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegistrationRequestDTOTest {

    @Test
    void registerNewUser() {
        RegistrationRequestDTO request = new RegistrationRequestDTO(
                "user",
                "user@gmail.com",
                "password",
                "adjkiwwidjiscjiicjsaidjaodoas");
        assertEquals("user", request.getUsername());
        assertEquals("user@gmail.com", request.getEmail());
        assertEquals("password", request.getPassword());
        assertEquals("adjkiwwidjiscjiicjsaidjaodoas", request.getRegistrationToken());
    }
}