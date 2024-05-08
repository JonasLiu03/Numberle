import java.util.Stack;
import java.util.regex.Pattern;
import java.util.ArrayList;

public class NumberleController {
    private NumberleModel model;
    private NumberleView view;

    public NumberleController(NumberleModel model, NumberleView view) {
        this.model = model;
        this.view = view;

        // 确保视图订阅模型的变化，视图需要实现 Observer 接口
        this.model.addObserver(view);
    }
    public void setGameWin(boolean won){
        model.setGameWon(won);
    }

    public void processUserInput(String input) {
        if (model.validateInput(input)) {
            model.makeGuess(input);
            System.out.println("Input is valid and processed");
            view.clearInputField();  // Clear the field only if valid
        } else {
            System.out.println("Invalid input, error displayed");
//            System.out.println("Call");
//            view.displayInvalidInputMessage();  // Display error without clearing the input
        }
    }
    public void resetGame() {
        model.reset();  // Reset the model, assuming there's a method to reset all necessary attributes
        view.resetDisplay();  // Reset the view, e.g., clear all inputs and results

    }
    public NumberleModel getModel() {
        return model;
    }
    public boolean isGameWon() {
        return model.isGameWon();
    }
}

