package com.northgard.controller;

import com.northgard.model.Game;
import com.northgard.model.Tile;
import com.northgard.view.GameView;
import javafx.animation.AnimationTimer;

import java.awt.event.MouseEvent;

public class GameController {
    private Game model;                 //данные
    private GameView view;              //графика
    public GameController(Game model, GameView view){
        this.model = model;
        this.view = view;
        setupEventHandlers();
        startGameLoop();
    }
    private void setupEventHandlers(){
        //Обработчик кликов по клеткам
        view.setOnTileClick(this::handleTileClick);
        //Обработчик кнопок
        view.setOnHireWorker(() -> handleHireWorker());
        view.setOnHireWarrior(() -> handleHireWarrior());
        view.setOnCaptureTile(() -> handleCaptureTile());
    }
    //Обработка клика по клетке
    private int selectRow = -1;
    private int selectCol = -1;
    private void handleCaptureTileClick(MouseEvent event, int row, int col){
        this.selectRow = row;
        this.selectCol = col;
        Tile tile = model.getTile(row, col);
        String unitInfo = (tile.getUnit() == null ? "Нет юнита" : tile.getUnit().getClass().getSimpleName());
        System.out.println("Выбрана клетка (" + row + ", " + col +
                "), тип - " + tile.getType() +
                ", владелец - " + (tile.isOwned() ? "игрок" : "нейтральная") +
                ", юнит - " + unitInfo);
        //Обновление панели информации
        view.updateInfoPanel(row, col, tile, unitInfo);
        //Состояние кнопок (активны/неактивны)
        updateButtonsState();
    }
            //Обработка кнопок найма рабочего и воина
    private void handleHireWorker(){
        if (selectRow == -1 || selectCol == -1){
            System.out.println("Сначала выберите клетку!");
            return;
        }
        boolean success = model.hireWorker(selectRow,selectCol);
        if (success){
            System.out.println("Рабочий нанят!");
            view.refresh();
        } else {
            System.out.println("Рабочий не нанят! Выберите свободную захваченную клетку и проверьте наличие необходимых ресурсов!");
        }
        updateButtonsState();
    }
    private void handleHireWarrior(){
        if (selectRow == -1 || selectCol == -1){
            System.out.println("Сначала выберите клетку!");
            return;
        }
        boolean success = model.hireWarrior(selectRow,selectCol);
        if (success){
            System.out.println("Воин нанят!");
            view.refresh();
        } else {
            System.out.println("Воин не нанят! Выберите свободную захваченную клетку и проверьте наличие необходимых ресурсов!");
        }
        updateButtonsState();
    }
    //Обработка кнопки захвата территории
    private void handleCaptureTile(){
        if (selectRow == -1 || selectCol == -1){
            System.out.println("Сначала выберите клетку!");
            return;
        }
        boolean success = model.captureTile(selectRow,selectCol);
        if (success){
            System.out.println("Клетка захвачена!");
            view.refresh;
        } else {
            System.out.println("Не удалось захватить клетку! Она должна быть соседней с вашей" +
                    " территорией, и у вас должно быть минимум 50 еды и 30 древесины");
        }
        updateButtonsState();
    }
    //Управление активностью кнопок
    private void updateButtonsState(){
        if (selectRow == -1 || selectCol == -1){
            view.setButtonsEnabled(false, false, false);
            return;
        }
        Tile tile = model.getTile(selectRow,selectCol);
        boolean isOwned = tile.isOwned();
        boolean hasUnit = (tile.getUnit() != null);

        //Кнопка завхата территории активна, если она нейтральная и ее можно захватить
        boolean canCapture = !isOwned && model.canCaptureTile(selectRow,selectCol);
        //Кнопки найма рабочего/воина активны, если клетка принадлежит игроку и на ней нет юнитов
        boolean canHireWorker = isOwned && !hasUnit;
        boolean canHireWarrior = isOwned && !hasUnit;
        view.setButtonsEnabled();
    }
    //Игровой цикл
    private void startGameLoop(){
        AnimationTimer gameLoop = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                //Обновление игры примерно 60 раз в секунду
                if (now - lastUpdate >= 16_000_000){ //16 млн наносекунд = 16мс
                    model.update();     //Обновление логики игры
                    view.refresh();     //Перерисовка интерфейса
                    lastUpdate = now;
                    //Если игра закончена, цикл останавливается
                    if (model.isGameOver()){
                        view.showGameOverDialog(model.isPlayerWon());
                        this.stop();
                    }
                }
            }
        };
        gameLoop.start();
    }
}
