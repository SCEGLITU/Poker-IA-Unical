package com.mygdx.game;

import it.unical.mat.embasp.languages.Id;
import it.unical.mat.embasp.languages.Param;

@Id("card")
public class Card {

    @Param(0)
    protected Suite suite;

    @Param(1)
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
