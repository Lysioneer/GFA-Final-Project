package com.gfa.straysfullstacktribes.controllers;

import com.gfa.straysfullstacktribes.models.Building;
import com.gfa.straysfullstacktribes.models.Kingdom;
import com.gfa.straysfullstacktribes.models.enums.TroopType;
import com.gfa.straysfullstacktribes.services.*;
import com.gfa.straysfullstacktribes.utilities.JwtUtilities;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
class TroopControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private EmailServiceImpl emailService;

    @MockBean
    private BuildingService buildingService;

    @MockBean
    private KingdomService kingdomService;

    @MockBean
    private AppUserServiceImpl appUserService;

    @MockBean
    private TroopService troopService;

    @MockBean
    private UnitLevelService unitLevelService;

    @MockBean
    private JwtUtilities jwtUtilities;  //it isn't directly used but needs to be there, test would not be able to read

    @MockBean
    private UpdateResourcesService updateResourcesService;

    @Test
    void bad_post_Url_test() throws Exception {
        String mockToken = "mockToken";

        mvc.perform(MockMvcRequestBuilders
                        .post("/kingdoms/ /troops")
                        .header("Authorization", "Bearer " + mockToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error")
                        .value("Provide Kingdom's Id in url path, example: /kingdoms/1/troops"));
    }

    @Test
    void crete_troop_no_json_test() throws Exception {
        String mockToken = "mockToken";

        mvc.perform(MockMvcRequestBuilders
                        .post("/kingdoms/1/troops")
                        .header("Authorization", "Bearer " + mockToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error")
                        .value("Provide troop build request in JSON!"));
    }

    @Test
    void create_troop_no_type_request_test() throws Exception {
        String mockToken = "mockToken";

        mvc.perform(MockMvcRequestBuilders
                        .post("/kingdoms/1/troops")
                        .header("Authorization", "Bearer " + mockToken)
                        .content("{\"quantity\": 1 }")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error")
                        .value("Type is required."));
    }

    @Test
    void create_troop_no_quantity_request_test() throws Exception {
        String mockToken = "mockToken";

        mvc.perform(MockMvcRequestBuilders
                        .post("/kingdoms/1/troops")
                        .header("Authorization", "Bearer " + mockToken)
                        .content("{\"type\":\"phalanx\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error")
                        .value("Quantity is required."));
    }

    @Test
    void create_troop_kingdom_id_too_low_test() throws Exception {
        String mockToken = "mockToken";

        Mockito.when(kingdomService.kingdomRepoSize()).thenReturn(3);

        mvc.perform(MockMvcRequestBuilders
                        .post("/kingdoms/0/troops")
                        .header("Authorization", "Bearer " + mockToken)
                        .content("{\"type\":\"phalanx\"," +
                                "\"quantity\": 1 }")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("error")
                        .value("Entered Kingdom id not exists!"));
    }

    @Test
    void create_troop_kingdom_id_high_low_test() throws Exception {
        String mockToken = "mockToken";

        Mockito.when(kingdomService.kingdomRepoSize()).thenReturn(3);

        mvc.perform(MockMvcRequestBuilders
                        .post("/kingdoms/4/troops")
                        .header("Authorization", "Bearer " + mockToken)
                        .content("{\"type\":\"phalanx\"," +
                                "\"quantity\": 1 }")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("error")
                        .value("Entered Kingdom id not exists!"));
    }

    @Test
    void create_troop_user_not_owner_test() throws Exception {
        String mockToken = "mockToken";

        Mockito.when(kingdomService.kingdomExistsById(1L)).thenReturn(true);
        Mockito.when(kingdomService.kingdomRepoSize()).thenReturn(3);
        Mockito.when(appUserService.extractAppUserIdFromToken("Bearer mockToken")).thenReturn(2L);
        Mockito.when(kingdomService.appUserOwnsKingdom(1L, 1L)).thenReturn(false);

        mvc.perform(MockMvcRequestBuilders
                        .post("/kingdoms/1/troops")
                        .header("Authorization", "Bearer " + mockToken)
                        .content("{\"type\":\"phalanx\"," +
                                "\"quantity\": 1 }")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("error")
                        .value("You do not own that kingdom!"));
    }

    @Test
    void create_troop_troop_type_not_exists_test() throws Exception {
        String mockToken = "mockToken";

        Mockito.when(kingdomService.kingdomExistsById(1L)).thenReturn(true);
        Mockito.when(kingdomService.kingdomRepoSize()).thenReturn(3);
        Mockito.when(appUserService.extractAppUserIdFromToken("Bearer mockToken")).thenReturn(1L);
        Mockito.when(kingdomService.appUserOwnsKingdom(1L, 1L)).thenReturn(true);

        mvc.perform(MockMvcRequestBuilders
                        .post("/kingdoms/1/troops")
                        .header("Authorization", "Bearer " + mockToken)
                        .content("{\"type\":\"spy\"," +
                                "\"quantity\": 1 }")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("error").value("Entered type does not exist!"));
    }

    @Test
    void create_troop_barracks_type_not_exists_test() throws Exception {
        String mockToken = "mockToken";

        Mockito.when(kingdomService.kingdomExistsById(1L)).thenReturn(true);
        Mockito.when(kingdomService.kingdomRepoSize()).thenReturn(3);
        Mockito.when(appUserService.extractAppUserIdFromToken("Bearer mockToken")).thenReturn(1L);
        Mockito.when(kingdomService.appUserOwnsKingdom(1L, 1L)).thenReturn(true);
        Mockito.when(buildingService.existsKingdomBarracks(1L)).thenReturn(false);

        mvc.perform(MockMvcRequestBuilders
                        .post("/kingdoms/1/troops")
                        .header("Authorization", "Bearer " + mockToken)
                        .content("{\"type\":\"phalanx\"," +
                                "\"quantity\": 1 }")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("error").value("You have to build barracks first!"));
    }

    @Test
    void create_troop_insufficient_gold_test() throws Exception {
        String mockToken = "mockToken";
        Kingdom kingdom = new Kingdom();
        kingdom.setGoldAmount(0L);

        Mockito.when(kingdomService.kingdomExistsById(1L)).thenReturn(true);
        Mockito.when(kingdomService.kingdomRepoSize()).thenReturn(3);
        Mockito.when(appUserService.extractAppUserIdFromToken("Bearer mockToken")).thenReturn(1L);
        Mockito.when(kingdomService.appUserOwnsKingdom(1L, 1L)).thenReturn(true);
        Mockito.when(buildingService.existsKingdomBarracks(1L)).thenReturn(true);
        Mockito.when(kingdomService.findKingdomById(1L)).thenReturn(kingdom);

        mvc.perform(MockMvcRequestBuilders
                        .post("/kingdoms/1/troops")
                        .header("Authorization", "Bearer " + mockToken)
                        .content("{\"type\":\"phalanx\"," +
                                "\"quantity\": 1 }")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("error").value("Not enough gold!"));
    }

    @Test
    void create_troop_test() throws Exception {
        String mockToken = "mockToken";
        Kingdom kingdom = new Kingdom();
        kingdom.setGoldAmount(10000L);

        Mockito.when(kingdomService.kingdomExistsById(1L)).thenReturn(true);
        Mockito.when(kingdomService.kingdomRepoSize()).thenReturn(3);
        Mockito.when(appUserService.extractAppUserIdFromToken("Bearer mockToken")).thenReturn(1L);
        Mockito.when(kingdomService.appUserOwnsKingdom(1L, 1L)).thenReturn(true);
        Mockito.when(buildingService.existsKingdomBarracks(1L)).thenReturn(true);
        Mockito.when(kingdomService.findKingdomById(1L)).thenReturn(kingdom);

        mvc.perform(MockMvcRequestBuilders
                        .post("/kingdoms/1/troops")
                        .header("Authorization", "Bearer " + mockToken)
                        .content("{\"type\":\"phalanx\"," +
                                "\"quantity\": 1 }")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void bad_put_Url_test() throws Exception {
        String mockToken = "mockToken";

        mvc.perform(MockMvcRequestBuilders
                        .put("/kingdoms/ /troops")
                        .header("Authorization", "Bearer " + mockToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error")
                        .value("Provide Kingdom's Id in url path, example: /kingdoms/1/troops"));
    }

    @Test
    void upgrade_troop_no_json_test() throws Exception {
        String mockToken = "mockToken";

        mvc.perform(MockMvcRequestBuilders
                        .put("/kingdoms/1/troops")
                        .header("Authorization", "Bearer " + mockToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error")
                        .value("Provide troop build request in JSON!"));
    }

    @Test
    void upgrade_troop_no_type_request_test() throws Exception {
        String mockToken = "mockToken";

        mvc.perform(MockMvcRequestBuilders
                        .put("/kingdoms/1/troops")
                        .header("Authorization", "Bearer " + mockToken)
                        .content("{}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("error")
                        .value("Type is required."));
    }

    @Test
    void upgrade_troop_kingdom_id_too_low_test() throws Exception {
        String mockToken = "mockToken";

        Mockito.when(kingdomService.kingdomRepoSize()).thenReturn(3);

        mvc.perform(MockMvcRequestBuilders
                        .put("/kingdoms/0/troops")
                        .header("Authorization", "Bearer " + mockToken)
                        .content("{\"type\":\"phalanx\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("error")
                        .value("Entered Kingdom id not exists!"));
    }

    @Test
    void upgrade_troop_kingdom_id_high_low_test() throws Exception {
        String mockToken = "mockToken";

        Mockito.when(kingdomService.kingdomRepoSize()).thenReturn(3);

        mvc.perform(MockMvcRequestBuilders
                        .put("/kingdoms/4/troops")
                        .header("Authorization", "Bearer " + mockToken)
                        .content("{\"type\":\"phalanx\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("error")
                        .value("Entered Kingdom id not exists!"));
    }

    @Test
    void upgrade_troop_user_not_owner_test() throws Exception {
        String mockToken = "mockToken";

        Mockito.when(kingdomService.kingdomExistsById(1L)).thenReturn(true);
        Mockito.when(kingdomService.kingdomRepoSize()).thenReturn(3);
        Mockito.when(appUserService.extractAppUserIdFromToken("Bearer mockToken")).thenReturn(2L);
        Mockito.when(kingdomService.appUserOwnsKingdom(1L, 1L)).thenReturn(false);

        mvc.perform(MockMvcRequestBuilders
                        .put("/kingdoms/1/troops")
                        .header("Authorization", "Bearer " + mockToken)
                        .content("{\"type\":\"phalanx\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("error")
                        .value("You do not own that kingdom!"));
    }

    @Test
    void upgrade_troop_troop_type_not_exists_test() throws Exception {
        String mockToken = "mockToken";

        Mockito.when(kingdomService.kingdomExistsById(1L)).thenReturn(true);
        Mockito.when(kingdomService.kingdomRepoSize()).thenReturn(3);
        Mockito.when(appUserService.extractAppUserIdFromToken("Bearer mockToken")).thenReturn(1L);
        Mockito.when(kingdomService.appUserOwnsKingdom(1L, 1L)).thenReturn(true);

        mvc.perform(MockMvcRequestBuilders
                        .put("/kingdoms/1/troops")
                        .header("Authorization", "Bearer " + mockToken)
                        .content("{\"type\":\"spy\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("error").value("Entered type does not exist!"));
    }

    @Test
    void upgrade_troop_academy_not_exists_test() throws Exception {
        String mockToken = "mockToken";

        Mockito.when(kingdomService.kingdomExistsById(1L)).thenReturn(true);
        Mockito.when(kingdomService.kingdomRepoSize()).thenReturn(3);
        Mockito.when(appUserService.extractAppUserIdFromToken("Bearer mockToken")).thenReturn(1L);
        Mockito.when(kingdomService.appUserOwnsKingdom(1L, 1L)).thenReturn(true);
        Mockito.when(buildingService.existsKingdomAcademy(1L)).thenReturn(false);

        mvc.perform(MockMvcRequestBuilders
                        .put("/kingdoms/1/troops")
                        .header("Authorization", "Bearer " + mockToken)
                        .content("{\"type\":\"phalanx\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("error").value("You have to build academy first!"));
    }

    @Test
    void upgrade_troop_insufficient_gold_test() throws Exception {
        String mockToken = "mockToken";
        Kingdom kingdom = new Kingdom();
        kingdom.setGoldAmount(0L);

        Mockito.when(kingdomService.kingdomExistsById(1L)).thenReturn(true);
        Mockito.when(kingdomService.kingdomRepoSize()).thenReturn(3);
        Mockito.when(appUserService.extractAppUserIdFromToken("Bearer mockToken")).thenReturn(1L);
        Mockito.when(kingdomService.appUserOwnsKingdom(1L, 1L)).thenReturn(true);
        Mockito.when(buildingService.existsKingdomAcademy(1L)).thenReturn(true);
        Mockito.when(kingdomService.findKingdomById(1L)).thenReturn(kingdom);
        Mockito.when(unitLevelService.getKingdomUnitLevel(1L, TroopType.PHALANX)).thenReturn(1);

        mvc.perform(MockMvcRequestBuilders
                        .put("/kingdoms/1/troops")
                        .header("Authorization", "Bearer " + mockToken)
                        .content("{\"type\":\"phalanx\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("error").value("Not enough gold!"));
    }

    @Test
    void upgrade_troop_max_level_test() throws Exception {
        String mockToken = "mockToken";
        Kingdom kingdom = new Kingdom();
        kingdom.setGoldAmount(1000000L);

        Mockito.when(kingdomService.kingdomExistsById(1L)).thenReturn(true);
        Mockito.when(kingdomService.kingdomRepoSize()).thenReturn(3);
        Mockito.when(appUserService.extractAppUserIdFromToken("Bearer mockToken")).thenReturn(1L);
        Mockito.when(kingdomService.appUserOwnsKingdom(1L, 1L)).thenReturn(true);
        Mockito.when(buildingService.existsKingdomAcademy(1L)).thenReturn(true);
        Mockito.when(kingdomService.findKingdomById(1L)).thenReturn(kingdom);
        Mockito.when(unitLevelService.getKingdomUnitLevel(1L, TroopType.PHALANX)).thenReturn(20);

        mvc.perform(MockMvcRequestBuilders
                        .put("/kingdoms/1/troops")
                        .header("Authorization", "Bearer " + mockToken)
                        .content("{\"type\":\"phalanx\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("error").value("Type of troops already at maximum level!"));
    }

    @Test
    void upgrade_troop_academy_low_level_test() throws Exception {
        String mockToken = "mockToken";
        Kingdom kingdom = new Kingdom();
        kingdom.setGoldAmount(1000000L);

        Building academy = new Building();
        academy.setLevel(1);

        Mockito.when(kingdomService.kingdomExistsById(1L)).thenReturn(true);
        Mockito.when(kingdomService.kingdomRepoSize()).thenReturn(3);
        Mockito.when(appUserService.extractAppUserIdFromToken("Bearer mockToken")).thenReturn(1L);
        Mockito.when(kingdomService.appUserOwnsKingdom(1L, 1L)).thenReturn(true);
        Mockito.when(buildingService.existsKingdomAcademy(1L)).thenReturn(true);
        Mockito.when(kingdomService.findKingdomById(1L)).thenReturn(kingdom);
        Mockito.when(buildingService.findKingdomAcademy(1L)).thenReturn(academy);
        Mockito.when(unitLevelService.getKingdomUnitLevel(1L, TroopType.PHALANX)).thenReturn(1);

        mvc.perform(MockMvcRequestBuilders
                        .put("/kingdoms/1/troops")
                        .header("Authorization", "Bearer " + mockToken)
                        .content("{\"type\":\"phalanx\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("error")
                        .value("Unable to upgrade troop type, upgrade academy first!"));
    }

    @Test
    void upgrade_troop_already_upgrading_test() throws Exception {
        String mockToken = "mockToken";
        Kingdom kingdom = new Kingdom();
        kingdom.setGoldAmount(1000000L);

        Building academy = new Building();
        academy.setLevel(1);

        Mockito.when(kingdomService.kingdomExistsById(1L)).thenReturn(true);
        Mockito.when(kingdomService.kingdomRepoSize()).thenReturn(3);
        Mockito.when(appUserService.extractAppUserIdFromToken("Bearer mockToken")).thenReturn(1L);
        Mockito.when(kingdomService.appUserOwnsKingdom(1L, 1L)).thenReturn(true);
        Mockito.when(buildingService.existsKingdomAcademy(1L)).thenReturn(true);
        Mockito.when(kingdomService.findKingdomById(1L)).thenReturn(kingdom);
        Mockito.when(buildingService.findKingdomAcademy(1L)).thenReturn(academy);
        Mockito.when(unitLevelService.getKingdomUnitLevel(1L, TroopType.PHALANX)).thenReturn(0);
        Mockito.when(unitLevelService.isUnitLevelUpgrading(1L, TroopType.PHALANX)).thenReturn(true);

        mvc.perform(MockMvcRequestBuilders
                        .put("/kingdoms/1/troops")
                        .header("Authorization", "Bearer " + mockToken)
                        .content("{\"type\":\"phalanx\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("error")
                        .value("You are already upgrading this type of troops!"));
    }

    @Test
    void upgrade_troop_ok_test() throws Exception {
        String mockToken = "mockToken";
        Kingdom kingdom = new Kingdom();
        kingdom.setGoldAmount(1000000L);

        Building academy = new Building();
        academy.setLevel(1);

        Mockito.when(kingdomService.kingdomExistsById(1L)).thenReturn(true);
        Mockito.when(kingdomService.kingdomRepoSize()).thenReturn(3);
        Mockito.when(appUserService.extractAppUserIdFromToken("Bearer mockToken")).thenReturn(1L);
        Mockito.when(kingdomService.appUserOwnsKingdom(1L, 1L)).thenReturn(true);
        Mockito.when(buildingService.existsKingdomAcademy(1L)).thenReturn(true);
        Mockito.when(kingdomService.findKingdomById(1L)).thenReturn(kingdom);
        Mockito.when(buildingService.findKingdomAcademy(1L)).thenReturn(academy);
        Mockito.when(unitLevelService.getKingdomUnitLevel(1L, TroopType.PHALANX)).thenReturn(0);
        Mockito.when(unitLevelService.isUnitLevelUpgrading(1L, TroopType.PHALANX)).thenReturn(false);

        mvc.perform(MockMvcRequestBuilders
                        .put("/kingdoms/1/troops")
                        .header("Authorization", "Bearer " + mockToken)
                        .content("{\"type\":\"phalanx\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("status")
                        .value("OK"));
    }
}