package com.northgard.model.units;

import com.northgard.model.Game;
import com.northgard.model.Tile;

public class Warrior extends Unit {
    public Warrior() {
        super(60, 20);//60 ХП и 20 ДМГ
    }

    @Override
    public void action(Game game, Tile tile) {
        Unit potentialEnemy = tile.getUnit();
        if (potentialEnemy != null && potentialEnemy != this && potentialEnemy instanceof Enemy) {
            Enemy enemy = (Enemy) potentialEnemy;
            enemy.takeDamage(this.attack);
            System.out.println("Warrior attacks " + enemy.getDisplayName() + " on same tile!");
            System.out.println("Enemy HP left: " + enemy.getHealth());
            System.out.println("Warrior HP left: " + this.getHealth());

            if (!enemy.isAlive()) {
                tile.setUnit(null);
                System.out.println(enemy.getDisplayName() + " defeated!");
            }
        }
    }
}
