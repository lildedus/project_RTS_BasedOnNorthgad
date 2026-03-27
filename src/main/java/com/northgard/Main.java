package com.northgard;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class Main extends Application {

    private static final int TILE_SIZE = 60;
    private static final int MAP_SIZE = 5;

    @Override
    public void start(Stage primaryStage) {
        GridPane mapGrid = new GridPane();

        for (int row = 0; row < MAP_SIZE; row++) {
            for (int col = 0; col < MAP_SIZE; col++) {
                Rectangle tile = new Rectangle(TILE_SIZE, TILE_SIZE);
                tile.setFill(Color.LIGHTGREEN);
                tile.setStroke(Color.BLACK);

                final int r = row;
                final int c = col;
                tile.setOnMouseClicked(e -> {
                    System.out.println("Tile Y,X: " + r + ", " + c);
                });
                mapGrid.add(tile, col, row);
            }
        }

        Scene scene = new Scene(mapGrid, TILE_SIZE * MAP_SIZE, TILE_SIZE * MAP_SIZE);
        primaryStage.setTitle("Northgard Prototype");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}