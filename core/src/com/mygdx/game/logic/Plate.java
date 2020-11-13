package com.mygdx.game.logic;

public class Plate {
    protected int cash = 0;
    protected int currentValue = 0;

    public void clear(){
        cash = 0;
        currentValue = 0;
    }

    public void increase(int sum){
        cash += sum;
    }

    public int getCash() {
        return cash;
    }

    public int getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(int currentValue) {
        this.currentValue = currentValue;
    }

    @Override
    public String toString() {
        return "Plate=" + cash + " quote= " + currentValue;
    }
}
