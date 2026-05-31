package com.northgard.view;

import com.northgard.model.Game;
import com.northgard.model.Tile;
import com.northgard.model.units.Enemy;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;


public class GameView {
    //Константы
    private static final int TILE_SIZE = 80;
    private static final int MAP_SIZE = Game.MAP_SIZE;
    //Отображение выбранной для атаки клетки
    private int targetViewRow = -1;
    private int targetViewCol = -1;

    //JavaFX
    private Stage stage;
    private GridPane mapGrid;
    private Label foodLabel;
    private Label woodLabel;
    private Label infoLabel;
    private Label pauseLabel;
    private Button hireWorkerButton;
    private Button hireWarriorButton;
    private Button captureButton;
    private Button attackButton;

    //Обработчики
    private TileClickHandler tileClickHandler;
    private Runnable hireWorkerHandler;
    private Runnable hireWarriorHandler;
    private Runnable captureTileHandler;
    private javafx.event.EventHandler<javafx.scene.input.KeyEvent> keyPressedHandler;

    //Обработка нажатий клавиш
    public void setOnKeyPressed(javafx.event.EventHandler<javafx.scene.input.KeyEvent> handler) {
        this.keyPressedHandler = handler;
    }

    //Интерфейс для обработки клика по клетке
    public interface TileClickHandler {
        void onTileClick(MouseEvent event, int row, int col);
    }

    public GameView(Stage stage){
        this.stage = stage;
    }

