package com.gfa.straysfullstacktribes.models;

import com.gfa.straysfullstacktribes.models.dtos.KingdomRegistrationRequestModelDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KingdomRegistrationRequestDTOModelTest {

    @Test
    void createFullKingdomRegistrationModel() {
        KingdomRegistrationRequestModelDTO registration = new KingdomRegistrationRequestModelDTO("user", "123", "user kingdom", 10L, 10L);
        assertEquals("user", registration.getUsername());
        assertEquals("123", registration.getPassword());
        assertEquals("user kingdom", registration.getKingdomName());
        assertEquals(10L, registration.getCoordinateX());
        assertEquals(10L, registration.getCoordinateY());
    }

    @Test
    void createEmptyKingdomRegistrationModel() {
        KingdomRegistrationRequestModelDTO registration = new KingdomRegistrationRequestModelDTO();
        assertNull(registration.getUsername());
        assertNull(registration.getPassword());
        assertNull(registration.getKingdomName());
        assertNull(registration.getCoordinateX());
        assertNull(registration.getCoordinateY());
    }

}