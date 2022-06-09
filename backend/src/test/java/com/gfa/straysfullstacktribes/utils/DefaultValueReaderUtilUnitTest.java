package com.gfa.straysfullstacktribes.utils;

import com.gfa.straysfullstacktribes.exceptions.defaultValuesExceptions.DefaultValueNotFoundException;
import com.gfa.straysfullstacktribes.exceptions.defaultValuesExceptions.DefaultValuesFileMissingException;
import com.gfa.straysfullstacktribes.exceptions.defaultValuesExceptions.IncorrectDefaultValueTypeException;
import com.gfa.straysfullstacktribes.utilities.DefaultValueReaderUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DefaultValueReaderUtilUnitTest {

    @Test
    void getIntCorrectValue() throws DefaultValueNotFoundException, IncorrectDefaultValueTypeException, DefaultValuesFileMissingException {
        int farmProduction = DefaultValueReaderUtil.getInt("tokenExpirationTime.registrationToken");
        assertEquals(86400,farmProduction);
    }

    @Test
    void getIntThrowsDefaultValueNotFoundException() {
        Exception exception = assertThrows(DefaultValueNotFoundException.class, () -> DefaultValueReaderUtil.getInt("buildings.food"));
        String expectedMessage = "there is no such value as: food";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void getIntThrowsIncorrectDefaultValueTypeException() {
        Exception exception = assertThrows(IncorrectDefaultValueTypeException.class, () -> DefaultValueReaderUtil.getInt("dummy.string"));
        String expectedMessage = "the value have a different data type";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}