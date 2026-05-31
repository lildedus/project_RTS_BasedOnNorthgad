package com.northgard.model.units;

import com.northgard.model.Game;
import com.northgard.model.Tile;

public class Worker extends Unit {
    public Worker(){
        super(100, 0); //100 ХП и 0 ДМГ
    }
    @Override
    public void action (Game game, Tile tile){
        //Зависимость рес-ов от типа клетки
        switch (tile.getType()){
            case PLAIN:
                game.addFood(4);
                break;
            case FOREST:
                game.addWood(2);
                break;
            case MOUNTAIN:
                break;//не дает рес-ов
        }
    }
}
