//import java.awt.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.util.ArrayList;
//import java.util.Observable;
//import java.util.Observer;
//import javax.swing.*;
//import javax.swing.event.DocumentEvent;
//import javax.swing.event.DocumentListener;
//
//public class old extends JFrame implements Observer {
//    private NumberleController controller;
//    private JButton[][] gridButtons = new JButton[6][7]; // 6行7列的网格按钮
//    private JPanel gridPanel = new JPanel(new GridLayout(6, 7));
//    private JPanel inputPanel = new JPanel();
//    private JTextField inputField = new JTextField(20);
//    private int currentAttempt = 0;
//
//    public old() {
//        initializeWindow();
//        initializeGridPanel();
//        initializeInputPanel();
//        setupInputFieldListener(); // 设置输入字段监听器
//        setController(new NumberleController(new NumberleModel(), this)); // 此行可能需要根据实际初始化调整
//    }
//    private void setupInputFieldListener() {
//        inputField.getDocument().addDocumentListener(new DocumentListener() {
//            public void insertUpdate(DocumentEvent e) {
//                updateGridDisplay();
//            }
//            public void removeUpdate(DocumentEvent e) {
//                updateGridDisplay();
//            }
//            public void changedUpdate(DocumentEvent e) {
//                updateGridDisplay();
//            }
//
//            private void updateGridDisplay() {
//                String text = inputField.getText();
//                clearCurrentAttemptGrid();
//                for (int i = 0; i < text.length() && i < gridButtons[currentAttempt].length; i++) {
//                    gridButtons[currentAttempt][i].setText(String.valueOf(text.charAt(i)));
//                }
//            }
//        });
//    }
//    private void initializeWindow() {
//        setTitle("Numberle");
//        setSize(800, 600);
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        setLayout(new BorderLayout());
//        add(gridPanel, BorderLayout.CENTER);
//        add(inputPanel, BorderLayout.SOUTH);
//        setVisible(true);
//    }
//
//    private void initializeGridPanel() {
//        for (int i = 0; i < 6; i++) {
//            for (int j = 0; j < 7; j++) {
//                JButton button = new JButton();
//                button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
//                gridButtons[i][j] = button;
//                gridPanel.add(button);
//            }
//        }
//    }
//
////    private void initializeInputPanel() {
////        inputPanel.setLayout(new GridLayout(1, 9));
////        String[] buttons = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "+", "-", "*", "/", "=", "delete", "submit"};
////        for (String buttonText : buttons) {
////            System.out.println("accessing loop");
////            JButton button = new JButton(buttonText);
////            button.addActionListener(new ActionListener() {
////                @Override
////                public void actionPerformed(ActionEvent e) {
////                    String command = e.getActionCommand();
////                    if ("delete".equals(command) && !inputField.getText().isEmpty()) {
////                        inputField.setText(inputField.getText().substring(0, inputField.getText().length() - 1));
////                    } else if (!"submit".equals(command) && !"delete".equals(command)) {
////                        inputField.setText(inputField.getText() + command);
////                    } else if ("submit".equals(command)) {
////                        if (controller != null && controller.validateInput(inputField.getText())) {
////                            controller.processUserInput(inputField.getText());
////                            System.out.println("call actionPerformed");
////                        }
////                    }
////                }
////            });
////            inputPanel.add(button);
////        }
////
////        inputField.setEditable(false);
////        inputPanel.add(inputField);
////    }
//private void initializeInputPanel() {
//    inputPanel.setLayout(new GridLayout(1, 9));
//    String[] buttons = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "+", "-", "*", "/", "=", "delete", "submit"};
//    for (String buttonText : buttons) {
//        JButton button = new JButton(buttonText);
//        button.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                String command = e.getActionCommand();
//                System.out.println("Button pressed: " + command);  // Log which button was pressed
//
//                if ("delete".equals(command) && !inputField.getText().isEmpty()) {
//                    inputField.setText(inputField.getText().substring(0, inputField.getText().length() - 1));
//                    System.out.println("After delete: " + inputField.getText());  // Log the state after deletion
//                } else if (!"submit".equals(command) && !"delete".equals(command)) {
//                    inputField.setText(inputField.getText() + command);
//                    System.out.println("After appending: " + inputField.getText());  // Log the state after appending
//                } else if ("submit".equals(command)) {
//                    System.out.println("Submit pressed with input: " + inputField.getText());  // Log submit action
//                    if (controller != null) {
//                        controller.processUserInput(inputField.getText());
//                    } else {
//                        displayInvalidInputMessage(); // This should only happen if there's a system error where the controller isn't set
//                        System.out.println("Controller is not available, error displayed");
//                    }
////                    System.out.println("Submit pressed with input: " + inputField.getText());  // Log submit action
////                    if (controller != null && controller.validateInput(inputField.getText())) {
////                        controller.processUserInput(inputField.getText());
////                        System.out.println("Input is valid, processed input");
////                    } else {
////                        displayInvalidInputMessage();
////                        System.out.println("Input is invalid, displayed error");
////                    }
//                }
//            }
//        });
//        inputPanel.add(button);
//    }
//
//    inputField.setEditable(true);  // Ensure the field is editable
//    inputPanel.add(inputField);
//}
//
//
//    public void setController(NumberleController controller) {
//        this.controller = controller;
//    }
//
//    @Override
//    public void update(Observable o, Object arg) {
//        if (arg instanceof ArrayList<?>) {
//            updateGridWithFeedback((ArrayList<String>) arg);
//        } else if (arg instanceof String) {
//            JOptionPane.showMessageDialog(this, arg, "Error", JOptionPane.ERROR_MESSAGE);
//            inputField.setEditable(true);
//        }
//    }
//
//    private void updateGridWithFeedback(ArrayList<String> feedback) {
//        clearCurrentAttemptGrid();
//        String gray = feedback.get(0);
//        String orange = feedback.get(1);
//        String green = feedback.get(2);
//
//        for (int i = 0; i < 7; i++) {
//            if (green.charAt(i) != '_') {
//                gridButtons[currentAttempt][i].setBackground(Color.GREEN);
//                gridButtons[currentAttempt][i].setText(String.valueOf(green.charAt(i)));
//            } else if (orange.charAt(i) != '_') {
//                gridButtons[currentAttempt][i].setBackground(Color.ORANGE);
//                gridButtons[currentAttempt][i].setText(String.valueOf(orange.charAt(i)));
//            } else if (gray.charAt(i) != '_') {
//                gridButtons[currentAttempt][i].setBackground(Color.GRAY);
//                gridButtons[currentAttempt][i].setText(String.valueOf(gray.charAt(i)));
//            }
//        }
//        currentAttempt++;
//        inputField.setEditable(false);  // Lock the input field after valid submission
//    }
//    private void clearCurrentAttemptGrid() {
//        for (JButton button : gridButtons[currentAttempt]) {
//            button.setText("");
//        }
//    }
//    public void clearInputField() {
//        inputField.setText("");  // 清空输入字段
//    }
//
////    private void clearCurrentAttemptGrid() {
////        for (int i = 0; i < 7; i++) {
////            gridButtons[currentAttempt][i].setText("");
////            gridButtons[currentAttempt][i].setBackground(null);
////        }
////    }
////    public void displayInvalidInputMessage() {
////        JOptionPane.showMessageDialog(this, "无效输入。请确保输入格式正确且逻辑有效。", "输入错误", JOptionPane.ERROR_MESSAGE);
////        inputField.setEditable(true);  // 使输入框再次可编辑
////        inputField.requestFocusInWindow();  // 将焦点返回到输入框
////    }
//    public void displayInvalidInputMessage() {
//        JOptionPane.showMessageDialog(this, "无效输入。请检查并修正您的输入。", "输入错误", JOptionPane.ERROR_MESSAGE);
//        // 注意：这里不调用 inputField.setText(""); 确保用户可以修改已输入的文本
//    }
//
//}
