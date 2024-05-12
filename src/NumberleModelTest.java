import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NumberleModelTest {
    private NumberleModel model;

//    @BeforeEach
//    void setUp() {
//        model = new NumberleModel();
//    }


    @Test
    void generateTargetEquation() {
        model.generateTargetEquation();
        assertNotNull(model.getTargetEquation(), "targetEquation should not be null after generation.");
        assertFalse(model.getTargetEquation().isEmpty(), "targetEquation should not be empty.");
    }

    @Test
    void validateInput() {
        model = new NumberleModel();
        String validInput = "1+1=2";
        assertTrue(model.validateInput(validInput), "Input should be valid.");

        String invalidInput = "1+1";
        assertFalse(model.validateInput(invalidInput), "Input without '=' should be invalid.");

        String wrongLengthInput = "12+12=24";
        assertFalse(model.validateInput(wrongLengthInput), "Input of incorrect length should be invalid.");
    }

    @Test
    void reset() {
        model = new NumberleModel();
        model.generateTargetEquation();
        String validInput = "2*3-6=0";
        if(model.validateInput(validInput)) {
            model.makeGuess(validInput);
        }
        model.setGameWon(true);
        model.reset();
        assertNotNull(model.getTargetEquation(), "targetEquation should be reset and not null.");
        assertFalse(model.getTargetEquation().isEmpty(), "targetEquation should not be empty after reset.");
        assertEquals(0, model.getAttempts(), "Attempts should be reset to 0.");
        assertFalse(model.isGameWon(), "gameWon should be reset to false.");
    }
}