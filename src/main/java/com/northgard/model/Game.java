package com.northgard.model;

import com.northgard.model.units.Unit;
import com.northgard.model.units.Warrior;
import com.northgard.model.units.Worker;

import java.util.ArrayList;
import java.util.List;

public class Game {
    public static final int MAP_SIZE = 5;
    private static final int TILE_SIZE = 60;
    //Карта и ресурсы
    private Tile[][] map;
    private int food;
    private int wood;
    //Юниты
    private List<Unit> units;
    //Завершение игры
    private boolean gameOver;
    private boolean playerWon;

    public Game(){
        map = new Tile[MAP_SIZE][MAP_SIZE];
        units = new ArrayList<>();
        food = 100;
        wood = 50;
        gameOver = false;
        playerWon = false;
        initMap();
        initStartingUnits();
    }

                                            // Инициализация игры
    private void initMap(){
        //Заполнение карты клетками разных типов
        for (int row = 0; row < MAP_SIZE; row++) {
            for (int col = 0; col < MAP_SIZE; col++) {
                if (row == 2 && col == 2){
                    // Центральная клетка - гора
                    map[row][col] = new Tile(Tile.TileType.MOUNTAIN);
                } else if ((row + col % 3 == 0)){
                    // Каждая третья - лес
                    map[row][col] = new Tile(Tile.TileType.FOREST);
                } else {
                    // Остальные - равнины
                    map[row][col] = new Tile(Tile.TileType.PLAIN);
                }
            }
            
        }
        map[0][0].setOwned(true); // Стартовая клетка игрока
    }
    private void initStartingUnits(){
        // Создание рабочих юнитов
        Worker worker1 = new Worker();
        //Добавление в список
        units.add(worker1);
        map[0][0].setUnit(worker1);
    }
    public int getFood() {
        return food;
    }
    public int getWood() {
        return wood;
    }
    public boolean isGameOver() {
        return gameOver;
    }
    public boolean isPlayerWon() {
        return playerWon;
    }

    public List<Unit> getUnits() {
        return units;
    }

    public Tile getTile(int row, int col) {
        return map[row][col];
    }

                                    //Методы добычи

    private void addFood(int amount){
        this.food += amount;
        System.out.println("Добавлено" + amount + " еды. Всего: " + food);
    }
    private void addWood(int amount){
        this.wood += amount;
        System.out.println("Добавлено" + amount + " древесины. Всего: " + wood);
    }
                //Хватает ли ресурсов для их траты и вычитание от общего кол-ва
    public boolean spendResources(int foodCost, int woodCost){
        if (wood >= woodCost && food >= foodCost){
            wood -= woodCost;
            food -= foodCost;
            return true;
        }
        return false;
    }

                                    //Логика захвата клетки
    public boolean canCaptureTile(int row, int col){
        //Нельзя захватить уже захваченную клетку
        if (map[row][col].isOwned()){
            return false;
        }
        //Проверка соседних клеток
        int [][] directions = {{-1,0},{1,0},{0,-1},{0,1}};
        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            //Сохранение границ карты
            if (newRow >= 0 && newCol >= 0 && newRow < MAP_SIZE && newCol < MAP_SIZE){
                if (map[newRow][newCol].isOwned()){
                    return true; //Найдена соседняя захваченная клетка
                }
            }
        }
        return false; //Нет соседних захваченных клеток
    }
    //Ошибки при захвате
    public boolean captureTile(int row, int col){
        if (!canCaptureTile(row,col)){
            System.out.println("Нельзя захватить эту клетку!");
        }
        if (spendResources(50, 30)){
            map[row][col].setOwned(true);
            System.out.println("Клетка (" + row + ", " + col + ") захвачена!");
            return true;
        } else {
            System.out.println("Не хватает ресурсов для захвата!");
            return false;
        }
    }
                                    //Найм юнитов
    //Найм рабочего
    public boolean hireWorker(int row, int col) {
        //Захвачена и свободна ли клетка
        if (!map[row][col].isOwned()) {
            System.out.println("Клетка не захвачена!");
            return false;
        }
        if (map[row][col].getUnit() != null) {
            System.out.println("На клетке уже есть юнит!");
            return false;
        }
        //Проверка на достаточное кол-во ресурсов
        if (spendResources(20, 10)) {
            Worker worker = new Worker();
            units.add(worker);
            map[row][col].setUnit(worker);
            System.out.println("Рабочий нанят на клетку (" + row + ", " + col + ")");
            return true;
        } else {
            System.out.println("Не хватает ресурсов для найма рабочего!");
            return false;
        }
    }
    //Найм воина
    public boolean hireWarrior(int row, int col) {
        //Захвачена и свободна ли клетка
        if (!map[row][col].isOwned()) {
            System.out.println("Клетка не захвачена!");
            return false;
        }
        if (map[row][col].getUnit() != null) {
            System.out.println("На клетке уже есть юнит!");
            return false;
        }
        //Проверка на достаточное кол-во ресурсов
        if (spendResources(50, 20)) {
            Warrior warrior = new Warrior();
            units.add(warrior);
            map[row][col].setUnit(warrior);
            System.out.println("Рабочий нанят на клетку (" + row + ", " + col + ")");
            return true;
        } else {
            System.out.println("Не хватает ресурсов для найма воина!");
            return false;
        }
    }
                                    //Обновление состояния игры
    public void update(){
        if (gameOver) return;

        //Юниты выполняют действия
        for (Unit unit : units){
            Tile unitTile = findTileByUnit(unit);
            if (unitTile != null){
                unit.action(this,unitTile);
            }
        }
        //Потребление еды
        int foodConsumption = units.size();
        if (food >= foodConsumption){
            food -= foodConsumption;
        } else {
            //Голод
            System.out.println("Нехватка еды, люди голодают!");
            for (Unit unit : units){
                unit.takeDamage(10);
            }
        }
        //Удаление мертвых юнитов
        removeDeadUnits();
        //Проверка условий победы/поражения
        checkWinConditions();
        checkLossConditions();
    }
    //Поиск клетки по юниту
    private Tile findTileByUnit(Unit targetUnit){
        for (int row = 0; row < MAP_SIZE; row++){
            for (int col = 0; col < MAP_SIZE; col++) {
                if (map[row][col].getUnit() == targetUnit){
                    return map[row][col];
                }
            }
        }
        return null;
    }
    public void removeDeadUnits (){
        List<Unit> toRemove = new ArrayList<>();
        for (Unit unit : units){
            if (!unit.isAlive()){
                //Удаляем юнита с найденной клетки
                Tile tile = findTileByUnit(unit);
                if (tile != null){
                    tile.setUnit(null);
                }
                toRemove.add(unit);
            }
        }
        units.removeAll(toRemove);
        if (toRemove.size() > 0){
            System.out.println("Удалено мёртвых юнитов - " + toRemove.size());
        }
    }
                                    //Проверка победы/поражения
    private void checkWinConditions(){
        //Захват центральной клетки
        Tile centerTile = map[2][2];
        if (centerTile.isOwned()){
            gameOver = true;
            playerWon = true;
            System.out.println("ПОБЕДА! Центральная клетка захвачена!");
            return;
        }
        //Экономическая победа (500 еды и 500 древесины)
        if (food >= 500 && wood >= 500){
            gameOver = true;
            playerWon = true;
            System.out.println("ПОБЕДА! Накоплено 500 единиц ресурсов двух типов!");
        }
    }
    private void checkLossConditions(){
        if (units.isEmpty()){
            gameOver = true;
            playerWon = false;
            System.out.println("ПОРАЖЕНИЕ! Все юниты мертвы.");
        }
    }
}
