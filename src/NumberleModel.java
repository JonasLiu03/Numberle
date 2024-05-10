import java.util.Observable;
import java.util.ArrayList;
import java.util.Random;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.Stack;
import java.util.regex.Pattern;

public class NumberleModel extends Observable implements INumberleModel{

    private String targetEquation;
    private int attempts;
    private ArrayList<Character> grayChars = new ArrayList<>();
    private ArrayList<Character> greenChars = new ArrayList<>();
    private ArrayList<Character> orangeChars = new ArrayList<>();
    public boolean gameWon = false;
    private int maxAttempts = 5;

    public NumberleModel() {
        System.out.println("Creating NumberleModel instance");
        generateTargetEquation();
        this.attempts = 0;
    }
    public void setGameWon(boolean won){
        this.gameWon = won;
        setChanged();
        notifyObservers("won");
    }
    /**
     * Select random equation from txt file.
     * @pre:  None specific; the method can always execute as it initializes state.
     * @post: targetEquation must not be null or empty after execution.
     */
    @Override
    public void generateTargetEquation() {
        try {
            var lines = Files.readAllLines(Paths.get("src/equations.txt"));
            assert !lines.isEmpty() : "Equation list must not be empty"; // Precondition for file content
            targetEquation = lines.get(new Random().nextInt(lines.size()));
            assert targetEquation != null && !targetEquation.isEmpty() : "Target equation must not be empty"; // Postcondition
            System.out.println(targetEquation);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Input a guess
     * @pre:  guess must be a non-null, 7-character string fitting the pattern.
     * @post: attempts should increase by 1. If guess is correct, gameWon should be true.
     */
    public boolean keepRunning(){
        return attempts<maxAttempts;
    }
    @Override
    public void makeGuess(String guess) {
        assert guess != null && guess.length() == 7 && Pattern.matches("[0-9\\+\\-\\*/=]+", guess) : "Invalid guess format"; // Precondition
        if (validateInput(guess)) {
//            System.out.println(attempts);
            System.out.println(gameWon);
            if(attempts>=maxAttempts && !isGameWon()){
                setChanged();
                notifyObservers("max_attempts_reached");
            } else{
                // 初始化三个字符串为等式长度的 '_'
                StringBuilder gray = new StringBuilder("_______");
                StringBuilder orange = new StringBuilder("_______");
                StringBuilder green = new StringBuilder("_______");

                for (int i = 0; i < guess.length(); i++) {
                    char c = guess.charAt(i);
                    if (targetEquation.charAt(i) == c) {
                        green.setCharAt(i, c);  // 如果字符位置正确，更新 green 字符串
                    } else if (targetEquation.contains(String.valueOf(c))) {
                        orange.setCharAt(i, c);  // 如果字符存在但位置不正确，更新 orange 字符串
                    } else {
                        gray.setCharAt(i, c);  // 如果字符不存在于目标方程式中，更新 gray 字符串
                    }
                }

                // 处理猜测结果
                attempts++;
                gameWon = guess.equals(targetEquation);
                assert !gameWon || guess.equals(targetEquation) : "Game won must be true if guess is correct"; // Postcondition for gameWon

                // 使用更新后的字符串通知观察者
                ArrayList<String> feedback = new ArrayList<>();
                feedback.add(gray.toString());
                feedback.add(orange.toString());
                feedback.add(green.toString());
                setChanged();
                notifyObservers(feedback);


            }
        }else{
            setChanged();
            notifyObservers(guess);
        }

    }
    /**
     * Check whether guess is valid or not
     * @pre:  Input should be a non-null string of length 7.
     * @post: Return true if the input is valid, false otherwise.
     */
    @Override
    public boolean validateInput(String input) {
        assert input != null && input.length() == 7 : "Input must be 7 characters long"; // Precondition
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

        double leftValue = evaluateSimpleExpression(parts[0]);
        double rightValue = evaluateSimpleExpression(parts[1]);
//        System.out.println(leftValue+","+rightValue);
        if (leftValue != rightValue) { // Using a small threshold to avoid floating point precision issues
            System.out.println("The left side is not equal to the right.");
//            System.out.println("Here?");
            return false;
        }

        System.out.println("Input is valid and equations are balanced.");
        return true;
    }
    private double evaluateSimpleExpression(String expression) {
        return new ExpressionEvaluator().evaluate(expression);
    }
    private static class ExpressionEvaluator {
        public int precedence(char op) {
            if (op == '*' || op == '/') {
                return 2;  // Multiplication and division have higher precedence
            } else if (op == '+' || op == '-') {
                return 1;  // Addition and subtraction have lower precedence
            }
            return 0;  // Default precedence for other characters
        }

        public double evaluate(String expression) {
            char[] tokens = expression.toCharArray();
            Stack<Double> values = new Stack<>();
            Stack<Character> ops = new Stack<>();

            for (int i = 0; i < tokens.length; i++) {
                if (tokens[i] == ' ') continue;

                if (tokens[i] >= '0' && tokens[i] <= '9') {
                    StringBuffer sbuf = new StringBuffer();
                    while (i < tokens.length && tokens[i] >= '0' && tokens[i] <= '9') {
                        sbuf.append(tokens[i++]);
                    }
                    values.push(Double.parseDouble(sbuf.toString()));
                    i--;
                } else if (tokens[i] == '+' || tokens[i] == '-' || tokens[i] == '*' || tokens[i] == '/') {
                    while (!ops.isEmpty() && precedence(tokens[i]) <= precedence(ops.peek())) {
                        values.push(applyOp(ops.pop(), values.pop(), values.pop()));
                    }
                    ops.push(tokens[i]);
                }
            }

            while (!ops.isEmpty()) {
                values.push(applyOp(ops.pop(), values.pop(), values.pop()));
            }

            return values.pop();
        }

        private double applyOp(char op, double b, double a) {
            switch (op) {
                case '+': return a + b;
                case '-': return a - b;
                case '*': return a * b;
                case '/':
                    if (b == 0) throw new UnsupportedOperationException("Cannot divide by zero");
                    return a / b;
            }
            throw new UnsupportedOperationException("Unsupported operation " + op);
        }
    }
    /**
     * Returns the list of characters that have been identified as not present in the target equation.
     * @pre None, as the getter has no restrictions.
     * @post Returns the current state of grayChars; should not be null.
     */
    @Override
    public ArrayList<Character> getGrayChars() {
        assert grayChars != null : "grayChars should never be null";
        return grayChars;
    }
    /**
     * Returns the list of characters correctly positioned in the target equation.
     * @pre None, as the getter has no restrictions.
     * @post Returns the current state of greenChars; should not be null.
     */
    @Override
    public ArrayList<Character> getGreenChars() {
        assert greenChars != null : "greenChars should never be null";
        return greenChars;
    }
    /**
     * Returns the list of characters that are present in the target equation but wrongly positioned.
     * @pre None, as the getter has no restrictions.
     * @post Returns the current state of orangeChars; should not be null.
     */
    @Override
    public ArrayList<Character> getOrangeChars() {
        assert orangeChars != null : "orangeChars should never be null";
        return orangeChars;
    }
    /**
     * Checks if the game has been won.
     * @pre None, as the getter has no restrictions.
     * @post Returns the boolean state of gameWon.
     */
    @Override
    public boolean isGameWon() {
        return gameWon;
    }
    /**
     * Toggles the gameWon status.
     * @pre None, as toggling state does not require a specific condition.
     * @post gameWon state is toggled to its opposite value.
     */
    @Override
    public void gameWon(){
        boolean oldState = gameWon;
        gameWon = !gameWon;
        assert gameWon != oldState : "gameWon state must toggle";
    }
    /**
     * Returns the current number of attempts made.
     * @pre None, as the getter has no restrictions.
     * @post Returns the count of attempts.
     */
    @Override
    public int getAttempts() {
        return attempts;
    }
    /**
     * Resets the game to its initial state.
     * @pre None specific; method can execute in any state.
     * @post Ensures targetEquation is not null or empty, attempts reset to 0, gameWon set to false.
     */
    @Override
    public void reset() {
        generateTargetEquation();  // Ensure a new target equation is set
        int oldAttempts = attempts;
        boolean oldGameWon = gameWon;
        attempts = 0;
        gameWon = false;
        setChanged();
        notifyObservers(null);
        assert targetEquation != null && !targetEquation.isEmpty() : "targetEquation must be reset to a non-empty value";
        assert attempts == 0 && oldAttempts != 0 : "attempts must be reset to zero";
        assert !gameWon && oldGameWon != gameWon : "gameWon must be reset to false";
    }
    /**
     * Returns the target equation.
     * @pre None, as the getter has no restrictions.
     * @post Returns the current target equation; should not be null or empty.
     */
    @Override
    public String getTargetEquation(){
        assert targetEquation != null && !targetEquation.isEmpty() : "targetEquation must not be null or empty";
        return targetEquation;
    }
    /**
     * Returns the current number of attempts made.
     * @pre None, as the getter has no restrictions.
     * @post Returns the count of attempts; must be non-negative.
     */
    @Override
    public int returnAttempts(){
        assert attempts >= 0 : "attempts should always be non-negative";
        return attempts;
    }
    /**
     * Increments the count of attempts by one.
     * @pre None specific; this operation is always permitted.
     * @post Ensures that attempts are incremented by one from its previous value.
     */
    @Override
    public void incrementAttempts(){
        int oldAttempts = attempts;
        attempts++;
        assert attempts == oldAttempts + 1 : "attempts should be incremented by one";
    }

}
