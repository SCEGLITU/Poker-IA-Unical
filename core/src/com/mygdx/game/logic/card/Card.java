package com.mygdx.game.logic.card;

import it.unical.mat.embasp.languages.Id;
import it.unical.mat.embasp.languages.Param;

@Id("card")
public class Card {

    @Param(0)
    public Suite suite;

    @Param(1)
    public int number;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return number == card.number &&
                suite == card.suite;
    }

    @Override
    public String toString() {
        return "Card{" +
                "suite=" + suite +
                ", number=" + number +
                '}';
    }
}
