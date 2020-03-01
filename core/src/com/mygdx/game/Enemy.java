package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.Batch;

public class Enemy extends Player {
    public Enemy(PlayerDirection direction) {
        super(direction);
    }

    public Enemy(String name, int money) {
        super(name, money);
    }

    @Override
    public void drawCards(Batch batch) {
        for(Card card:cards)
            Deck.getIstance().getAsDarkCard(card).draw(batch);
    }
}
