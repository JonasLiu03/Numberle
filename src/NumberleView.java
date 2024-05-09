import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import javax.swing.border.AbstractBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.geom.RoundRectangle2D;

public class NumberleView extends JFrame implements Observer {
    private NumberleController controller;
    private NumberleModel model;
    private JButton[][] gridButtons = new JButton[6][7]; // 6行7列的网格
    private JPanel gridPanel = new JPanel(new GridLayout(6, 7));
    private JPanel inputPanel = new JPanel();
    private JTextField inputField = new JTextField(20);
    private int currentAttempt = 0;

    private JButton[] numberButtons = new JButton[10];
    private JButton[] operationButtons = new JButton[8];
    private  Color greenColor= new Color(81,193,166);
    private  Color orangeColor = new Color(239,153,109);
    private Color grayColor = new Color(165,174,196);
    private Color borderInitial = new Color(222,225,233);
    public class RoundedButton extends JButton {
        private static final int ARC_WIDTH = 10;  // 圆角的宽度
        private static final int ARC_HEIGHT = 10;  // 圆角的高度

        private Color baseColor;  // 基础颜色
        private Color hoverColor;  // 悬浮时颜色
        private Color pressedColor;  // 按下时颜色
        private boolean customBackgroundSet = false;

        public RoundedButton(String label, Color base, Color hover, Color pressed) {
            super(label);
            this.baseColor = base;
            this.hoverColor = hover;
            this.pressedColor = pressed;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
        }

        @Override
        public void setBackground(Color bg) {
            super.setBackground(bg);
            // 如果背景颜色是基础颜色，则视为未设置自定义背景
            customBackgroundSet = !bg.equals(baseColor);
            repaint();  // 重新绘制以应用变更
//            super.setBackground(bg);
//            customBackgroundSet = true;

        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Determine color based on button state and whether a custom background is set
            Color useColor = baseColor; // Default to base color
            if (customBackgroundSet) {
                useColor = getBackground(); // Use the custom background if set
            } else if (getModel().isPressed()) {
                useColor = pressedColor;
            } else if (getModel().isRollover()) {
                useColor = hoverColor;
            }

            g2.setColor(useColor);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, ARC_WIDTH, ARC_HEIGHT);
            // 绘制灰色边框
            g2.setColor(borderInitial);
            g2.setStroke(new BasicStroke(1)); // 设置边框宽度
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, ARC_WIDTH, ARC_HEIGHT);
            super.paintComponent(g2);
            g2.dispose();

        }
    }
