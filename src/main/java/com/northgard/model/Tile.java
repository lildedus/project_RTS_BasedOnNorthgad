package com.northgard.model;

import com.northgard.model.units.Unit;

public class Tile {
    //Типы местности
    public enum TileType{
        PLAIN, //равнина (еда)
        FOREST, //лес (древесина)
        MOUNTAIN //гора (без рес-ов)
    }
    private TileType type;
    private boolean isOwned; //проверка владения
    private Unit unit; //тип юнита на клетке

    // Конструктор
    public Tile(TileType type) {
        this.type = type;
        this.isOwned = false;  // изначально клетка нейтральная
        this.unit = null;      // юнитов нет
    }

    public TileType getType() {
        return type;
    }

    public boolean isOwned() {
        return isOwned;
    }

    public void setOwned(boolean owned) {
        isOwned = owned;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }
}