    //Окно игры
    public void show(Game model) {
        BorderPane root = new BorderPane();
        root.setTop(createTopPanel());
        mapGrid = createMapGrid(model);
        root.setCenter(mapGrid);
        root.setRight(createRightPanel());

        // Пауза
        pauseLabel = new Label("ПАУЗА");
        pauseLabel.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        pauseLabel.setStyle("-fx-text-fill: red; -fx-background-color: rgba(0,0,0,0.7);");
        pauseLabel.setVisible(false);
        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(root, pauseLabel);
        StackPane.setAlignment(pauseLabel, javafx.geometry.Pos.CENTER);

        Scene scene = new Scene(stackPane, TILE_SIZE * MAP_SIZE + 250, TILE_SIZE * MAP_SIZE + 100);
        // Обработка нажатий клавиш
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.P) {
                // Вызываем обработчик из контроллера
                if (keyPressedHandler != null) {
                    keyPressedHandler.handle(event);
                }
            }
        });
        stage.setTitle("RTS Prototype");
        stage.setScene(scene);
        stage.show();
    }

    // Метод для отображения сообщения о паузе
    public void showPauseMessage(boolean isPaused) {
        if (pauseLabel != null) {
            pauseLabel.setVisible(isPaused);
        }
    }

    //Верхняя панель с ресурсами
    private HBox createTopPanel() {
        HBox topPanel = new HBox(20);
        topPanel.setPadding(new Insets(10));
        topPanel.setAlignment(Pos.CENTER);
        topPanel.setStyle("-fx-background-color: #2c3e50;");

        //Метка для еды
        Label foodIcon = new Label("\uD83C\uDF4E Еда:");
        foodIcon.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        foodIcon.setStyle("-fx-text-fill: white;");

        foodLabel = new Label("100");
        foodLabel.setFont(Font.font("Arial", FontWeight.BOLD,16));
        foodLabel.setStyle("-fx-text-fill: #2ecc71;");

        //Метка для древесины
        Label woodIcon = new Label("\uD83C\uDF32 Древесина:");
        woodIcon.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        woodIcon.setStyle("-fx-text-fill: white;");

        woodLabel = new Label("50");
        woodLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        woodLabel.setStyle("-fx-text-fill: #e67e22;");

        topPanel.getChildren().addAll(foodIcon, foodLabel, woodIcon, woodLabel);
         return topPanel;
    }

    private VBox createRightPanel() {
        VBox rightPanel = new VBox(15);
        rightPanel.setPadding(new Insets(15));
        rightPanel.setStyle("-fx-background-color: #34495e;");
        rightPanel.setPrefWidth(230);

        // Заголовок
        Label titleLabel = new Label("ИНФОРМАЦИЯ");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        titleLabel.setStyle("-fx-text-fill: white;");

        // Разделительная линия
        Separator separator = new Separator();
        separator.setStyle("-fx-background-color: #7f8c8d;");

        // Информация о клетке
        infoLabel = new Label("Выберите клетку");
        infoLabel.setFont(Font.font("Arial", 14));
        infoLabel.setStyle("-fx-text-fill: #ecf0f1;");
        infoLabel.setWrapText(true);

        // Разделитель
        Separator separator2 = new Separator();

        // Кнопки
        hireWorkerButton = ButtonWithCost("\uD83D\uDC77 Нанять рабочего", "20\uD83C\uDF4E / 10\uD83C\uDF32", "#27ae60");
        hireWarriorButton = ButtonWithCost("⚜ Нанять воина", "50\uD83C\uDF4E / 20\uD83C\uDF32", "#e67e22");
        captureButton = ButtonWithCost("\uD83C\uDFF0 Захватить территорию","50\uD83C\uDF4E / 30\uD83C\uDF32" , "#2980b9");
        attackButton = createStyledButton("\uD83D\uDCA5 Атака", "#e74c3c");

        // Начальное состояние кнопок (неактивны)
        hireWorkerButton.setDisable(true);
        hireWarriorButton.setDisable(true);
        captureButton.setDisable(true);
        attackButton.setDisable(true);

        rightPanel.getChildren().addAll(
                titleLabel, separator, infoLabel, separator2,
                hireWorkerButton, hireWarriorButton, captureButton, attackButton
        );

        return rightPanel;
    }

    public void setOnAttack(Runnable handler) {
        attackButton.setOnAction(e -> {
            if (handler != null) handler.run();
        });
    }

    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        button.setStyle(
                "-fx-background-color: " + color + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 5;" +
                        "-fx-padding: 8 15 8 15;"
        );
        button.setMaxWidth(Double.MAX_VALUE);
        return button;
    }

    private Button ButtonWithCost(String text, String cost, String color) {
        String fullText = text + " (" + cost + ")";
        Button button = new Button(fullText);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        button.setStyle(
                "-fx-background-color: " + color + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 5;" +
                        "-fx-padding: 8 15 8 15;" +
                        "-fx-wrap-text: true;" +
                        "-fx-text-alignment: center;"
        );
        button.setMaxWidth(Double.MAX_VALUE);
        button.setText(text + "\n" + cost);
        return button;
    }
    private GridPane createMapGrid(Game model) {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setPadding(new Insets(10));

        for (int row = 0; row < MAP_SIZE; row++) {
            for (int col = 0; col < MAP_SIZE; col++) {
                Tile tile = model.getTile(row, col);
                Rectangle rectangle = createTileRectangle(tile, row, col);

                final int r = row;
                final int c = col;
                rectangle.setOnMouseClicked(event ->{
                    if (tileClickHandler != null){
                        tileClickHandler.onTileClick(event, r, c);
                    }
                });
                grid.add(rectangle, col, row);
            }
        }
        return grid;
    }

    private Rectangle createTileRectangle (Tile tile, int row, int col){
        Rectangle rect  = new Rectangle(TILE_SIZE, TILE_SIZE);
        //Цвет клетки в зависимости от ее типа
        switch (tile.getType()){
            case PLAIN:
                rect.setFill(Color.LIGHTGREEN);
                break;
            case FOREST:
                rect.setFill(Color.DARKGREEN);
                break;
            case MOUNTAIN:
                if (tile.getUnit() instanceof Enemy && ((Enemy) tile.getUnit()).getType().equals("DRAGON")) {
                    rect.setFill(Color.DARKRED);
                } else {
                    rect.setFill(Color.GRAY);
                }
                break;
        }

        //Красная граница для клетки, выбранной для атаки
        if (row == targetViewRow && col == targetViewCol) {
            rect.setStroke(Color.RED);
            rect.setStrokeWidth(1);
        }else if (tile.isOwned()) { //Синяя граница для клеток, принадлежащих игроку
            rect.setStroke(Color.BLUE);
            rect.setStrokeWidth(1);
        } else {
            rect.setStroke(Color.BLACK);
            rect.setStrokeWidth(1);
        }

        return rect;
    }
    //Подсветка цели для атаки
    public void targetView(int row, int col) {
        this.targetViewRow = row;
        this.targetViewCol = col;
    }

    public void refresh(Game model){
        //Обновление ресурсов
        foodLabel.setText(String.valueOf(model.getFood()));
        woodLabel.setText(String.valueOf(model.getWood()));
        //Обновление карты
        for (int row = 0; row < MAP_SIZE; row++) {
            for (int col = 0; col < MAP_SIZE; col++) {
                Tile tile = model.getTile(row, col);
                Rectangle rectangle = (Rectangle)
                        getNodeFromGrid(mapGrid,col,row);
                if (rectangle != null){
                    switch (tile.getType()){
                        case PLAIN -> rectangle.setFill(Color.LIGHTGREEN);
                        case FOREST -> rectangle.setFill(Color.DARKGREEN);
                        case MOUNTAIN -> {
                            if (tile.getUnit() instanceof Enemy && ((Enemy) tile.getUnit()).
                                    getType().equals("DRAGON")) {
                                rectangle.setFill(Color.DARKRED);
                            } else {
                                rectangle.setFill(Color.GRAY);
                            }
                        }
                    }
                }
                if (row == targetViewRow && col == targetViewCol) {
                    rectangle.setStroke(Color.RED);
                    rectangle.setStrokeWidth(1);
                } else if (tile.isOwned()) {
                    rectangle.setStroke(Color.BLUE);
                    rectangle.setStrokeWidth(1);
                } else {
                    rectangle.setStroke(Color.BLACK);
                    rectangle.setStrokeWidth(1);
                }
            }
        }
    }
    private javafx.scene.Node getNodeFromGrid(GridPane grid, int col, int row){
        for (javafx.scene.Node node : grid.getChildren()){
            if (GridPane.getColumnIndex(node) == col
            && GridPane.getRowIndex(node) == row){
                return node;
            }
        }
        return null;
    }

    public void updateInfoPanel(int row, int col, Tile tile, String unitInfo) {
        StringBuilder info = new StringBuilder();
        info.append("\uD83D\uDCCC Клетка: ").append(row).append(", ").append(col).append("\n");
        info.append("\uD83C\uDF0D Тип: ").append(tile.getType()).append("\n");
        info.append("\uD83D\uDC51 Владелец: ").append(tile.isOwned() ? "Игрок" : "Нейтральная").append("\n");
        info.append("\uD83D\uDC64 Юнит: ").append(unitInfo).append("\n");

        //Информация о ресурсах клетки
        switch (tile.getType()) {
            case PLAIN:
                info.append("\uD83C\uDF3E Ресурс: +4 еды");
                break;
            case FOREST:
                info.append("\uD83C\uDF32 Ресурс: +2 древесины");
                break;
            case MOUNTAIN:
                info.append("️Ресурс: нет");
                break;
        }

        infoLabel.setText(info.toString());
    }

    public void setButtonsEnabled(boolean hireWorker, boolean hireWarrior, boolean capture, boolean attack) {
        hireWorkerButton.setDisable(!hireWorker);
        hireWarriorButton.setDisable(!hireWarrior);
        captureButton.setDisable(!capture);
        attackButton.setDisable(!attack);
    }

    //Обработчики событий
    public void setOnTileClick(TileClickHandler handler) {
        this.tileClickHandler = handler;
    }

    public void setOnHireWorker(Runnable handler) {
        this.hireWorkerHandler = handler;
        hireWorkerButton.setOnAction(e -> {
            if (hireWorkerHandler != null) {
                hireWorkerHandler.run();
            }
        });
    }

    public void setOnHireWarrior(Runnable handler) {
        this.hireWarriorHandler = handler;
        hireWarriorButton.setOnAction(e -> {
            if (hireWarriorHandler != null) {
                hireWarriorHandler.run();
            }
        });
    }

    public void setOnCaptureTile(Runnable handler) {
        this.captureTileHandler = handler;
        captureButton.setOnAction(e -> {
            if (captureTileHandler != null) {
                captureTileHandler.run();
            }
        });
    }

    //Диалог при окончании игры
    public void showGameOverDialog(boolean playerWon) {
        String title = playerWon ? "ПОБЕДА!" : "ПОРАЖЕНИЕ!";
        String message = playerWon
                ? "Поздравляем! Вы выиграли игру!\nНажмите OK для выхода."
                : "К сожалению, вы проиграли...\nНажмите OK для выхода.";

        // Задержка в  показе диалога, чтобы не блокировать игровой цикл
        javafx.application.Platform.runLater(() -> {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.INFORMATION
            );
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
            stage.close();
        });
    }
}
