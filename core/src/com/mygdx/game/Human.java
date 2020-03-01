package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.Batch;

public class Human extends Player {

    // you can have more human player, so when there isn't its shitf the card must be dark.
    public boolean shift = true;



    public Human(PlayerDirection direction) {
        super(direction);
    }

    public Human(String name, int money) {
        super(name, money);
    }

    @Override
    public void drawCards(Batch batch) {
        for(Card card: cards){
            if(isShift())
                Deck.getIstance().getCard(card).draw(batch);
            else
                Deck.getIstance().getAsDarkCard(card).draw(batch);
        }
    }

    public boolean isShift() {
        return shift;
    }

    public void setShift(boolean shift) {
        this.shift = shift;
    }
}
