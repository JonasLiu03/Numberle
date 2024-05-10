import java.util.Scanner;
import java.util.HashSet;
import java.util.Set;
public class CLIApp {
    private INumberleModel model;

    public CLIApp() {
        this.model = new NumberleModel(); // Instantiate your model
//        this.model.reset(); // Prepare the game state
    }
    private void makeGuess(String guess) {
        StringBuilder gray = new StringBuilder("_______");
        StringBuilder orange = new StringBuilder("_______");
        StringBuilder green = new StringBuilder("_______");
        StringBuilder white = new StringBuilder();
        Set<Character> allChars = new HashSet<>();
        for (char c = '0'; c <= '9'; c++) {
            allChars.add(c);
        }
        allChars.add('+');
        allChars.add('-');
        allChars.add('*');
        allChars.add('/');

        for (int i = 0; i < guess.length(); i++) {
            char guessChar = guess.charAt(i);
            char targetChar = model.getTargetEquation().charAt(i);

            if (guessChar == targetChar) {
                green.setCharAt(i, guessChar);
                allChars.remove(guessChar);
            } else if (model.getTargetEquation().indexOf(guessChar) >= 0) {
                orange.setCharAt(i, guessChar);
                allChars.remove(guessChar);
            } else {
                gray.setCharAt(i, guessChar);
                allChars.remove(guessChar);
            }
        }
        for (char c : allChars) {
            white.append(c);
        }

        System.out.println("Gray representation: " + gray);
        System.out.println("Orange representation: " + orange);
        System.out.println("Green representation: " + green);
        System.out.println("White representation: " + white);

        if (guess.equals(model.getTargetEquation())) {
            System.out.println("Congratulations, you've guessed the equation correctly!");
//            model.isGameWon() = true;
            model.gameWon();
        } else {
            System.out.println("Incorrect guess. Try again.");
        }

        model.incrementAttempts();
    }

    private void playGame() {
        Scanner scanner = new Scanner(System.in);
        while (!model.isGameWon() && model.getAttempts() < INumberleModel.MAX_ATTEMPTS) {
            System.out.println("Enter your equation guess:");
            String guess = scanner.nextLine();
            if (model.validateInput(guess)) {
                makeGuess(guess);
            } else {
                System.out.println("Invalid input. Please try again.");
            }
        }

        if (!model.isGameWon()) {
            System.out.println("Game over. The correct equation was: " + model.getTargetEquation());
        }

        scanner.close();
    }

    public static void main(String[] args) {
        CLIApp game = new CLIApp();
        game.playGame();
    }
}
