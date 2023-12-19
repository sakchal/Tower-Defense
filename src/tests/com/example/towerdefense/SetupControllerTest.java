package com.example.towerdefense;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SetupControllerTest {
    private SetupController controller;
    private String validName;
    private String invalidName;
    private String invalidName2;
    private String validDiff;
    private String invalidDiff;
    private String invalidDiff2;

    @BeforeEach
    public void setup() {
        controller = new SetupController();
        validName = "Adityaa";
        invalidName = "     ";
        invalidName2 = "";
        validDiff = "Normal";
        invalidDiff = "Super Hard";
        invalidDiff2 = "";
    }

    @Test
    /*
     * (M2) Tests if a name is correctly identified as valid.
     */
    public void validNameTest() {
        assertTrue(controller.isValidName(validName));
    }

    @Test
    /*
     * (M2) Tests if names with spaces are correctly identified as invalid.
     */
    public void invalidSpacesNameTest() {
        assertFalse(controller.isValidName(invalidName));
    }

    @Test
    /*
     * (M2) Tests if blank names are correctly identified as invalid.
     */
    public void invalidBlankNameTest() {
        assertFalse(controller.isValidName(invalidName2));
    }

    @Test
    /*
     * (M2) Tests if difficulty inputs is correctly identified as valid.
     */
    public void validDifficultyTest() {
        assertTrue(controller.isValidDifficulty(validDiff));
    }

    @Test
    /*
     * (M2) Tests if an incorrect difficulty input is correctly identified as invalid.
     */
    public void invalidDifficultyTest() {
        assertFalse(controller.isValidDifficulty(invalidDiff));
    }

    @Test
    /*
     * (M2) Tests if a blank difficulty input is correctly identified as invalid.
     */
    public void invalidBlankDifficultyTest() {
        assertFalse(controller.isValidDifficulty(invalidDiff2));
    }
}
