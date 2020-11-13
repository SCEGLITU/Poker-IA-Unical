package com.mygdx.game.logic;

public class Plate {
    protected int cash = 0;

    public void clear(){
        cash = 0;
    }

    public void increase(int sum){
        cash += sum;
    }

    public int getCash() {
        return cash;
    }

    @Override
    public String toString() {
        return "Plate=" + cash;
    }
}
