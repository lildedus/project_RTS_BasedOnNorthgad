package com.northgard.controller;

import com.northgard.model.Game;
import com.northgard.model.Tile;
import com.northgard.model.units.Enemy;
import com.northgard.model.units.Warrior;
import com.northgard.view.GameView;
import javafx.animation.AnimationTimer;
import javafx.scene.input.MouseEvent;

public class GameController {
    private Game model;                 //данные
    private GameView view;              //графика
    private boolean isPaused = false;   //пауза
    //цель атаки
    private int targetRow = -1;
    private int targetCol = -1;

    public GameController(Game model, GameView view) {
        this.model = model;
        this.view = view;
        setupEventHandlers();
        startGameLoop();
    }

    //Обработка клика по клетке
    private int selectRow = -1;
    private int selectCol = -1;

    private void handleTileClick(MouseEvent event, int row, int col) {
        Tile tile = model.getTile(row, col);

        // Если клик по врагу
        if (tile.getUnit() instanceof Enemy) {
            // Проверяем, есть ли воин на ранее выбранной клетке (selectRow, selectCol)
            if (selectRow != -1 && selectCol != -1) {
                Tile attackerTile = model.getTile(selectRow, selectCol);
                if (attackerTile.getUnit() instanceof Warrior) {
                    this.targetRow = row;
                    this.targetCol = col;
                    view.targetView(row, col);
                    System.out.println("Enemy selected as target! Press Attack button.");
                } else {
                    System.out.println("Selected tile has no warrior! Select a warrior first.");
                }
            } else {
                System.out.println("Select a warrior tile first, then click on enemy!");
            }
        }
        // Если клик по клетке с воином
        else if (tile.getUnit() instanceof Warrior) {
            this.selectRow = row;
            this.selectCol = col;
            System.out.println("Warrior selected on tile (" + row + ", " + col + ")");
        }
        // Если клик по обычной клетке (не враг и не воин)
        else {
            this.selectRow = row;
            this.selectCol = col;
            // Сбрасываем цель
            this.targetRow = -1;
            this.targetCol = -1;
            view.targetView(-1, -1);
        }

        // Обновление информации и кнопок
        String unitInfo = getUnitInfo(tile);
        view.updateInfoPanel(row, col, tile, unitInfo);
        updateButtonsState();
    }

    // Вспомогательный метод для получения информации о юните
    private String getUnitInfo(Tile tile) {
        if (tile.getUnit() == null) return "Has no units here!";
        if (tile.getUnit() instanceof Enemy) {
            return ((Enemy) tile.getUnit()).getDisplayName();
        }
        return tile.getUnit().getClass().getSimpleName();
    }
    //Обработка кнопок найма рабочего и воина
    private void handleHireWorker() {
        if (selectRow == -1 || selectCol == -1) {
            System.out.println("Select a tile first!");
            return;
        }
        boolean success = model.hireWorker(selectRow, selectCol);
        if (success) {
            System.out.println("Worker hired!");
            view.refresh(model);
        } else {
            System.out.println("Failed to hire worker! Select an empty owned tile and check resources!");
        }
        updateButtonsState();
    }

    private void handleHireWarrior() {
        if (selectRow == -1 || selectCol == -1) {
            System.out.println("Select a tile first!");
            return;
        }
        boolean success = model.hireWarrior(selectRow, selectCol);
        if (success) {
            System.out.println("Warrior hired!");
            view.refresh(model);
        } else {
            System.out.println("Failed to hire warrior! Select an empty owned tile and check resources!");
        }
        updateButtonsState();
    }

    //Обработка кнопки захвата территории
    private void handleCaptureTile() {
        if (selectRow == -1 || selectCol == -1) {
            System.out.println("Select a tile first!");
            return;
        }
        boolean success = model.captureTile(selectRow, selectCol);
        if (success) {
            System.out.println("Tile captured!");
            view.refresh(model);
        } else {
            System.out.println("Failed to capture tile! It must be adjacent to your territory, and you need 50 food and 30 wood.");
        }
        updateButtonsState();
    }

    //Управление активностью кнопок
    private void updateButtonsState() {
        if (selectRow == -1 || selectCol == -1) {
            view.setButtonsEnabled(false, false, false, false);
            return;
        }
        Tile tile = model.getTile(selectRow, selectCol);
        boolean isOwned = tile.isOwned();
        boolean hasUnit = (tile.getUnit() != null);

        //Кнопка захвата территории активна, если она нейтральная и ее можно захватить
        boolean canCapture = !isOwned && model.canCaptureTile(selectRow, selectCol);

        //Кнопки найма рабочего/воина активны, если клетка принадлежит игроку и на ней нет юнитов
        boolean canHireWorker = isOwned && !hasUnit;
        boolean canHireWarrior = isOwned && !hasUnit;

        // Кнопка атаки активна, если есть воин на выбранной клетке и выбран враг
        boolean canAttack = false;
        if (tile.getUnit() instanceof Warrior && targetRow != -1 && targetCol != -1) {
            // Проверяем соседство
            boolean isAdjacent = (Math.abs(selectRow - targetRow) + Math.abs(selectCol - targetCol)) == 1;
            canAttack = isAdjacent;
            if (!canAttack) {
                System.out.println("Target is not adjacent to warrior!");
            }
        }
        //Обновление состояния кнопок
        view.setButtonsEnabled(canHireWorker, canHireWarrior, canCapture, canAttack);
    }
    //Атака воина на соседнюю клетку
    private void handleAttack() {
        if (selectRow == -1 || selectCol == -1) {
            System.out.println("Select attacker tile first!");
            return;
        }
        if (targetRow == -1 || targetCol == -1) {
            System.out.println("Select target tile with enemy!");
            return;
        }

        boolean success = model.attackNeighbor(selectRow, selectCol, targetRow, targetCol);
        if (success) {
            view.refresh(model);
        }
        // Сбрасываем выбранную цель
        targetRow = -1;
        targetCol = -1;
        view.targetView(-1, -1);
        updateButtonsState();
    }

    private void setupEventHandlers() {
        //Обработчик кликов по клеткам
        view.setOnTileClick(this::handleTileClick);
        //Обработчик кнопок
        view.setOnHireWorker(() -> handleHireWorker());
        view.setOnHireWarrior(() -> handleHireWarrior());
        view.setOnCaptureTile(() -> handleCaptureTile());
        view.setOnAttack(() -> handleAttack());

        // Обработчик клавиши P для паузы
        view.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.P) {
                togglePause();
            }
        });
    }
    //Информация о паузе
    private void togglePause() {
        isPaused = !isPaused;
        view.showPauseMessage(isPaused);
        System.out.println(isPaused ? "Game PAUSED. Press P to resume." : "Game RESUMED.");
    }

    // Экономика не должна обновляться на паузе
    private void startGameLoop() {
        AnimationTimer gameLoop = new AnimationTimer() {
            private long lastUpdate = 0;
            private long lastEconomyUpdate = 0;

            @Override
            public void handle(long now) {
                if (now - lastUpdate >= 16_000_000) {
                    view.refresh(model);
                    lastUpdate = now;
                }

                // Экономика обновляется раз в секунду, кроме паузы
                if (!isPaused && now - lastEconomyUpdate >= 1_000_000_000) {
                    model.update();
                    lastEconomyUpdate = now;

                    if (model.isGameOver()) {
                        view.showGameOverDialog(model.isPlayerWon());
                        this.stop();
                    }
                }
            }
        };
        gameLoop.start();
    }
}

