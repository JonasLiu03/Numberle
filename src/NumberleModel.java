import java.util.Observable;
import java.util.ArrayList;
import java.util.Random;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.Stack;
import java.util.regex.Pattern;

public class NumberleModel extends Observable {
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

    private void generateTargetEquation() {
        try {
            var lines = Files.readAllLines(Paths.get("D:/AOOPCW/CW/src/equations.txt"));
            targetEquation = lines.get(new Random().nextInt(lines.size()));
            System.out.println(targetEquation);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public boolean keepRunning(){
        return attempts<maxAttempts;
    }

    public void makeGuess(String guess) {
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
    public boolean validateInput(String input) {
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
    public ArrayList<Character> getGrayChars() { return grayChars; }

    public ArrayList<Character> getGreenChars() { return greenChars; }

    public ArrayList<Character> getOrangeChars() { return orangeChars; }

    public boolean isGameWon() { return gameWon; }
    public void gameWon(){
        gameWon = !gameWon;
    }

    public int getAttempts() { return attempts; }
    public void reset() {
        generateTargetEquation();  // 重新生成一个目标方程
        attempts = 0;  // 重置尝试次数
        gameWon = false;  // 重置游戏胜利状态
        setChanged();
        notifyObservers(null);  // 可以通知观察者状态重置
    }

}
