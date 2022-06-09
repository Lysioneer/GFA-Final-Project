package com.gfa.straysfullstacktribes.controllers;

import com.gfa.straysfullstacktribes.models.Kingdom;
import com.gfa.straysfullstacktribes.models.AppUser;
import com.gfa.straysfullstacktribes.models.dtos.RegistrationRequestDTO;
import com.gfa.straysfullstacktribes.repositories.AppUserRepository;
import com.gfa.straysfullstacktribes.repositories.KingdomRepository;
import com.gfa.straysfullstacktribes.repositories.TroopRepository;
import com.gfa.straysfullstacktribes.services.AppUserService;
import com.gfa.straysfullstacktribes.services.KingdomService;
import com.gfa.straysfullstacktribes.services.MapService;
import com.gfa.straysfullstacktribes.services.RegistrationService;
import com.gfa.straysfullstacktribes.services.*;
import com.gfa.straysfullstacktribes.utilities.JwtUtilities;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RegistrationController.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class RegistrationControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    MapService mapService;
    @MockBean
    AppUserService appUserService;
    @MockBean
    KingdomService kingdomService;
    @MockBean
    AppUserRepository appUserRepository;
    @MockBean
    KingdomRepository kingdomRepository;
    @MockBean
    JwtUtilities jwtUtilities;
    @MockBean
    RegistrationService registrationService;
    @MockBean
    RegistrationRequestDTO registrationRequestDTO;
    @MockBean
    TroopRepository troopRepository;
    @MockBean
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @MockBean
    BuildingService buildingService;
    @MockBean
    UnitLevelService unitLevelService;

    @Test
    void correctRegistrationTest() throws Exception {

        Mockito.when(appUserService.getUserByUsernameAndPassword("mates", "12345678")).thenReturn(new AppUser());

        mvc.perform(MockMvcRequestBuilders.post("/registration/kingdom")
                        .content("{\"username\": \"mates\",\n" +
                                "    \"password\": \"12345678\",\n" +
                                "    \"kingdomName\": \"matesovo\",\n" +
                                "    \"coordinateX\": 20,\n" +
                                "    \"coordinateY\": 2}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"message\": \"Congratulations mates." +
                        " Your kingdom matesovo has been found at world map coordinates [20,2]!\",\n" +
                        "    \"status\": \"ok\"}"));
    }

    @Test
    void correctRegistrationWithoutSpecifiedKingdomNameTest() throws Exception {

        Mockito.when(appUserService.getUserByUsernameAndPassword("mates", "12345678")).thenReturn(new AppUser());

        mvc.perform(MockMvcRequestBuilders.post("/registration/kingdom")
                        .content("{\"username\": \"mates\",\n" +
                                "    \"password\": \"12345678\",\n" +
                                "    \"coordinateX\": 20,\n" +
                                "    \"coordinateY\": 2}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"message\": \"Congratulations mates. " +
                        "Your kingdom mates´s kingdom has been found at world map coordinates [20,2]!\",\n" +
                        "    \"status\": \"ok\"}"));
    }

    @Test
    void registrationKingdomWithWrongUsername() throws Exception {

        Mockito.when(appUserService.getUserByUsernameAndPassword("dummy", "12345678")).thenReturn(new AppUser());

        mvc.perform(MockMvcRequestBuilders.post("/registration/kingdom")
                        .content("{\"username\": \"mates\",\n" +
                                "    \"password\": \"12345678\",\n" +
                                "    \"kingdomName\": \"matesovo\",\n" +
                                "    \"coordinateX\": 20,\n" +
                                "    \"coordinateY\": 2}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error\": \"Wrong username or password!\"}"));
    }

    @Test
    void registrationKingdomWithWrongPassword() throws Exception {

        Mockito.when(appUserService.getUserByUsernameAndPassword("mates", "abcdefg")).thenReturn(new AppUser());

        mvc.perform(MockMvcRequestBuilders.post("/registration/kingdom")
                        .content("{\"username\": \"mates\",\n" +
                                "    \"password\": \"12345678\",\n" +
                                "    \"kingdomName\": \"matesovo\",\n" +
                                "    \"coordinateX\": 20,\n" +
                                "    \"coordinateY\": 2}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error\": \"Wrong username or password!\"}"));
    }

    @Test
    void registrationMissingUsernameTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/registration/kingdom")
                        .content("{\"password\": \"12345678\",\n" +
                                "    \"kingdomName\": \"matesovo\",\n" +
                                "    \"coordinateX\": 20,\n" +
                                "    \"coordinateY\": 2}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error\": \"Missing username!\"}"));
    }

    @Test
    void registrationMissingPasswordTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/registration/kingdom")
                        .content("{\"username\": \"mates\",\n" +
                                "    \"kingdomName\": \"matesovo\",\n" +
                                "    \"coordinateX\": 20,\n" +
                                "    \"coordinateY\": 2}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error\": \"Missing password!\"}"));
    }

    @Test
    void registrationShortPasswordTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/registration/kingdom")
                        .content("{\"username\": \"mates\",\n" +
                                "    \"password\": \"123\",\n" +
                                "    \"kingdomName\": \"matesovo\",\n" +
                                "    \"coordinateX\": 20,\n" +
                                "    \"coordinateY\": 2}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error\": \"Password must be at least 8 characters long!\"}"));
    }

    @Test
    void registrationKingdomPositionXOutOfRange() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/registration/kingdom")
                        .content("{\"username\": \"mates\",\n" +
                                "    \"password\": \"12345678\",\n" +
                                "    \"kingdomName\": \"matesovo\",\n" +
                                "    \"coordinateX\": 200,\n" +
                                "    \"coordinateY\": 2}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error\": \"Coordinates are out of range!\"}"));
    }

    @Test
    void registrationKingdomPositionYOutOfRange() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/registration/kingdom")
                        .content("{\"username\": \"mates\",\n" +
                                "    \"password\": \"12345678\",\n" +
                                "    \"kingdomName\": \"matesovo\",\n" +
                                "    \"coordinateX\": 20,\n" +
                                "    \"coordinateY\": 200}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error\": \"Coordinates are out of range!\"}"));
    }

    @Test
    void registrationKingdomIfCoordinatesAreNotEmpty() throws Exception {

        Mockito.when(mapService.getCoordinatesInfo2(1L, 1L)).thenReturn(Optional.of(new Kingdom()));

        mvc.perform(MockMvcRequestBuilders.post("/registration/kingdom")
                        .content("{\"username\": \"mates\",\n" +
                                "    \"password\": \"123456789\",\n" +
                                "    \"kingdomName\": \"matesovo\",\n" +
                                "    \"coordinateX\": 1,\n" +
                                "    \"coordinateY\": 1}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error\": \"Coordinates are already taken by another kingdom!\"}"));
    }

    @Test
    void registerNewUserTest() throws Exception {

        Mockito.when(appUserService.getUserByUsernameEmailPassword("newuser", "newuser@dummy.com", "password1")).thenReturn(new AppUser());

        mvc.perform(MockMvcRequestBuilders.post("/registration")
                        .content("{\"username\": \"newuser\"," +
                                "\"email\": \"newuser@dummy.com\"," +
                                "\"password\": \"password1\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"status\":\"ok\"}"));
    }

    @Test
    void registerNewUserNoInput() throws Exception {

        mvc.perform(MockMvcRequestBuilders.post("/registration")
                        .content("{\"username\": \"\"," +
                                "\"email\": \"\"," +
                                "\"password\": \"\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error\":\"Please provide username, email, and password.\"}"));

    }

    @Test
    void registerUsernameMissingTest() throws Exception {
        Mockito.when(registrationRequestDTO.getUsername()).thenReturn("user");
        Mockito.when(registrationRequestDTO.getEmail()).thenReturn("email@gmail.com");
        Mockito.when(registrationRequestDTO.getPassword()).thenReturn("password");

        mvc.perform(MockMvcRequestBuilders.post("/registration")
                        .content("{\"username\": \"\"," +
                                "\"email\": \"email@gmail.com\"," +
                                "\"password\": \"password\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error").value("All required fields must be filled out."));
    }

    @Test
    void registerEmailMissingTest() throws Exception {
        Mockito.when(registrationRequestDTO.getUsername()).thenReturn("user");
        Mockito.when(registrationRequestDTO.getEmail()).thenReturn("email@gmail.com");
        Mockito.when(registrationRequestDTO.getPassword()).thenReturn("password");

        mvc.perform(MockMvcRequestBuilders.post("/registration")
                        .content("{\"username\": \"user\"," +
                                "\"email\": \"\"," +
                                "\"password\": \"password\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error").value("All required fields must be filled out."));
    }

    @Test
    void registerPasswordMissingTest() throws Exception {
        Mockito.when(registrationRequestDTO.getUsername()).thenReturn("user");
        Mockito.when(registrationRequestDTO.getEmail()).thenReturn("email@gmail.com");
        Mockito.when(registrationRequestDTO.getPassword()).thenReturn("password");

        mvc.perform(MockMvcRequestBuilders.post("/registration")
                        .content("{\"username\": \"user\"," +
                                "\"email\": \"mail@gmail.com\"," +
                                "\"password\": \"\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error").value("All required fields must be filled out."));
    }


    @Test
    void registerPasswordTooShortTest() throws Exception {

        mvc.perform(MockMvcRequestBuilders.post("/registration")
                        .content("{\"username\": \"username1\"," +
                                "\"email\": \"dummy@dummy.com\"," +
                                "\"password\": \"pass\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error").value("Password must be at least 8 characters long!"));
    }

    @Test
    void userNameAlreadyExistsTest() throws Exception {

        Mockito.when(appUserService.getUserByName("username1")).thenReturn(new AppUser());

        mvc.perform(MockMvcRequestBuilders.post("/registration")
                        .content("{\"username\": \"username1\"," +
                                "\"email\": \"dummy@dummy.com\"," +
                                "\"password\": \"password\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error").value("Provided credentials are not valid."));
    }

    @Test
    void emailAlreadyExistsTest() throws Exception {

        Mockito.when(appUserService.getUserByEmail("dummy@dummy.com")).thenReturn(new AppUser());

        mvc.perform(MockMvcRequestBuilders.post("/registration")
                        .content("{\"username\": \"username1\"," +
                                "\"email\": \"dummy@dummy.com\"," +
                                "\"password\": \"password\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error").value("Provided credentials are not valid."));
    }
}