package com.gfa.straysfullstacktribes.controllers;

import com.gfa.straysfullstacktribes.models.AppUser;
import com.gfa.straysfullstacktribes.models.dtos.AuthenticationRequestDTO;
import com.gfa.straysfullstacktribes.repositories.AppUserRepository;
import com.gfa.straysfullstacktribes.repositories.KingdomRepository;
import com.gfa.straysfullstacktribes.repositories.TroopRepository;
import com.gfa.straysfullstacktribes.services.AppUserService;
import com.gfa.straysfullstacktribes.services.EmailServiceImpl;
import com.gfa.straysfullstacktribes.services.RegistrationService;
import com.gfa.straysfullstacktribes.utilities.JwtUtilities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
class JwtControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private EmailServiceImpl emailService;

    @MockBean
    private AppUserRepository appUserRepository;

    @MockBean
    private KingdomRepository kingdomRepository;

    @MockBean
    private AuthenticationRequestDTO authenticationRequestDTO;

    @MockBean
    private AppUserService appUserService;

    @MockBean
    private RegistrationService registrationService;

    @MockBean
    private AppUser appUser;

    @MockBean
    private JwtUtilities jwtUtilities;  //it isn't directly used but needs to be there, test would not be able to read

    //token in login_test() method
    @BeforeEach
    void empty_repository_test() {
        assertEquals(0, appUserRepository.findAll().size());
        assertEquals(0, kingdomRepository.findAll().size());
    }

    @Test
    void login_no_arg_test() throws Exception {

        mvc.perform(post("/login"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error").value("Provide username and password in JSON!"));

        assertEquals(0, appUserRepository.findAll().size());
    }

    @Test
    void login_username_only_test() throws Exception {
        Mockito.when(authenticationRequestDTO.getUsername()).thenReturn("test");
        Mockito.when(authenticationRequestDTO.getPassword()).thenReturn("testPassword");

        mvc.perform(MockMvcRequestBuilders.post("/login")
                        .content("{\"username\":\"test\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error").value("Provide username and password!"));
    }

    @Test
    void login_password_only_test() throws Exception {
        Mockito.when(authenticationRequestDTO.getUsername()).thenReturn("test");
        Mockito.when(authenticationRequestDTO.getPassword()).thenReturn("testPassword");

        mvc.perform(MockMvcRequestBuilders.post("/login")
                        .content("{\"password\":\"testPassword\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error").value("Provide username and password!"));
    }

    @Test
    void login_bad_username_test() throws Exception {
        Mockito.when(authenticationRequestDTO.getUsername()).thenReturn("test");
        Mockito.when(authenticationRequestDTO.getPassword()).thenReturn("testPassword");

        mvc.perform(MockMvcRequestBuilders.post("/login")
                        .content("{\"username\":\"badTest\"," +
                                "\"password\":\"testPassword\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error").value("Provided credentials are not valid!"));
    }

    @Test
    void login_bad_password_test() throws Exception {
        AppUser mockUser = new AppUser("test", "test@mail.com", "testPassword", "xxxxx.yyyyy.zzzzz");

        Mockito.when(authenticationRequestDTO.getUsername()).thenReturn(mockUser.getUsername());
        Mockito.when(authenticationRequestDTO.getPassword()).thenReturn(mockUser.getPassword());
        Mockito.when(appUserService.userExists(authenticationRequestDTO.getUsername())).thenReturn(true);

        mvc.perform(MockMvcRequestBuilders.post("/login")
                        .content("{\"username\":\"test\"," +
                                "\"password\":\"badPassword\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error").value("Provided credentials are not valid!"));
    }

    @Test
    void login_user_disabled_test() throws Exception {
        AppUser mockUser = new AppUser("test", "test@mail.com", "testPassword", "xxxxx.yyyyy.zzzzz");

        Mockito.when(authenticationRequestDTO.getUsername()).thenReturn("test");
        Mockito.when(authenticationRequestDTO.getPassword()).thenReturn("testPassword");

        Mockito.when(appUserService.getUserByName(authenticationRequestDTO.getUsername()))
                .thenReturn(mockUser);
        Mockito.when(appUserService.userExists(authenticationRequestDTO.getUsername())).thenReturn(true);
        Mockito.when(appUserService
                        .userPasswordMismatch(authenticationRequestDTO.getUsername(), authenticationRequestDTO.getPassword()))
                .thenReturn(true);

        Mockito.when(appUserService.hasKingdom(authenticationRequestDTO.getUsername())).thenReturn(true);


        mvc.perform(MockMvcRequestBuilders.post("/login")
                        .content("{\"username\":\"test\"," +
                                "\"password\":\"testPassword\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("error").value("Account not yet activated, check your e-mail"));
    }


    @Test
    void login_no_kingdoms_test() throws Exception {
        AppUser mockUser = new AppUser("test", "test@mail.com", "testPassword", "xxxxx.yyyyy.zzzzz");
        mockUser.setActive(true);

        Mockito.when(authenticationRequestDTO.getUsername()).thenReturn("test");
        Mockito.when(authenticationRequestDTO.getPassword()).thenReturn("testPassword");

        Mockito.when(appUserService.getUserByName(authenticationRequestDTO.getUsername()))
                .thenReturn(mockUser);
        Mockito.when(appUserService.userExists(authenticationRequestDTO.getUsername())).thenReturn(true);
        Mockito.when(appUserService
                        .userPasswordMismatch(authenticationRequestDTO.getUsername(), authenticationRequestDTO.getPassword()))
                .thenReturn(true);

        Mockito.when(appUserService.hasKingdom(authenticationRequestDTO.getUsername())).thenReturn(false);

        mvc.perform(MockMvcRequestBuilders.post("/login")
                        .content("{\"username\":\"test\"," +
                                "\"password\":\"testPassword\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("error").value("You do not have any Kingdoms yet!"));
    }

    @Test
    void login_test() throws Exception {
        AppUser mockUser = new AppUser("test", "test@mail.com", "testPassword", "xxxxx.yyyyy.zzzzz");
        mockUser.setActive(true);

        Mockito.when(authenticationRequestDTO.getUsername()).thenReturn("test");
        Mockito.when(authenticationRequestDTO.getPassword()).thenReturn("testPassword");

        Mockito.when(appUserService.getUserByName(authenticationRequestDTO.getUsername()))
                .thenReturn(mockUser);
        Mockito.when(appUserService.userExists(authenticationRequestDTO.getUsername())).thenReturn(true);
        Mockito.when(appUserService
                        .userPasswordMismatch(authenticationRequestDTO.getUsername(), authenticationRequestDTO.getPassword()))
                .thenReturn(true);

        Mockito.when(appUserService.hasKingdom(authenticationRequestDTO.getUsername())).thenReturn(true);

        MvcResult result = mvc.perform(MockMvcRequestBuilders.post("/login")
                        .content("{\"username\":\"test\"," +
                                "\"password\":\"testPassword\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        response = response.replace("{\"token\": \"", "");
        String token = response.replace("\"}", "");

        mvc.perform(MockMvcRequestBuilders.get("/test")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }
}