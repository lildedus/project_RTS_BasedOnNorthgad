package com.northgard.model.units;

import com.northgard.model.Game;
import com.northgard.model.Tile;

public class Enemy extends Unit {
    private String type; // "WOLF", "BEAR" или "DRAGON"

    public Enemy(String type) {
        super(getHealthByType(type), getAttackByType(type));
        this.type = type;
    }
    private static int getHealthByType(String type) {
        switch (type) {
            case "WOLF": return 50;     //у волка 50ХП
            case "BEAR": return 100;    //у медведя 100ХП
            case "DRAGON": return 150;  //у дракона 150ХП
            default: return 50;
        }
    }
    private static int getAttackByType(String type) {
        switch (type) {
            case "WOLF": return 15;     //у волка 15 ДМГ
            case "BEAR": return 30;     //у медведя 30 ДМГ
            case "DRAGON": return 50;   //у дракона 50 ДМГ
            default: return 15;
        }
    }

    @Override
    public void action(Game game, Tile tile) {
        // Враг атакует, если на его клетке есть юнит игрока
        Unit playerUnit = tile.getUnit();
        if (playerUnit != null && playerUnit != this && (playerUnit instanceof Worker || playerUnit instanceof Warrior)) {
            playerUnit.takeDamage(this.attack);
            System.out.println(type + " attacks! Unit has " + playerUnit.getHealth() + " HP left");
            System.out.println("Player unit HP left: " + playerUnit.getHealth());
            System.out.println("Enemy HP left: " + this.getHealth());

            // Если юнит умер, удаляем его
            if (!playerUnit.isAlive()) {
                tile.setUnit(null);
                System.out.println("Player unit died!");
            }
        }
    }

    public String getType() {
        return type;
    }

    public String getDisplayName() {
        switch (type) {
            case "WOLF": return "Волк";
            case "BEAR": return "Медведь";
            case "DRAGON": return "Дракон";
            default: return "Враг";
        }
    }
}