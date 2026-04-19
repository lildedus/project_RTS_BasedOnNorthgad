package com.northgard.model.units;

import com.northgard.model.Game;
import com.northgard.model.Tile;

public abstract class Unit {
    protected int health;
    protected int attack;

    public Unit(int health, int attack){
        this.health = health;
        this.attack = attack;
    }
    public int getHealth(){
        return health;
    }
    public void takeDamage(int damage){
        this.health -= damage;
        if (this.health < 0) this.health = 0;
    }
    public boolean isAlive(){
        return health > 0;
    }
    public int getAttack(){
        return attack;
    }
    public abstract void action(Game game, Tile tile);
}