//    public class RoundedCornerBorder extends AbstractBorder {
//        private final Color borderColor;
//        private final int cornerRadius;
//
//        public RoundedCornerBorder(Color borderColor, int cornerRadius) {
//            this.borderColor = borderColor;
//            this.cornerRadius = cornerRadius;
//        }
//
//        @Override
//        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
//            super.paintBorder(c, g, x, y, width, height);
//            Graphics2D g2d = (Graphics2D) g.create();
//            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//            g2d.setColor(borderColor);
//            g2d.drawRoundRect(x, y, width - 1, height - 1, cornerRadius, cornerRadius);
//            g2d.dispose();
//        }
//
//        @Override
//        public Insets getBorderInsets(Component c) {
//            return new Insets(this.cornerRadius, this.cornerRadius, this.cornerRadius, this.cornerRadius);
//        }
//
//        @Override
//        public Insets getBorderInsets(Component c, Insets insets) {
//            insets.left = insets.top = insets.right = insets.bottom = this.cornerRadius;
//            return insets;
//        }
//    }
    public NumberleView(NumberleModel model) {
        this.model = model;
        initializeWindow();
        initializeGridPanel();
        initializeInputPanel();
        setupInputFieldListener(); // 设置输入字段监听器
        setController(new NumberleController(model, this)); // 此行可能需要根据实际初始化调整
//        System.out.println("2");
    }
    private void setupInputFieldListener() {
        inputField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                updateGridDisplay();
            }
            public void removeUpdate(DocumentEvent e) {
                updateGridDisplay();
            }
            public void changedUpdate(DocumentEvent e) {
                updateGridDisplay();
            }

            private void updateGridDisplay() {
                String text = inputField.getText();
                clearCurrentAttemptGrid();
                for (int i = 0; i < text.length() && i < gridButtons[currentAttempt].length; i++) {
                    gridButtons[currentAttempt][i].setText(String.valueOf(text.charAt(i)));
                }
            }
        });
    }
    private void initializeWindow() {
        setTitle("Numberle");
        setSize(820, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        add(gridPanel, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);
        setVisible(true);
    }

    private void initializeGridPanel() {
        gridPanel.setLayout(new GridLayout(6, 7, 5, 5));
//        gridPanel.setBorder(new RoundedCornerBorder(Color.RED,10));
        gridPanel.setBorder(BorderFactory.createEmptyBorder(5, 110, 5, 110));
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                Color base = new Color(255, 255, 255);  // Cornflower Blue
                Color hover = new Color(255, 255, 255);  // Steel Blue
                Color pressed = new Color(255, 255, 255);  // Royal Blue
                RoundedButton button = new RoundedButton("", base, hover, pressed);
                button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
                gridButtons[i][j] = button;
                gridPanel.add(button);
            }
        }
    }
    private void initializeInputPanel() {
        inputPanel.setLayout(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 30));
//        gridPanel.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;

        // First row - Number buttons from 1 to 9 and 0
        for (int i = 0; i < 10; i++) {
            constraints.gridx = i;
            constraints.gridy = 0;
            constraints.gridwidth = 1;
            addButton(i == 9 ? "0" : Integer.toString(i + 1), inputPanel, constraints,numberButtons,i == 9 ? 0 : i + 1);
        }

        // Second row - Operation and control buttons plus a new reset button
        String[] secondRowButtons = {"delete", "+", "-", "*", "/", "=", "enter", "reset"};
        int[] gridWidths = {2, 1, 1, 1, 1, 1, 2, 2}; // Include custom width for reset button

        for (int i = 0, gridx = 0; i < secondRowButtons.length; i++) {
            constraints.gridx = gridx;
            constraints.gridy = 1;
            constraints.gridwidth = gridWidths[i];
            addButton(secondRowButtons[i], inputPanel, constraints,operationButtons,i);
            gridx += gridWidths[i];
        }
    }


