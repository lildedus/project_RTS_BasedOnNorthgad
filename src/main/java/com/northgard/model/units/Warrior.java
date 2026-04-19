package com.northgard.model.units;

import com.northgard.model.Game;
import com.northgard.model.Tile;

public class Warrior extends Unit{
    public Warrior(){
        super(100,20);//100 ХП и 20 ДМГ
    }
    @Override
    public void action(Game game, Tile tile){
        //Атака если есть противник
        Unit enemy = tile.getUnit();
        if (enemy != null && enemy != this){
            enemy.takeDamage(this.attack);
            System.out.println("Атака врага! У противника осталось " + enemy.getHealth() + "HP");
        }
    }
}
