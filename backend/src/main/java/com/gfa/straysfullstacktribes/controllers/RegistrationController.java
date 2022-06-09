package com.gfa.straysfullstacktribes.controllers;

import com.gfa.straysfullstacktribes.exceptions.WrongConfirmationTokenException;
import com.gfa.straysfullstacktribes.models.Building;
import com.gfa.straysfullstacktribes.models.dtos.RegistrationRequestDTO;
import com.gfa.straysfullstacktribes.models.enums.BuildingType;
import com.gfa.straysfullstacktribes.services.*;
import com.gfa.straysfullstacktribes.models.AppUser;
import com.gfa.straysfullstacktribes.models.Kingdom;
import com.gfa.straysfullstacktribes.models.dtos.KingdomRegistrationRequestModelDTO;
import com.gfa.straysfullstacktribes.utilities.DefaultValueReaderUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path = "/registration")
@AllArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;
    private final MapService mapService;
    private final AppUserService appUserService;
    private final KingdomService kingdomService;
    private final BuildingService buildingService;    
    private final UnitLevelService unitLevelService;

    @PostMapping
    public ResponseEntity registerUser(@RequestBody RegistrationRequestDTO request) {

        if ((request.getUsername() == null || request.getUsername().isEmpty())
                && (request.getEmail() == null || request.getEmail().isEmpty())
                && (request.getPassword() == null || request.getPassword().isEmpty())) {
            Map<String, String> body = new HashMap<>();
            body.put("error", "Please provide username, email, and password.");
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);

        } else if (request.getUsername() == null || request.getUsername().isEmpty()) {
            Map<String, String> body = new HashMap<>();
            body.put("error", "All required fields must be filled out.");
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);

        } else if (request.getEmail() == null || request.getEmail().isEmpty()) {
            Map<String, String> body = new HashMap<>();
            body.put("error", "All required fields must be filled out.");
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);

        } else if (appUserService.getUserByName(request.getUsername()) != null) {
            Map<String, String> body = new HashMap<>();
            body.put("error", "Provided credentials are not valid.");
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);

        } else if (appUserService.getUserByEmail(request.getEmail()) != null) {
            Map<String, String> body = new HashMap<>();
            body.put("error", "Provided credentials are not valid.");
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);

        } else if (request.getPassword() == null || request.getPassword().isEmpty()) {
            Map<String, String> body = new HashMap<>();
            body.put("error", "All required fields must be filled out.");
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);

        } else if (request.getPassword().length() < 8) {
            Map<String, String> body = new HashMap<>();
            body.put("error", "Password must be at least 8 characters long!");
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }

        registrationService.register(request);

        Map<String, String> body = new HashMap<>();
        body.put("status", "ok");

        return new ResponseEntity(body, HttpStatus.OK);
    }

    @GetMapping(path = "/confirmation")
    public String confirmUser(@RequestParam("token") String token) {
        try {
            return registrationService.confirmToken(token);
        } catch (WrongConfirmationTokenException e) {
            e.printStackTrace();
            return e.toString();
        }
    }

    // SFT-6 Register player's kingdom
    @PostMapping(value = "/kingdom")
    public ResponseEntity registerKingdom(@RequestBody KingdomRegistrationRequestModelDTO kingdomRegistrationRequestModelDTO) throws Exception {

        if (kingdomRegistrationRequestModelDTO.getKingdomName() == null || kingdomRegistrationRequestModelDTO.getKingdomName().equals("")) {
            kingdomRegistrationRequestModelDTO.setKingdomName(kingdomRegistrationRequestModelDTO.getUsername() + "´s kingdom");
        }

        int boardSizeX = DefaultValueReaderUtil.getInt("map.size.axisX");
        int boardSizeY = DefaultValueReaderUtil.getInt("map.size.axisY");

        if (kingdomRegistrationRequestModelDTO.getUsername() == null || kingdomRegistrationRequestModelDTO.getUsername().equals("")) {
            Map<String, String> body = new HashMap<>();
            body.put("error", "Missing username!");
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);

        } else if (kingdomRegistrationRequestModelDTO.getPassword() == null) {
            Map<String, String> body = new HashMap<>();
            body.put("error", "Missing password!");
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);

        } else if (kingdomRegistrationRequestModelDTO.getPassword().length() < 8) {
            Map<String, String> body = new HashMap<>();
            body.put("error", "Password must be at least 8 characters long!");
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);

        } else if (kingdomRegistrationRequestModelDTO.getCoordinateX() < 1 || kingdomRegistrationRequestModelDTO.getCoordinateX() > boardSizeX) {
            Map<String, String> body = new HashMap<>();
            body.put("error", "Coordinates are out of range!");
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);

        } else if (kingdomRegistrationRequestModelDTO.getCoordinateY() < 1 || kingdomRegistrationRequestModelDTO.getCoordinateY() > boardSizeY) {
            Map<String, String> body = new HashMap<>();
            body.put("error", "Coordinates are out of range!");
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);

        } else if (mapService.getCoordinatesInfo2(
                kingdomRegistrationRequestModelDTO.getCoordinateX(),
                kingdomRegistrationRequestModelDTO.getCoordinateY()).isPresent()) {
            Map<String, String> body = new HashMap<>();
            body.put("error", "Coordinates are already taken by another kingdom!");
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);

        } else if (appUserService.getUserByUsernameAndPassword(kingdomRegistrationRequestModelDTO.getUsername(), kingdomRegistrationRequestModelDTO.getPassword()) == null) {
            Map<String, String> body = new HashMap<>();
            body.put("error", "Wrong username or password!");
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }

        AppUser user = appUserService.getUserByUsernameAndPassword(kingdomRegistrationRequestModelDTO.getUsername(), kingdomRegistrationRequestModelDTO.getPassword());

        if (!user.getKingdom().isEmpty()) {
            Map<String, String> body = new HashMap<>();
            body.put("error", "Default kingdom already created!");
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }

        Kingdom kingdom = new Kingdom(
                kingdomRegistrationRequestModelDTO.getKingdomName(),
                kingdomRegistrationRequestModelDTO.getCoordinateX(),
                kingdomRegistrationRequestModelDTO.getCoordinateY(),
                appUserService.getUserByUsernameAndPassword(kingdomRegistrationRequestModelDTO.getUsername(), kingdomRegistrationRequestModelDTO.getPassword()),
                (long) DefaultValueReaderUtil.getInt("kingdom.goldAmount"),
                (long) DefaultValueReaderUtil.getInt("kingdom.foodAmount"),
                DefaultValueReaderUtil.getInt("kingdom.goldProduction"),
                DefaultValueReaderUtil.getInt("kingdom.foodProduction"));

        Building townHall = new Building(kingdom, BuildingType.TOWN_HALL, 1, 1);
        Building farm = new Building(kingdom, BuildingType.FARM, 2, 1);
        Building goldmine = new Building(kingdom, BuildingType.GOLD_MINE, 3, 1);

        kingdomService.saveKingdom(kingdom);
        buildingService.constructNew(townHall);
        buildingService.constructNew(farm);
        buildingService.constructNew(goldmine);

        //      This will create level 0 unitLevels used for creating troops
        unitLevelService.createUnitLevelForNewKingdom(kingdom);

        user.addKingdom(kingdom);

        Map<String, String> body = new HashMap<>();
        body.put("status", "ok");
        body.put("message", "Congratulations " + kingdomRegistrationRequestModelDTO.getUsername() + ". Your kingdom " +
                kingdomRegistrationRequestModelDTO.getKingdomName() + " has been found at world map coordinates [" +
                kingdomRegistrationRequestModelDTO.getCoordinateX() + "," + kingdomRegistrationRequestModelDTO.getCoordinateY() +
                "]!");

        return new ResponseEntity(body, HttpStatus.OK);

    }
}