package com.gfa.straysfullstacktribes.controllers;

import com.gfa.straysfullstacktribes.exceptions.defaultValuesExceptions.DefaultValueNotFoundException;
import com.gfa.straysfullstacktribes.exceptions.defaultValuesExceptions.DefaultValuesFileMissingException;
import com.gfa.straysfullstacktribes.exceptions.defaultValuesExceptions.IncorrectDefaultValueTypeException;
import com.gfa.straysfullstacktribes.models.*;
import com.gfa.straysfullstacktribes.models.dtos.*;
import com.gfa.straysfullstacktribes.models.enums.BuildingType;
import com.gfa.straysfullstacktribes.repositories.BattleRepository;
import com.gfa.straysfullstacktribes.services.*;
import com.gfa.straysfullstacktribes.utilities.DefaultValueReaderUtil;
import com.nimbusds.jose.shaded.json.JSONObject;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@AllArgsConstructor
public class KingdomController {

    private final MapService mapService;
    private final UpdateResourcesService updateResourcesService;
    private final BuildingService buildingService;
    private final BattleService battleService;
    private final BattleRepository battleRepository;
    private KingdomService kingdomService;
    private AppUserService appUserService;

    @PutMapping("/kingdoms/{id}")
    public ResponseEntity<?> renameKingdom(@PathVariable(name = "id", required = false) Long id,
                                           @RequestBody(required = false) KingdomRenameDTO kingdomRenameDTO,
                                           @RequestHeader(value = "Authorization") String token) {

        if (id == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorMessage("Provide Kingdom's Id in url path, example: /kingdoms/1"));
        } else if (kingdomRenameDTO == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorMessage("Provide Kingdom's name in JSON!"));
        } else if (!kingdomService.kingdomExistsById(id) || kingdomService.kingdomRepoSize() < id) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorMessage("Entered Kingdom id not exists!"));
        } else if (!kingdomService.appUserOwnsKingdom(id, appUserService.extractAppUserIdFromToken(token))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorMessage("You do not own that kingdom!"));
        }
        kingdomService.renameKingdom(id, kingdomRenameDTO.getKingdomName());
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessMessage("Kingdom renamed"));
    }

    @GetMapping(value = "/map")
    public ResponseEntity identifyKingdom(@RequestBody(required = true) IdentifyKingdomByCoordinatesDTO coordinates)
            throws DefaultValuesFileMissingException, DefaultValueNotFoundException, IncorrectDefaultValueTypeException {
        int boardSizeX = DefaultValueReaderUtil.getInt("map.size.axisX");
        int boardSizeY = DefaultValueReaderUtil.getInt("map.size.axisY");

        if (coordinates.getCoordinateX() == null || coordinates.getCoordinateY() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorMessage("Missing coordinates info!"));
        } else if (coordinates.getCoordinateX() < 1L || coordinates.getCoordinateX() > (long) boardSizeX
                || coordinates.getCoordinateY() < 1L || coordinates.getCoordinateY() > (long) boardSizeY) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorMessage("One or both provided coordinates are out of range!"));
        } else {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(mapService.getKingdomByCoordinates(coordinates.getCoordinateX(), coordinates.getCoordinateY()));
        }
    }

    @PostMapping("/kingdoms/{id}/buildings")
    public ResponseEntity addBuildingToKingdom(@PathVariable(required = true) Long id,
                                               @RequestBody AddBuildingRequestDTO addBuildingRequestDTO,
                                               @RequestHeader(value = "Authorization") String token) {

        if (token.isEmpty() || !kingdomService.appUserOwnsKingdom(id, appUserService.extractAppUserIdFromToken(token))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorMessage("This kingdom does not belong to authenticated player!"));
        } else {
            updateResourcesService.updateResources(id);
            Optional<ErrorMessage> constructionCheck = buildingService.checkPlannedConstruction(addBuildingRequestDTO, id);
            if (constructionCheck.isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(constructionCheck.get());
            } else {
                Optional<Kingdom> optionalKingdom = kingdomService.getKingdomById(id);
                if (optionalKingdom.isPresent()) {
                    Building newBuilding =
                            new Building(optionalKingdom.get(), BuildingType.fromName(addBuildingRequestDTO.getBuildingType()), addBuildingRequestDTO.getPosition());
                    buildingService.constructNew(newBuilding);
                    try {
                        int buildingCost = DefaultValueReaderUtil.getInt("buildings." + newBuilding.getType().getName() + ".price");
                        optionalKingdom.get().setGoldAmount(optionalKingdom.get().getGoldAmount() - buildingCost);
                        kingdomService.saveKingdom(optionalKingdom.get());
                    } catch (DefaultValueNotFoundException | IncorrectDefaultValueTypeException | DefaultValuesFileMissingException e) {
                        e.printStackTrace();
                    }
                    return ResponseEntity.status(HttpStatus.OK)
                            .body(AddBuildingResponseDTO
                                    .builder()
                                    .position(newBuilding.getBuildingPosition())
                                    .id(newBuilding.getId())
                                    .kingdomId(id)
                                    .type(newBuilding.getType().getName())
                                    .level(newBuilding.getLevel())
                                    .constructTime(newBuilding.getConstructTime())
                                    .destroyTime(null)
                                    .build());
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(new ErrorMessage("No such kingdom!"));
                }
            }
        }
    }

    @PutMapping("/kingdoms/{kingdomId}/buildings/{buildingId}")
    public ResponseEntity upgradeBuilding(@PathVariable(name = "kingdomId") Long kingdomId,
                                          @PathVariable(name = "buildingId") Long buildingId,
                                          @RequestBody(required = false) UpgradeBuildingRequestDTO upgradeBuildingRequestDTO,
                                          @RequestHeader(value = "Authorization") String token) {
        if (kingdomService.getKingdomById(kingdomId).isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorMessage("This kingdom does not exist."));
        } else if (token.isEmpty() || !kingdomService.appUserOwnsKingdom(kingdomId, appUserService.extractAppUserIdFromToken(token))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorMessage("This kingdom does not belong to authenticated player!"));
        } else if (buildingService.getBuildingByBuildingIdAndKingdomId(buildingId, kingdomId).isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorMessage("There is no such building in your kingdom!"));
        } else if (upgradeBuildingRequestDTO == null ||
                (!upgradeBuildingRequestDTO.getAction().equals("upgrade") && !upgradeBuildingRequestDTO.getAction().equals("tear-down"))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorMessage("Your request is incorrect!"));
        }

        updateResourcesService.updateResources(kingdomId);
        if (upgradeBuildingRequestDTO.getAction().equals("upgrade")) {
            Optional<ErrorMessage> checkLevelingUp = buildingService.levelUpBuildingCheck(buildingId, kingdomId);
            if (checkLevelingUp.isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(checkLevelingUp.get());
            } else {
                return ResponseEntity.status(HttpStatus.OK).body(buildingService.levelUpBuilding(buildingId, kingdomId));
            }
        } else if (upgradeBuildingRequestDTO.getAction().equals("tear-down")) {
            Optional<ErrorMessage> checkTearingDown =
                    buildingService.tearDownBuildingCheck(buildingId, kingdomId, upgradeBuildingRequestDTO.getInstant());
            if (checkTearingDown.isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(checkTearingDown.get());
            } else {
                Optional<UpgradeBuildingResponseDTO> response =
                        buildingService.tearDownBuilding(buildingId, kingdomId, upgradeBuildingRequestDTO.getInstant());
                if (response.isPresent()) {
                    return ResponseEntity.status(HttpStatus.OK)
                            .body(response.get());
                }
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessage("Something went wrong!"));
    }

    @GetMapping(value = "/kingdoms/{id}")
    public ResponseEntity<?> getKingdomDetails(@PathVariable(name = "id") Long id,
                                               @RequestHeader(value = "Authorization") String token) {
        if (kingdomService.findKingdomById(id) == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorMessage("This kingdom does not exist."));
        } else if (token.isEmpty() || !kingdomService.appUserOwnsKingdom(id, appUserService.extractAppUserIdFromToken(token))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorMessage("This kingdom does not belong to authenticated player!"));
        } else {
            KingdomDetailsDTO kingdomDetails = kingdomService.getKingdomDetailsDTOById(id);
            return ResponseEntity.status(HttpStatus.OK).body(kingdomDetails);
        }
    }

    @GetMapping(value = "/kingdoms/{id}/buildings")
    public ResponseEntity<?> getKingdomBuildings(@PathVariable(name = "id") Long id,
                                                 @RequestHeader(value = "Authorization") String token) {
        if (kingdomService.findKingdomById(id) == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorMessage("This kingdom does not exist."));
        } else if (token.isEmpty() || !kingdomService.appUserOwnsKingdom(id, appUserService.extractAppUserIdFromToken(token))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorMessage("This kingdom does not belong to authenticated player!"));
        } else {
            KingdomBuildingsDTO buildingsDetails = kingdomService.getKingdomBuildingsDTOById(id);
            return ResponseEntity.status(HttpStatus.OK).body(buildingsDetails);
        }
    }

    @GetMapping(value = "/kingdoms/{id}/troops")
    public ResponseEntity<?> getKingdomTroops(@PathVariable(name = "id") Long id,
                                              @RequestHeader(value = "Authorization") String token) {
        if (kingdomService.findKingdomById(id) == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorMessage("This kingdom does not exist."));
        } else if (token.isEmpty() || !kingdomService.appUserOwnsKingdom(id, appUserService.extractAppUserIdFromToken(token))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorMessage("This kingdom does not belong to authenticated player!"));
        } else {
            KingdomTroopsDTO troopsDetails = kingdomService.getKingdomTroopsDTOById(id);
            return ResponseEntity.status(HttpStatus.OK).body(troopsDetails);
        }
    }

    @PostMapping(value = "/kingdoms/{id}/battles")
    public ResponseEntity<?> battle(@PathVariable("id") Long attackerKingdomId,
                                    @RequestBody(required = true) BattleRequestDTO battleRequestDTO,
                                    @RequestHeader(value = "Authorization") String token) {

        if (!kingdomService.appUserOwnsKingdom(attackerKingdomId, appUserService.extractAppUserIdFromToken(token))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorMessage("You do not own that kingdom!"));
        }
        updateResourcesService.updateResources(attackerKingdomId);
        Double distance = battleService.calculateDistance(kingdomService.findKingdomById(attackerKingdomId), kingdomService.findKingdomById(battleRequestDTO.getDefenderKingdomId()));

        Battle battle = new Battle(kingdomService.findKingdomById(attackerKingdomId), kingdomService.findKingdomById(battleRequestDTO.getDefenderKingdomId()));

        for (BattleTroopDTO troop : battleRequestDTO.getTroops()) { //checks if you have enough troops
            if (troop.getQuantity() > battleService.getTroopsForBattle(attackerKingdomId, troop.getType()).size()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorMessage("You don´t have enough troops!"));
            }
        }

        try {
            battle.setBattleTime(Math.round(battle.getBattleTime() + ((distance / battleService.getSpeedOfTheSlowestTroop(battleRequestDTO, attackerKingdomId)) * DefaultValueReaderUtil.getInt("gameCycle.length"))));
            battle.setReturnTime(Math.round(battle.getBattleTime() + ((distance / battleService.getSpeedOfTheSlowestTroop(battleRequestDTO, attackerKingdomId)) * DefaultValueReaderUtil.getInt("gameCycle.length"))));
            battle.setBattleType(battleRequestDTO.getBattleType());

            battleService.saveBattle(battle);

            battleService.sendTroopsIntoBattle(battleRequestDTO, battle);

            Map<String, String> body = new HashMap<>();
            body.put("battleId", String.valueOf(battle.getId()));
            body.put("resolutionTime", String.valueOf(battle.getBattleTime()));

            return new ResponseEntity(body, HttpStatus.OK);

        } catch (DefaultValueNotFoundException | IncorrectDefaultValueTypeException | DefaultValuesFileMissingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorMessage("Default value error!"));
        }
    }

    @GetMapping("/kingdoms/{kingdomId}/battles/{battleId}")
    public ResponseEntity<?> battleResults(@PathVariable("kingdomId") Long kingdomId,
                                           @PathVariable("battleId") Long battleId,
                                           @RequestHeader(value = "Authorization") String token) {
        if (!kingdomService.appUserOwnsKingdom(kingdomId, appUserService.extractAppUserIdFromToken(token))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorMessage("You do not own that kingdom!"));
        }
        updateResourcesService.updateResources(kingdomId);
        Battle battle = battleRepository.getById(battleId);

        if (battleRepository.findById(battle.getId()).isEmpty()) {
            JSONObject battleNotExist = new JSONObject();
            battleNotExist.put("error", "battle not exists!");
            return new ResponseEntity<>(battleNotExist, HttpStatus.OK);

        } else if (battle.getAttackerWins() == null) {
            JSONObject battleNotExist = new JSONObject();
            battleNotExist.put("info", "the battle has not yet taken place!");
            return new ResponseEntity<>(battleNotExist, HttpStatus.OK);
        }

        return battleService.battleResult(battle, kingdomId);
    }

    @GetMapping("") // for battle method testing purposes
    public String battle() {
        //battleService.troopAttack(6L);
        battleService.destructionAttack(20L);
        //battleService.takeoverAttack(18L);
        //battleService.spyAttack(14L);
        return "ok";
    }

    @GetMapping(value = "/kingdom/{id}/buildings")
    public ResponseEntity<?> getKingdomConstructionPlaces(@PathVariable(name = "id") Long id,
                                                 @RequestHeader(value = "Authorization") String token) {
        if (kingdomService.findKingdomById(id) == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorMessage("This kingdom does not exist."));
        } else if (token.isEmpty() || !kingdomService.appUserOwnsKingdom(id, appUserService.extractAppUserIdFromToken(token))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorMessage("This kingdom does not belong to authenticated player!"));
        } else {
            updateResourcesService.updateResources(id);
            KingdomBuildingsDTO constructionPlacesDetails = kingdomService.getConstructionPlacesList(id);
            return ResponseEntity.status(HttpStatus.OK).body(constructionPlacesDetails);
        }
    }

    @GetMapping(value = "/buildingTypes/{kingdomId}")
    public ResponseEntity<?> getBuildingTypes(@PathVariable(name = "kingdomId") Long kingdomId,
                                              @RequestHeader(value = "Authorization") String token) {
        if (token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorMessage("No token!"));
        } else {
            BuildingTypesDTO buildingTypesDTO = buildingService.getBuildingTypes(kingdomId);
            return ResponseEntity.status(HttpStatus.OK).body(buildingTypesDTO);
        }
    }

}
