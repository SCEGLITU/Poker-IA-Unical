package com.mygdx.game.logic.player;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.mygdx.game.graphicsGDX.card.Deck;
import com.mygdx.game.graphicsGDX.player.PlayerDirection;
import com.mygdx.game.logic.LogicGame;
import com.mygdx.game.logic.card.Card;

import java.util.Random;

public class Enemy extends Player {

    private Random random;

    public Enemy(String name, int money, OnActionListerner listerner) {
        super(name, money, listerner);
        random = new Random();
    }

    @Override
    public void moveChoice(int currentPlayerValue, int currentValue) {
        if (random.nextInt(5) == 0) {
            fold();
        } else {
            raise(random.nextInt(money));
        }
    }

    @Override
    public void removeCardChoice(int currentPlayerValue, int currentValue) {
        int ncards = random.nextInt(cards.size());

    }
}
