
public class GUIApp {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                NumberleModel model = new NumberleModel();
//                System.out.println("1");
//                NumberleView view = new NumberleView(model);
                NumberleView view = new NumberleView(model);
                view.setVisible(true);
                NumberleController controller = new NumberleController(model, view);
                view.setController(controller);
            }
        });
    }
}
