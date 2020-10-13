package com.mygdx.game.logic.player;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.mygdx.game.graphicsGDX.card.Deck;
import com.mygdx.game.graphicsGDX.player.PlayerDirection;
import com.mygdx.game.logic.card.Card;

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
            Deck.getInstance().getCard(card).draw(batch);
    }

    @Override
    public void drawKeybord(Batch batch) {

    }
}
