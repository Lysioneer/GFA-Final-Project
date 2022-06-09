package com.gfa.straysfullstacktribes.controllers;

import com.gfa.straysfullstacktribes.models.Kingdom;
import com.gfa.straysfullstacktribes.models.dtos.KingdomInfoDTO;
import com.gfa.straysfullstacktribes.models.dtos.KingdomTroopsDTO;
import com.gfa.straysfullstacktribes.models.dtos.TroopDTO;
import com.gfa.straysfullstacktribes.models.enums.TroopType;
import com.gfa.straysfullstacktribes.repositories.AppUserRepository;
import com.gfa.straysfullstacktribes.repositories.KingdomRepository;
import com.gfa.straysfullstacktribes.repositories.TroopRepository;
import com.gfa.straysfullstacktribes.services.AppUserServiceImpl;
import com.gfa.straysfullstacktribes.services.EmailServiceImpl;
import com.gfa.straysfullstacktribes.services.KingdomService;
import com.gfa.straysfullstacktribes.services.MapService;
import com.gfa.straysfullstacktribes.utilities.JwtUtilities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
class KingdomControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private EmailServiceImpl emailService;

    @MockBean
    private KingdomService kingdomService;

    @MockBean
    private AppUserServiceImpl appUserService;

    @MockBean
    private AppUserRepository appUserRepository;

    @MockBean
    private KingdomRepository kingdomRepository;

    @MockBean
    private MapService mapService;

    @MockBean
    private TroopRepository troopRepository;

    @MockBean
    private JwtUtilities jwtUtilities;  //it isn't directly used but needs to be there, test would not be able to read
    //token in login_test() method

    @MockBean
    private KingdomTroopsDTO kingdomTroopsDTO;

    @MockBean
    private TroopDTO troopDTO;

    @MockBean
    private Kingdom kingdom;

    @BeforeEach
    void test_empty_repos_test() {
        assertEquals(0, appUserRepository.findAll().size());
        assertEquals(0, kingdomRepository.findAll().size());
    }

    @Test
    void niIdResponse_test() throws Exception {
        String mockToken = "mockToken";

        mvc.perform(MockMvcRequestBuilders
                        .put("/kingdoms/ ")
                        .header("Authorization", "Bearer " + mockToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error")
                        .value("Provide Kingdom's Id in url path, example: /kingdoms/1"));
    }

    @Test
    void rename_kingdom_no_DTO_test() throws Exception {
        String mockToken = "mockToken";

        mvc.perform(MockMvcRequestBuilders
                        .put("/kingdoms/1")
                        .header("Authorization", "Bearer " + mockToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error")
                        .value("Provide Kingdom's name in JSON!"));
    }

    @Test
    void rename_kingdom_id_too_low_test() throws Exception {
        String mockToken = "mockToken";

        Mockito.when(kingdomService.kingdomRepoSize()).thenReturn(3);

        mvc.perform(MockMvcRequestBuilders
                        .put("/kingdoms/0")
                        .header("Authorization", "Bearer " + mockToken)
                        .content("{\"kingdomName\":\"renameKingdom\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("error")
                        .value("Entered Kingdom id not exists!"));
    }

    @Test
    void rename_kingdom_id_too_high_test() throws Exception {
        String mockToken = "mockToken";

        Mockito.when(kingdomService.kingdomRepoSize()).thenReturn(3);

        mvc.perform(MockMvcRequestBuilders
                        .put("/kingdoms/4")
                        .header("Authorization", "Bearer " + mockToken)
                        .content("{\"kingdomName\":\"renameKingdom\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("error")
                        .value("Entered Kingdom id not exists!"));
    }

    @Test
    void rename_kingdom_user_not_owner_test() throws Exception {
        String mockToken = "mockToken";

        Mockito.when(kingdomService.kingdomExistsById(1L)).thenReturn(true);
        Mockito.when(kingdomService.kingdomRepoSize()).thenReturn(3);
        Mockito.when(appUserService.extractAppUserIdFromToken("Bearer mockToken")).thenReturn(2L);
        Mockito.when(kingdomService.appUserOwnsKingdom(1L, 1L)).thenReturn(false);

        mvc.perform(MockMvcRequestBuilders
                        .put("/kingdoms/1")
                        .header("Authorization", "Bearer " + mockToken)
                        .content("{\"kingdomName\":\"renameKingdom\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("error")
                        .value("You do not own that kingdom!"));
    }

    @Test
    void rename_kingdom_test() throws Exception {
        String mockToken = "mockToken";

        Mockito.when(kingdomService.kingdomExistsById(1L)).thenReturn(true);
        Mockito.when(kingdomService.kingdomRepoSize()).thenReturn(3);
        Mockito.when(appUserService.extractAppUserIdFromToken("Bearer mockToken")).thenReturn(1L);
        Mockito.when(kingdomService.appUserOwnsKingdom(1L, 1L)).thenReturn(true);

        mvc.perform(MockMvcRequestBuilders
                        .put("/kingdoms/1")
                        .header("Authorization", "Bearer " + mockToken)
                        .content("{\"kingdomName\":\"renameKingdom\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("message")
                        .value("Kingdom renamed"));
    }

    @Test
    void identify_kingdom_nullValue_coordinate_test() throws Exception {
        String mockToken = "mockToken";

        mvc.perform(MockMvcRequestBuilders
                        .get("/map")
                        .header("Authorization", "Bearer " + mockToken)
                        .content("{\"coordinateX\": 1}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error")
                        .value("Missing coordinates info!"));
    }

    @Test
    void identify_kingdom_negativeValue_coordinate_test() throws Exception {
        String mockToken = "mockToken";

        mvc.perform(MockMvcRequestBuilders
                        .get("/map")
                        .header("Authorization", "Bearer " + mockToken)
                        .content("{\"coordinateX\": 20,\n" +
                                "    \"coordinateY\": -1}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error")
                        .value("One or both provided coordinates are out of range!"));
    }

    @Test
    void identify_kingdom_outOfRangeValue_coordinate_test() throws Exception {
        String mockToken = "mockToken";

        mvc.perform(MockMvcRequestBuilders
                        .get("/map")
                        .header("Authorization", "Bearer " + mockToken)
                        .content("{\"coordinateX\": 20,\n" +
                                "    \"coordinateY\": 21}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error")
                        .value("One or both provided coordinates are out of range!"));
    }

    @Test
    void identify_kingdom_emptyCoordinates_coordinate_test() throws Exception {
        String mockToken = "mockToken";
        KingdomInfoDTO kingdomInfoDTO = new KingdomInfoDTO();

        Mockito.when(mapService.getKingdomByCoordinates(1L, 1L)).thenReturn(kingdomInfoDTO);

        mvc.perform(MockMvcRequestBuilders
                        .get("/map")
                        .header("Authorization", "Bearer " + mockToken)
                        .content("{\"coordinateX\": 1,\n" +
                                "    \"coordinateY\": 1}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"username\": null,\n" +
                                                    "    \"kingdomName\": null}"));
    }

    @Test
    void identify_kingdom_kingdomOnCoordinates_coordinate_test() throws Exception {
        String mockToken = "mockToken";
        KingdomInfoDTO kingdomInfoDTO = new KingdomInfoDTO();
        kingdomInfoDTO.setUsername("dummy");
        kingdomInfoDTO.setKingdomName("kingdom");

        Mockito.when(mapService.getKingdomByCoordinates(3L, 3L)).thenReturn(kingdomInfoDTO);

        mvc.perform(MockMvcRequestBuilders
                        .get("/map")
                        .header("Authorization", "Bearer " + mockToken)
                        .content("{\"coordinateX\": 3,\n" +
                                "    \"coordinateY\": 3}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"username\": \"dummy\",\n" +
                                                    "    \"kingdomName\": \"kingdom\"}"));
    }

    @Test
    void get_kingdom_troops_kingdom_does_not_exist_test() throws Exception {
        String mockToken = "mockToken";

        Mockito.when(kingdomService.findKingdomById(1L)).thenReturn(null);

        mvc.perform(MockMvcRequestBuilders
                        .get("/kingdoms/1/troops")
                        .header("Authorization", "Bearer " + mockToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error")
                        .value("This kingdom does not exist."));
    }

    @Test
    void get_kingdom_troops_user_not_owner_test() throws Exception {
        String mockToken = "mockToken";

        Mockito.when(kingdomService.findKingdomById(1L)).thenReturn(kingdom);
        Mockito.when(appUserService.extractAppUserIdFromToken("Bearer mockToken")).thenReturn(2L);
        Mockito.when(kingdomService.appUserOwnsKingdom(1L, 1L)).thenReturn(false);

        mvc.perform(MockMvcRequestBuilders
                .get("/kingdoms/1/troops")
                .header("Authorization", "Bearer " + mockToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("error")
                        .value("This kingdom does not belong to authenticated player!"));
    }

//    @Test
//    void get_kingdom_troops_success_test() throws Exception {
//        String mockToken = "mockToken";
//
//        TroopDTO troopDTO = new TroopDTO();
//        troopDTO.setTroopId(1L);
//        troopDTO.setType(TroopType.KNIGHT);
//        troopDTO.setEndOfTrainingTime(1321344L);
//
//        TroopDTO troopDTO1 = new TroopDTO();
//        troopDTO1.setTroopId(2L);
//        troopDTO1.setType(TroopType.KNIGHT);
//        troopDTO1.setEndOfTrainingTime(234241344L);
//        KingdomTroopsDTO kingdomTroopsDTO = new KingdomTroopsDTO();
//        List<TroopDTO> kingdomTroops = new ArrayList<>();
//        kingdomTroopsDTO.setKingdomTroops(kingdomTroops);
//
//        Mockito.when(kingdomService.findKingdomById(1L)).thenReturn(kingdom);
//        Mockito.when(appUserService.extractAppUserIdFromToken("Bearer mockToken")).thenReturn(1L);
//        Mockito.when(kingdomService.appUserOwnsKingdom(1L, 1L)).thenReturn(true);
//        Mockito.when(kingdomService.getKingdomTroopsDTOById(1L)).thenReturn(kingdomTroopsDTO);
//
//        mvc.perform(MockMvcRequestBuilders
//                        .get("/kingdoms/2/troops")
//                        .header("Authorization", "Bearer " + mockToken))
//                .andExpect(status().isOk())
//                .andExpect(content().json(
//                        "{ \"kingdomTroops\" : [ {\"troopId\": 1, \"type\": \"KNIGHT\", \"endOfTrainingTime\": 1321344}, " +
//                                "{\"troopId\": 2, \"type\": \"KNIGHT\", \"endOfTrainingTime\": 234241344} ] }"));
//
//    }
    
//    @Test
//    void get_kingdom_troops_success_test() throws Exception {
//
//        mvc.perform(MockMvcRequestBuilders
//                        .get("/kingdoms/1/troops")
//                        .header("Authorization", "Bearer " + mockToken))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.kingdomTroops").value(kingdomTroops))
//                .andExpect(jsonPath("$.troopId").value(troopDTO.getTroopId()))
//                .andExpect(jsonPath("$.type").value(troopDTO.getType()))
//                .andExpect(jsonPath("$.endOfTrainingTime").value(troopDTO.getEndOfTrainingTime()))
//                .andExpect(jsonPath("$.troopId").value(troopDTO1.getTroopId()))
//                .andExpect(jsonPath("$.type").value(troopDTO1.getType()))
//                .andExpect(jsonPath("$.endOfTrainingTime").value(troopDTO1.getEndOfTrainingTime()));
}