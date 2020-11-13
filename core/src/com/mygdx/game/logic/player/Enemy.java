package com.mygdx.game.logic.player;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.mygdx.game.graphicsGDX.card.Deck;
import com.mygdx.game.graphicsGDX.player.PlayerDirection;
import com.mygdx.game.logic.LogicGame;
import com.mygdx.game.logic.card.Card;

import java.util.Random;

public class Enemy extends Player {

    private Random random;

    public Enemy(String name, int money) {
        super(name, money);
        random = new Random();
    }

    @Override
    public void move(int currentValue) {
        if (currentValue < 100){
            raise(100, currentValue);
        } else
            check(currentValue);
    }

    @Override
    public void removeCard() {
        int ncards = random.nextInt(cards.size());
        for(int i=0; i<ncards; i++){
            rmv.add(cards.get(i));
        }
        changeCards();
    }
}
