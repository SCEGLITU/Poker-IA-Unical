package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;

import java.util.ArrayList;

public class Human extends Player {

    protected Sprite plus = null;
    protected Sprite min = null;
    protected Sprite check = null;
    protected Sprite raise = null;
    protected Sprite fold = null;
    protected Sprite call = null;
    // you can have more human player, so when there isn't its shitf the card must be dark.
    public boolean shift = true;

    {
        initButton();
    }


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

    @Override
    public void drawKeybord(Batch batch) {
        if(Game.currentValue==0)
            check.draw(batch);
        else
            call.draw(batch);
        fold.draw(batch);
        min.draw(batch);
        raise.draw(batch);
        plus.draw(batch);
    }

    public boolean isShift() {
        return shift;
    }

    private void initButton() {
        plus = new Sprite(new Texture("game/Plus.png"));
        plus.setSize(70,50);
        plus.setPosition(150,MyGdxGame.WORLD_HEIGHT/2-20);

        min = new Sprite(new Texture("game/Min.png"));
        min.setSize(70,50);
        min.setPosition(300,MyGdxGame.WORLD_HEIGHT/2-30);

        check = new Sprite(new Texture("game/Check.png"));
        check.setSize(150,80);
        check.setPosition(450,MyGdxGame.WORLD_HEIGHT/2-30);

        call = new Sprite(new Texture("game/Call.png"));
        call.setSize(150,80);
        call.setPosition(450,MyGdxGame.WORLD_HEIGHT/2-30);

        fold = new Sprite(new Texture("game/Fold.png"));
        fold.setSize(150,80);
        fold.setPosition(600,MyGdxGame.WORLD_HEIGHT/2-30);

        raise = new Sprite(new Texture("game/Raise.png"));
        raise.setSize(150,80);
        raise.setPosition(750,MyGdxGame.WORLD_HEIGHT/2-30);
    }

    public void setShift(boolean shift) {
        this.shift = shift;
    }
}
