package com.northgard.model;

import com.northgard.model.units.Unit;
import com.northgard.model.units.Warrior;
import com.northgard.model.units.Worker;
import com.northgard.model.units.Enemy;

import java.util.ArrayList;
import java.util.List;

public class Game {
    public static final int MAP_SIZE = 5;
    private static final int TILE_SIZE = 60;
    //Карта и ресурсы
    private Tile[][] map;
    private double food;
    private int wood;
    //Юниты
    private List<Unit> playerUnits;   //рабочие и воины
    private List<Enemy> enemies;      //враги
    //Завершение игры
    private boolean gameOver;
    private boolean playerWon;

    public Game(){
        map = new Tile[MAP_SIZE][MAP_SIZE];
        playerUnits = new ArrayList<>();
        enemies = new ArrayList<>();
        food = 100;
        wood = 50;
        gameOver = false;
        playerWon = false;
        initMap();
        initStartingUnits();
    }

                                            // Инициализация игры
private void initMap() {
    for (int row = 0; row < MAP_SIZE; row++) {
        for (int col = 0; col < MAP_SIZE; col++) {
            if (row == 2 && col == 2) {
                map[row][col] = new Tile(Tile.TileType.MOUNTAIN);
            } else if ((row + col) % 3 == 0) {
                map[row][col] = new Tile(Tile.TileType.FOREST);
            } else {
                map[row][col] = new Tile(Tile.TileType.PLAIN);
            }
        }
    }
    map[0][0].setOwned(true);

    // Добавляем врагов на все клетки, кроме стартовой, центральной и соседних со стартовой
    addEnemies();
}

    private void addEnemies() {
        for (int row = 0; row < MAP_SIZE; row++) {
            for (int col = 0; col < MAP_SIZE; col++) {
                // Пропускаем стартовую клетку
                if (row == 0 && col == 0) continue;

                // Пропускаем соседние клетки от стартовой
                if (isAdjacentToStart(row, col)) continue;

                // Центральная клетка (2,2) — ДРАКОН
                if (row == 2 && col == 2) {
                    Enemy dragon = new Enemy("DRAGON");
                    enemies.add(dragon);
                    map[row][col].setUnit(dragon);
                    continue;
                }

                Tile tile = map[row][col];
                if (tile.getUnit() == null) {
                    if (tile.getType() == Tile.TileType.PLAIN) {
                        Enemy wolf = new Enemy("WOLF");
                        enemies.add(wolf);
                        tile.setUnit(wolf);
                    } else if (tile.getType() == Tile.TileType.FOREST) {
                        Enemy bear = new Enemy("BEAR");
                        enemies.add(bear);
                        tile.setUnit(bear);
                    }
                }
            }
        }
    }

    // Проверяет, является ли клетка соседней со стартовой (0,0)
    private boolean isAdjacentToStart(int row, int col) {
        // Соседние клетки: (0,1), (1,0), (1,1)
        return (row == 0 && col == 1) ||
                (row == 1 && col == 0) ||
                (row == 1 && col == 1);
    }

