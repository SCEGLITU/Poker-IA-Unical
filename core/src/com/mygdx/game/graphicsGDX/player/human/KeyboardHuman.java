package com.mygdx.game.graphicsGDX.player.human;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.mygdx.game.MyGdxGame;

public class KeyboardHuman {
    public Sprite plus = null;
    public Sprite min = null;
    public Sprite check = null;
    public Sprite raise = null;
    public Sprite fold = null;
    public Sprite call = null;

    KeyboardHuman(){
        initButton();
    }

    private void initButton() {
        plus = new Sprite(new Texture("game/Plus.png"));
        plus.setSize(70,50);
        plus.setPosition(150, MyGdxGame.WORLD_HEIGHT/2-20);

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
}
