package com.gfa.straysfullstacktribes.controllers;

import com.gfa.straysfullstacktribes.exceptions.defaultValuesExceptions.DefaultValueNotFoundException;
import com.gfa.straysfullstacktribes.exceptions.defaultValuesExceptions.DefaultValuesFileMissingException;
import com.gfa.straysfullstacktribes.exceptions.defaultValuesExceptions.IncorrectDefaultValueTypeException;
import com.gfa.straysfullstacktribes.models.ErrorMessage;
import com.gfa.straysfullstacktribes.models.StatusMessage;
import com.gfa.straysfullstacktribes.models.dtos.DeleteTypeDTO;
import com.gfa.straysfullstacktribes.models.dtos.TroopCreateDTO;
import com.gfa.straysfullstacktribes.models.dtos.UnitUpgradeDTO;
import com.gfa.straysfullstacktribes.models.enums.TroopType;
import com.gfa.straysfullstacktribes.services.*;
import com.gfa.straysfullstacktribes.utilities.DefaultValueReaderUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class TroopController {

    private KingdomService kingdomService;
    private AppUserService appUserService;
    private TroopService troopService;
    private BuildingService buildingService;
    private UnitLevelService unitLevelService;
    private UpdateResourcesService updateResourcesService;


    @PostMapping("/kingdoms/{id}/troops")
    public ResponseEntity<?> createTroop(@PathVariable(name = "id", required = false) Long id,
                                         @RequestBody(required = false) TroopCreateDTO troopCreateDTO,
                                         @RequestHeader(value = "Authorization") String token)
            throws DefaultValuesFileMissingException, IncorrectDefaultValueTypeException,
            DefaultValueNotFoundException {

        if (id == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorMessage("Provide Kingdom's Id in url path, example: /kingdoms/1/troops"));
        } else if (troopCreateDTO == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorMessage("Provide troop build request in JSON!"));
        } else if (troopCreateDTO.getType() == null || troopCreateDTO.getType().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorMessage("Type is required."));
        } else if (troopCreateDTO.getQuantity() == null || troopCreateDTO.getQuantity() == 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorMessage("Quantity is required."));
        } else if (!kingdomService.kingdomExistsById(id) || kingdomService.kingdomRepoSize() < id) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorMessage("Entered Kingdom id not exists!"));
        } else if (!kingdomService.appUserOwnsKingdom(id, appUserService.extractAppUserIdFromToken(token))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorMessage("You do not own that kingdom!"));
        } else if (TroopType.fromName(troopCreateDTO.getType()) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessage("Entered type does not exist!"));
        } else if (!buildingService.existsKingdomBarracks(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessage("You have to build barracks first!"));
        }
        updateResourcesService.updateResources(id);
        if (kingdomService.findKingdomById(id).getGoldAmount() <
                (long) DefaultValueReaderUtil.getInt("troops." + troopCreateDTO.getType().toLowerCase() +
                        ".price") * troopCreateDTO.getQuantity()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorMessage("Not enough gold!"));
        }
        return ResponseEntity.status(HttpStatus.OK).body(troopService.createTroops(troopCreateDTO, id));
    }

    @PutMapping("/kingdoms/{id}/troops")
    public ResponseEntity<?> upgradeTroops(@PathVariable(name = "id", required = false) Long id,
                                           @RequestBody(required = false) UnitUpgradeDTO unitUpgradeDTO,
                                           @RequestHeader(value = "Authorization") String token)
            throws DefaultValuesFileMissingException, IncorrectDefaultValueTypeException,
            DefaultValueNotFoundException {

        if (id == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorMessage("Provide Kingdom's Id in url path, example: /kingdoms/1/troops"));
        } else if (unitUpgradeDTO == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorMessage("Provide troop build request in JSON!"));
        } else if (unitUpgradeDTO.getType() == null || unitUpgradeDTO.getType().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorMessage("Type is required."));
        } else if (!kingdomService.kingdomExistsById(id) || kingdomService.kingdomRepoSize() < id) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorMessage("Entered Kingdom id not exists!"));
        } else if (!kingdomService.appUserOwnsKingdom(id, appUserService.extractAppUserIdFromToken(token))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorMessage("You do not own that kingdom!"));
        } else if (TroopType.fromName(unitUpgradeDTO.getType()) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ErrorMessage("Entered type does not exist!"));
        } else if (!buildingService.existsKingdomAcademy(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ErrorMessage("You have to build academy first!"));
        }
        updateResourcesService.updateResources(id);
        if (kingdomService.findKingdomById(id).getGoldAmount() <
                (long) (DefaultValueReaderUtil.getInt("buildings.academy.troopUpgradePriceCoefficient")) *
                        (unitLevelService.getKingdomUnitLevel(id, TroopType.fromName(unitUpgradeDTO.getType())) + 1)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorMessage("Not enough gold!"));
        } else if (unitLevelService.getKingdomUnitLevel(id, TroopType.fromName(unitUpgradeDTO.getType())) >= 20) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorMessage("Type of troops already at " +
                    "maximum level!"));
        } else if (buildingService.findKingdomAcademy(id).getLevel() <=
                unitLevelService.getKingdomUnitLevel(id, TroopType.fromName(unitUpgradeDTO.getType()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorMessage("Unable to upgrade troop type, " +
                    "upgrade academy first!"));
        } else if (unitLevelService.isUnitLevelUpgrading(id, TroopType.fromName(unitUpgradeDTO.getType()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorMessage("You are already upgrading this" +
                    " type of troops!"));
        }

        unitLevelService.upgradeTroopType(id, TroopType.fromName(unitUpgradeDTO.getType()));
        return ResponseEntity.status(HttpStatus.OK).body(new StatusMessage("OK"));
    }

    @GetMapping("/kingdoms/{id}/troopList")
    public ResponseEntity<?> getTroopList(@PathVariable(name = "id", required = false) Long id,
                                          @RequestHeader(value = "Authorization") String token) {

        if (id == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorMessage("Provide Kingdom's Id in url path, example: /kingdoms/1/troopList"));
        } else if (!kingdomService.kingdomExistsById(id) || kingdomService.kingdomRepoSize() < id) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorMessage("Entered Kingdom id not exists!"));
        } else if (!kingdomService.appUserOwnsKingdom(id, appUserService.extractAppUserIdFromToken(token))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorMessage("You do not own that kingdom!"));
        }
        updateResourcesService.updateResources(id);

        return ResponseEntity.status(HttpStatus.OK).body(troopService.getListOfTroops(id));
    }

    @GetMapping("/kingdoms/{id}/unitLevel")
    public ResponseEntity<?> getUnitLevelList(@PathVariable(name = "id", required = false) Long id,
                                              @RequestHeader(value = "Authorization") String token)
            throws DefaultValuesFileMissingException, IncorrectDefaultValueTypeException,
            DefaultValueNotFoundException {

        if (id == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorMessage("Provide Kingdom's Id in url path, example: /kingdoms/1/troopList"));
        } else if (!kingdomService.kingdomExistsById(id) || kingdomService.kingdomRepoSize() < id) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorMessage("Entered Kingdom id not exists!"));
        } else if (!kingdomService.appUserOwnsKingdom(id, appUserService.extractAppUserIdFromToken(token))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorMessage("You do not own that kingdom!"));
        }
        updateResourcesService.updateResources(id);

        return ResponseEntity.status(HttpStatus.OK).body(unitLevelService.getKingdomUnitLevel(id));
    }

    @GetMapping("/kingdoms/{id}/queue")
    public ResponseEntity<?> getQueue(@PathVariable(name = "id", required = false) Long id,
                                      @RequestHeader(value = "Authorization") String token) {
        if (id == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorMessage("Provide Kingdom's Id in url path, example: /kingdoms/1/troopList"));
        } else if (!kingdomService.kingdomExistsById(id) || kingdomService.kingdomRepoSize() < id) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorMessage("Entered Kingdom id not exists!"));
        } else if (!kingdomService.appUserOwnsKingdom(id, appUserService.extractAppUserIdFromToken(token))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorMessage("You do not own that kingdom!"));
        }
        updateResourcesService.updateResources(id);

        return ResponseEntity.status(HttpStatus.OK).body(troopService.getTrainingList(id));
    }

    @DeleteMapping("/kingdoms/{id}/troops")
    public ResponseEntity<?> deleteAll(@PathVariable(name = "id", required = false) Long id,
                                       @RequestBody(required = false) DeleteTypeDTO deleteTypeDTO,
                                       @RequestHeader(value = "Authorization") String token)
            throws DefaultValuesFileMissingException, IncorrectDefaultValueTypeException,
            DefaultValueNotFoundException {

        if (id == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorMessage("Provide Kingdom's Id in url path, example: /kingdoms/1/troopList"));
        } else if (!kingdomService.kingdomExistsById(id) || kingdomService.kingdomRepoSize() < id) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorMessage("Entered Kingdom id not exists!"));
        } else if (!kingdomService.appUserOwnsKingdom(id, appUserService.extractAppUserIdFromToken(token))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorMessage("You do not own that kingdom!"));
        } else if (deleteTypeDTO == null) {
            updateResourcesService.updateResources(id);

            troopService.deleteAllInQueue(id);

            return ResponseEntity.status(HttpStatus.OK).body("Troops deleted from queue");

        } else if (deleteTypeDTO.getAmount() == 0 && deleteTypeDTO.getType() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ErrorMessage("Amount too low!"));
        } else if (deleteTypeDTO.getAmount() > 0 && deleteTypeDTO.getType() == null) {
            updateResourcesService.updateResources(id);

            troopService.deleteAmount(id, deleteTypeDTO.getAmount());

            return ResponseEntity.status(HttpStatus.OK).body("Troops deleted from queue");

        } else if (TroopType.fromName(deleteTypeDTO.getType()) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ErrorMessage("Entered type does not exist!"));
        } else

            updateResourcesService.updateResources(id);

        troopService.deleteByType(id, deleteTypeDTO);

        return ResponseEntity.status(HttpStatus.OK).body("Troops of type " + deleteTypeDTO.getType() +
                " deleted from queue");
    }
}
