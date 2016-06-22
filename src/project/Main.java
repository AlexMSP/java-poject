package project;

import java.awt.*;

public class Main {

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            IModel myModel = new MyModel();
            IView myView = new MyView();

            IPresenter myPresenter = new MyPresenter();
            myPresenter.setModel(myModel);
            myPresenter.setView(myView);

            myPresenter.run();
        });
    }
}



