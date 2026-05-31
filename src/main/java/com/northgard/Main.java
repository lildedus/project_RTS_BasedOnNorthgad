package com.northgard;

import com.northgard.controller.GameController;
import com.northgard.model.Game;
import com.northgard.view.GameView;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Модель (данные и логика игры)
        Game model = new Game();

        //Графический интерфейс
        GameView view = new GameView(primaryStage);

        // Окно игры
        view.show(model);

        // Контроллер
        GameController controller = new GameController(model, view);

    }

    public static void main(String[] args) {
        launch(args);
    }
}