private void addButton(String text, JPanel panel, GridBagConstraints constraints, JButton[] buttonsArray, int index) {
    // 使用RoundedButton代替普通的JButton，以应用圆角和特定的颜色
    Color baseColor = new Color(221, 225, 237); // 浅灰色作为基础颜色
    Color hoverColor = new Color(231, 235, 247); // 悬浮时的颜色稍微亮一些
    Color pressedColor = new Color(211, 215, 227); // 按下时的颜色稍微暗一些

    RoundedButton button = new RoundedButton(text, baseColor, hoverColor, pressedColor);
    button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12)); // 设置字体，可以根据需要调整

    // 添加到面板
    panel.add(button, constraints);

    // 保存按钮引用到数组，以便后续可以访问
    buttonsArray[index] = button;

    // 添加事件监听器
    button.addActionListener(this::handleButtonPress); // 假设有一个处理按钮点击的方法
}


    private void handleButtonPress(ActionEvent e) {
        String command = e.getActionCommand();

        if ("delete".equals(command) && !inputField.getText().isEmpty()) {
            inputField.setText(inputField.getText().substring(0, inputField.getText().length() - 1));
        } else if ("enter".equals(command)) {
            if (controller != null) {
                controller.processUserInput(inputField.getText());
                inputField.setText("");
            } else {
                displayInvalidInputMessage();
            }
        } else if ("reset".equals(command)) {
//            if (controller != null) {
//                controller.resetGame();  // Assuming there is a method in the controller to reset the game
//            }else{
//                displayInvalidInputMessage();
//            }
            if (controller != null&&currentAttempt!=0) {
                controller.resetGame();  // Assuming there is a method in the controller to reset the game
            }else{
                displayInvalidInputMessage();
            }
        } else {
            inputField.setText(inputField.getText() + command);
        }
    }



    public void setController(NumberleController controller) {
        this.controller = controller;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof ArrayList<?>) {
            ArrayList<String> feedback = (ArrayList<String>) arg;
            updateGridWithFeedback(feedback);
        } else if (arg instanceof String) {
            if ("max_attempts_reached".equals(arg)) {
                promptForNewGame("Game lost. Try again?");
            } else if("won".equals(arg)){
                System.out.println("call");
                promptForNewGame("Game won. Try new one?");
            }else if ("invalid_input".equals(arg)) {
                displayInvalidInputMessage();
            }
        }

    }

    private int loopForNumber(char target,JButton[] number){
        int index = 100;
        for(int i=0; i<=9;i++){
            if(target==(number[i].getText().charAt(0))){
                index = i;
            }
        }

        return index;
    }
    private int loopForOperation(char target,JButton[] operation){
        int index = 100;
        for(int j=0;j<=7;j++){
            if(target==(operation[j].getText().charAt(0))){
                index = j;
            }
        }
        return index;
    }
    private void changeButtonColor(JButton button, Color color){
        button.setBackground(color);
        button.setForeground(Color.WHITE);
    }
    private void changeGridColor(JButton button, Color color, char target){
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setText(String.valueOf(target));
//        gridButtons[currentAttempt][i].setBackground(greenColor);
//        gridButtons[currentAttempt][i].setForeground(Color.WHITE);
//        gridButtons[currentAttempt][i].setText(String.valueOf(green.charAt(i)));
    }
    private void updateGridWithFeedback(ArrayList<String> feedback) {
        clearCurrentAttemptGrid();
        String gray = feedback.get(0);
        String orange = feedback.get(1);
        String green = feedback.get(2);
        int greenCount = 0;
//        System.out.println(operationButtons[2].getBackground());
        for (int i = 0; i < 7; i++) {
            if (green.charAt(i) != '_') {

                int greenNum = loopForNumber(green.charAt(i),numberButtons);
                if(greenNum!=100){
                    changeButtonColor(numberButtons[greenNum],greenColor);
                    greenCount++;
                }

                int greenOpe = loopForOperation(green.charAt(i),operationButtons);
                if(greenOpe!=100){
                    changeButtonColor(operationButtons[greenOpe],greenColor);
                    greenCount++;
                }
                changeGridColor(gridButtons[currentAttempt][i], greenColor, green.charAt(i));
//                gridButtons[currentAttempt][i].setBackground(greenColor);
//                gridButtons[currentAttempt][i].setForeground(Color.WHITE);
//                gridButtons[currentAttempt][i].setText(String.valueOf(green.charAt(i)));

            } else if (orange.charAt(i) != '_') {
                int orangeNum = loopForNumber(orange.charAt(i),numberButtons);
                if(orangeNum!=100 && !numberButtons[orangeNum].getBackground().equals(greenColor)){
                    changeButtonColor(numberButtons[orangeNum],orangeColor);
                }

                int orangeOpe = loopForOperation(orange.charAt(i),operationButtons);
                if(orangeOpe!=100 && !operationButtons[orangeOpe].getBackground().equals(greenColor)){
                    changeButtonColor(operationButtons[orangeOpe],orangeColor);
                }
                changeGridColor(gridButtons[currentAttempt][i], orangeColor, orange.charAt(i));
//                gridButtons[currentAttempt][i].setBackground(orangeColor);
//                gridButtons[currentAttempt][i].setForeground(Color.WHITE);
//
//                gridButtons[currentAttempt][i].setText(String.valueOf(orange.charAt(i)));
            } else if (gray.charAt(i) != '_') {
                int grayNum = loopForNumber(gray.charAt(i),numberButtons);
                if(grayNum!=100){
                    changeButtonColor(numberButtons[grayNum],grayColor);
                }

                int grayOpe = loopForOperation(gray.charAt(i),operationButtons);
                if(grayOpe!=100){
                    changeButtonColor(operationButtons[grayOpe],grayColor);
                }
                changeGridColor(gridButtons[currentAttempt][i], grayColor, gray.charAt(i));
//                gridButtons[currentAttempt][i].setBackground(grayColor);
//                gridButtons[currentAttempt][i].setForeground(Color.WHITE);
//
//                gridButtons[currentAttempt][i].setText(String.valueOf(gray.charAt(i)));
            }
        }
        currentAttempt++;
        inputField.setEditable(false);
        System.out.println(greenCount);
        if(greenCount == 7){
            controller.setGameWin(true);
        }
          // Lock the input field after valid submission
    }

    private void clearCurrentAttemptGrid() {
        for (JButton button : gridButtons[currentAttempt]) {
            button.setText("");
        }
    }
    public void clearInputField() {
//        System.out.println("call");
        inputField.setText("");  // 清空输入字段
//        System.out.println("Call");
    }
    public void displayInvalidInputMessage() {
        JOptionPane.showMessageDialog(this, "无效输入。请检查并修正您的输入。", "输入错误", JOptionPane.ERROR_MESSAGE);
        inputField.setEditable(true);  // 使输入框再次可编辑
        inputField.requestFocusInWindow();
//        inputField.setEditable(true);  // 使输入框再次可编辑
//        inputField.requestFocusInWindow();  // 将焦点返回到输入框
        // 注意：这里不调用 inputField.setText(""); 确保用户可以修改已输入的文本
    }

    public void resetInputPanel(){
        Color buttonColor = new Color(221, 225, 237);
        for(JButton button:numberButtons){
            button.setBackground(buttonColor);
            button.setForeground(Color.BLACK);
        }
        for(JButton button:operationButtons){
            button.setBackground(buttonColor);
            button.setForeground(Color.BLACK);
        }
    }
    public void resetGridPanel(){
        Color baseColor = new Color(255, 255, 255);
        Color hoverColor = new Color(255, 255, 255);
        Color pressedColor = new Color(255, 255, 255);

        clearInputField();  // 清除输入字段
        for (int i = 0; i < gridButtons.length; i++) {
            for (int j = 0; j < gridButtons[i].length; j++) {
                JButton button = gridButtons[i][j];
                if (button instanceof RoundedButton) {
                    RoundedButton roundedButton = (RoundedButton) button;
                    roundedButton.customBackgroundSet = false;  // 重置自定义背景标志
                    roundedButton.setBackground(baseColor);  // 重置背景颜色
                    roundedButton.setForeground(Color.BLACK);

                }
                button.setText("");  // 清除网格上的所有文本
            }
        }
    }
    public void resetDisplay() {
        resetGridPanel();
        resetInputPanel();

        currentAttempt = 0;  // 重置当前尝试的计数器
        inputField.setEditable(true);  // 使输入字段再次可编辑
        repaint();
        System.out.println(currentAttempt);
    }
//    private void promptForNewGame(String message) {
//        int response = JOptionPane.showConfirmDialog(this, "End, Do you want a new game?", "Won", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
//        if (response == JOptionPane.YES_OPTION) {
//            controller.resetGame();  // 重置游戏
//        }
//    }
    private void promptForNewGame(String message) {
        int response = JOptionPane.showConfirmDialog(this, message, "Game end", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
        if (response == JOptionPane.YES_OPTION) {
            System.out.println("run");
            controller.resetGame();
        } else if (response == JOptionPane.NO_OPTION) {
            System.exit(0);
        }
    }

}
