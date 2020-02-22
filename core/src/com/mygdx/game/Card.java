package com.mygdx.game;

public class Card {
    protected Suite suite;
    protected int number;

    public Card() {
    }

    public Card(Suite suite, int number) {
        this.suite = suite;
        this.number = number;
    }


    public Suite getSuite() {
        return suite;
    }

    public void setSuite(Suite suite) {
        this.suite = suite;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