    private void initStartingUnits(){
        // Создание рабочих юнитов
        Worker worker1 = new Worker();
        //Добавление в список
        playerUnits.add(worker1);
        map[0][0].setUnit(worker1);
    }
    public double getFood() {
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

    public List<Unit> getPlayerUnits() {
        return playerUnits;
    }

    public List<Enemy> getEnemies(){
        return enemies;
    }

    public Tile getTile(int row, int col) {
        return map[row][col];
    }

                                    //Методы добычи

    public void addFood(double amount){
        this.food += amount;
        //System.out.println("Added " + amount + " food. Total: " + food);
    }
    public void addWood(int amount){
        this.wood += amount;
        //System.out.println("Added " + amount + " wood. Total: " + wood);
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

        // Нельзя захватить клетку с врагом
        if (map[row][col].getUnit() instanceof Enemy){
            System.out.println("Cannot capture tile with enemy! Defeat the enemy first!");
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
            System.out.println("Cannot capture this tile!");
        }
        if (spendResources(50, 30)){
            map[row][col].setOwned(true);
            System.out.println("Tile (" + row + ", " + col + ") captured!");
            return true;
        } else {
            System.out.println("Not enough resources to capture!");
            return false;
        }
    }
                                    //Найм юнитов
    //Найм рабочего
    public boolean hireWorker(int row, int col) {
        //Захвачена и свободна ли клетка
        if (!map[row][col].isOwned()) {
            System.out.println("Tile is not owned!");
            return false;
        }
        if (map[row][col].getUnit() != null) {
            System.out.println("Tile already has a unit!");
            return false;
        }
        //Проверка на достаточное кол-во ресурсов
        if (spendResources(20, 10)) {
            Worker worker = new Worker();
            playerUnits.add(worker);
            map[row][col].setUnit(worker);
            System.out.println("Worker hired on tile (" + row + ", " + col + ")");
            return true;
        } else {
            System.out.println("Not enough resources to hire a worker!");
            return false;
        }
    }
    //Найм воина
    public boolean hireWarrior(int row, int col) {
        //Захвачена и свободна ли клетка
        if (!map[row][col].isOwned()) {
            System.out.println("Tile is not owned!");
            return false;
        }
        if (map[row][col].getUnit() != null) {
            System.out.println("Tile already has a unit!");
            return false;
        }
        //Проверка на достаточное кол-во ресурсов
        if (spendResources(50, 20)) {
            Warrior warrior = new Warrior();
            playerUnits.add(warrior);
            map[row][col].setUnit(warrior);
            System.out.println("Warrior hired on tile (" + row + ", " + col + ")");
            return true;
        } else {
            System.out.println("Not enough resources to hire a warrior!");
            return false;
        }
    }
                                    //Обновление состояния игры
    public void update(){
        if (gameOver) return;

        //Юниты выполняют действия
        for (Unit unit : playerUnits){
            Tile unitTile = findTileByUnit(unit);
            if (unitTile != null){
                unit.action(this,unitTile);
            }
        }
        // Потребление еды: 1.5 на каждого юнита игрока
        double foodConsumption = playerUnits.size() * 1.5;
        if (food >= foodConsumption){
            food -= foodConsumption;
        } else {
            //Голод
            System.out.println("Food shortage! Units are starving.");
            for (Unit unit : playerUnits){
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
        for (Unit unit : playerUnits){
            if (!unit.isAlive()){
                //Удаляем юнита с найденной клетки
                Tile tile = findTileByUnit(unit);
                if (tile != null){
                    tile.setUnit(null);
                }
                toRemove.add(unit);
            }
        }
        for (Unit unit : enemies){
            if (!unit.isAlive()){
                //Удаляем юнита с найденной клетки
                Tile tile = findTileByUnit(unit);
                if (tile != null){
                    tile.setUnit(null);
                }
                toRemove.add(unit);
            }
        }
        playerUnits.removeAll(toRemove);
        enemies.removeAll(toRemove);
        if (toRemove.size() > 0){
            System.out.println("Removed dead units: " + toRemove.size());
        }
    }

    // Атака врага на соседней клетке
    public boolean attackNeighbor(int attackerRow, int attackerCol, int targetRow, int targetCol) {
        // Проверяем, что клетки соседние (по горизонтали или вертикали)
        boolean isAdjacent = (Math.abs(attackerRow - targetRow) + Math.abs(attackerCol - targetCol)) == 1;
        if (!isAdjacent) {
            System.out.println("Target is not adjacent!");
            return false;
        }

        // Проверяем, что на клетке атакующего есть воин
        Unit attacker = map[attackerRow][attackerCol].getUnit();
        if (!(attacker instanceof Warrior)) {
            System.out.println("No warrior on attacking tile!");
            return false;
        }

        // Проверяем, что на целевой клетке есть враг
        Unit target = map[targetRow][targetCol].getUnit();
        if (!(target instanceof Enemy)) {
            System.out.println("Target is not an enemy!");
            return false;
        }

        // Атака
        target.takeDamage(attacker.getAttack());
        System.out.println("Warrior attacks " + ((Enemy) target).getDisplayName() + "!");
        System.out.println("Enemy HP left: " + target.getHealth());
        System.out.println("Warrior HP left: " + attacker.getHealth());

        // КОНТРАТАКА ВРАГА (если враг ещё жив)
        if (target.isAlive()) {
            attacker.takeDamage(target.getAttack());
            System.out.println(((Enemy) target).getDisplayName() + " counterattacks!");
            System.out.println("Warrior HP left: " + attacker.getHealth());
            System.out.println("Enemy HP left: " + target.getHealth());
        }

        // Если враг умер, удаляем его
        if (!target.isAlive()) {
            map[targetRow][targetCol].setUnit(null);
            System.out.println(((Enemy) target).getDisplayName() + " defeated!");
            return true;
        }
        return false;
    }
                                    //Проверка победы/поражения
    private void checkWinConditions(){
        //Захват центральной клетки
        Tile centerTile = map[2][2];
        if (centerTile.isOwned() && centerTile.getUnit() == null){
            gameOver = true;
            playerWon = true;
            System.out.println("VICTORY! Dragon defeated and central tile captured!");
            return;
        }
        //Экономическая победа (500 еды и 500 древесины)
        if (food >= 500 && wood >= 500){
            gameOver = true;
            playerWon = true;
            System.out.println("VICTORY! Accumulated 500 of each resource!");
        }
    }
    private void checkLossConditions(){
        if (playerUnits.isEmpty()){
            gameOver = true;
            playerWon = false;
            System.out.println("GAME OVER! All units are dead.");
        }
    }
}
