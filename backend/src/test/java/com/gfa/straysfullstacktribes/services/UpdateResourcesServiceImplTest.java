package com.gfa.straysfullstacktribes.services;

import com.gfa.straysfullstacktribes.models.Kingdom;
import com.gfa.straysfullstacktribes.repositories.BuildingRepository;
import com.gfa.straysfullstacktribes.repositories.KingdomRepository;
import com.gfa.straysfullstacktribes.repositories.TroopRepository;
import com.gfa.straysfullstacktribes.repositories.UnitLevelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class UpdateResourcesServiceImplTest {

    private KingdomRepository kingdomRepository;
    private TroopRepository troopRepository;
    private BuildingRepository buildingRepository;
    private UnitLevelRepository unitLevelRepository;

    @BeforeEach
    void setup() {
        kingdomRepository = mock(KingdomRepository.class);
        troopRepository = mock(TroopRepository.class);
        buildingRepository = mock(BuildingRepository.class);
        unitLevelRepository = mock(UnitLevelRepository.class);
        assertEquals(0, buildingRepository.findAll().size());
        assertEquals(0, kingdomRepository.findAll().size());
        assertEquals(0, troopRepository.findAll().size());
        assertEquals(0, unitLevelRepository.findAll().size());
    }

    @Test
    void updateGold() {

    }

    @Test
    void updateFood() {
    }

    @Test
    void buildingDestroy() {
    }

    @Test
    void buildingUpgrade() {
    }

    @Test
    void buildingConsumption() {
    }

    @Test
    void troopsConsumption() {
    }
}