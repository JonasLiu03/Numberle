import java.util.ArrayList;

public interface INumberleModel {
    int MAX_ATTEMPTS = 6;
    String targetEquation = null;
    int attempts = 0;
    boolean gameWon = false;
    boolean validateInput(String input);  // Validate user input
    void makeGuess(String guess);  // Process a guess from the user
    void reset();  // Reset the model to a default state
    boolean isGameWon();  // Check if the game has been won
    int getAttempts();  // Get the number of attempts made so far
    void generateTargetEquation();
    ArrayList<Character> getGrayChars();  // Get characters that are not in the equation
    ArrayList<Character> getGreenChars();  // Get characters correctly placed in the equation
    ArrayList<Character> getOrangeChars();  // Get characters in the equation but misplaced

    String getTargetEquation();
    int returnAttempts();
    void gameWon();
    void incrementAttempts();
}
