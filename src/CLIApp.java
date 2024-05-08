
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.Stack;

public class CLIApp {
    private String targetEquation;
    private int attempts;
    private boolean gameWon = false;

    public CLIApp() {
        generateTargetEquation();
    }

    private void generateTargetEquation() {
        try {
            var lines = Files.readAllLines(Paths.get("D:/AOOPCW/CW/src/equations.txt")); // Update with your actual file path
            targetEquation = lines.get(new Random().nextInt(lines.size()));
            System.out.println("Target Equation (for debugging): " + targetEquation); // Remove or comment out for actual gameplay
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private boolean validateInput(String input) {
        // 首先检查输入是否符合基本格式：由数字、运算符和等号组成的7字符字符串
        if (input.length() != 7) {
            System.out.println("Invalid input length. Please ensure the input is exactly 7 characters.");
            return false;
        }

        // 定义一个正则表达式，以确保输入仅包含数字和指定的运算符
        String pattern = "[0-9\\+\\-\\*/=]+";
        if (!Pattern.matches(pattern, input)) {
            System.out.println("Invalid input characters. Please use only digits (0-9) and operators (+, -, *, /, =).");
            return false;
        }

        // 检查输入中是否包含等号“=”
        if (!input.contains("=")) {
            System.out.println("No equal '=' sign.");
            return false;
        }

        // 分割输入字符串，基于等号将其分为左右两部分
        String[] parts = input.split("=");
        if (parts.length != 2) {
            System.out.println("Invalid equation format. Please ensure there is one '=' sign.");
            return false;
        }

        try {
            double leftValue = evaluateSimpleExpression(parts[0]);
            double rightValue = evaluateSimpleExpression(parts[1]);
            if (Math.abs(leftValue - rightValue) > 0.0001) { // Using a small threshold to avoid floating point precision issues
                System.out.println("The left side is not equal to the right.");
                return false;
            }
        } catch (Exception e) {
            System.out.println("Error evaluating expressions: " + e.getMessage());
            return false;
        }

        System.out.println("Input is valid and equations are balanced.");
        return true;
    }
    private double evaluateSimpleExpression(String expression) {
        return new ExpressionEvaluator().evaluate(expression);
    }
    private static class ExpressionEvaluator {
        public double evaluate(String expression) {
            char[] tokens = expression.toCharArray();
            Stack<Double> values = new Stack<>();
            Stack<Character> ops = new Stack<>();

            for (int i = 0; i < tokens.length; i++) {
                if (tokens[i] == ' ') continue; // Skip spaces

                if (tokens[i] >= '0' && tokens[i] <= '9') {
                    StringBuffer sbuf = new StringBuffer();
                    while (i < tokens.length && tokens[i] >= '0' && tokens[i] <= '9') {
                        sbuf.append(tokens[i++]);
                    }
                    values.push(Double.parseDouble(sbuf.toString()));
                    i--; // Since the for loop also increases i
                } else if (tokens[i] == '+' || tokens[i] == '-' || tokens[i] == '*' || tokens[i] == '/') {
                    while (!ops.empty() && hasPrecedence(tokens[i], ops.peek())) {
                        values.push(applyOp(ops.pop(), values.pop(), values.pop()));
                    }
                    ops.push(tokens[i]);
                }
            }

            while (!ops.empty()) {
                values.push(applyOp(ops.pop(), values.pop(), values.pop()));
            }

            return values.pop();
        }

        public boolean hasPrecedence(char op1, char op2) {
            if ((op2 == '*' || op2 == '/') && (op1 == '+' || op1 == '-'))
                return false;
            else
                return true;
        }

        public double applyOp(char op, double b, double a) {
            switch (op) {
                case '+':
                    return a + b;
                case '-':
                    return a - b;
                case '*':
                    return a * b;
                case '/':
                    if (b == 0)
                        throw new UnsupportedOperationException("Cannot divide by zero");
                    return a / b;
            }
            return 0;
        }
    }
    private void processUserInput(String input) {
        if (validateInput(input)) {
            makeGuess(input);
        } else {
            System.out.println("Invalid input. Please try again.");
        }
    }

    private void makeGuess(String guess) {
        StringBuilder gray = new StringBuilder("_______");
        StringBuilder orange = new StringBuilder("_______");
        StringBuilder green = new StringBuilder("_______");

        for (int i = 0; i < guess.length(); i++) {
            char guessChar = guess.charAt(i);
            char targetChar = targetEquation.charAt(i);

            if (guessChar == targetChar) {
                green.setCharAt(i, guessChar);
            } else if (targetEquation.indexOf(guessChar) >= 0) {
                orange.setCharAt(i, guessChar);
            } else {
                gray.setCharAt(i, guessChar);
            }
        }

        System.out.println("Gray representation: " + gray);
        System.out.println("Orange representation: " + orange);
        System.out.println("Green representation: " + green);

        if (guess.equals(targetEquation)) {
            System.out.println("Congratulations, you've guessed the equation correctly!");
            gameWon = true;
        } else {
            System.out.println("Incorrect guess. Try again.");
        }

        attempts++;
    }

    public static void main(String[] args) {
        CLIApp game = new CLIApp();
        Scanner scanner = new Scanner(System.in);

        while (!game.gameWon && game.attempts < 6) {
            System.out.println("Enter your equation guess:");
            String guess = scanner.nextLine();
            game.processUserInput(guess);
        }

        if (!game.gameWon) {
            System.out.println("Game over. The correct equation was: " + game.targetEquation);
        }

        scanner.close();
    }
}
