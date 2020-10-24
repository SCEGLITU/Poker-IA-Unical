package com.mygdx.game.graphicsGDX.player.human;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.mygdx.game.MyGdxGame;

public class KeyboardHuman {
    public static int PLUS  = 0;
    public static int MIN   = 1;
    public static int CHECK = 2;
    public static int RAISE = 3;
    public static int FOLD  = 4;
    public static int CALL  = 5;

    public Sprite[] sprites;

    KeyboardHuman(){
        sprites = new Sprite[6];
        initButton();
    }

    private void initButton() {
        sprites[PLUS ]   = new Sprite(new Texture("game/Plus.png"));
        sprites[PLUS ]   .setSize(70,50);
        sprites[PLUS ]   .setPosition(150, MyGdxGame.WORLD_HEIGHT/2-20);
        sprites[MIN  ]   = new Sprite(new Texture("game/Min.png"));
        sprites[MIN  ]   .setSize(70,50);
        sprites[MIN  ]   .setPosition(300,MyGdxGame.WORLD_HEIGHT/2-30);
        sprites[CHECK]   = new Sprite(new Texture("game/Check.png"));
        sprites[CHECK]   .setSize(150,80);
        sprites[CHECK]   .setPosition(450,MyGdxGame.WORLD_HEIGHT/2-30);
        sprites[CALL ]   = new Sprite(new Texture("game/Call.png"));
        sprites[CALL ]   .setSize(150,80);
        sprites[CALL ]   .setPosition(450,MyGdxGame.WORLD_HEIGHT/2-30);
        sprites[FOLD ]   = new Sprite(new Texture("game/Fold.png"));
        sprites[FOLD ]   .setSize(150,80);
        sprites[FOLD ]   .setPosition(600,MyGdxGame.WORLD_HEIGHT/2-30);
        sprites[RAISE]   = new Sprite(new Texture("game/Raise.png"));
        sprites[RAISE]   .setSize(150,80);
        sprites[RAISE]   .setPosition(750,MyGdxGame.WORLD_HEIGHT/2-30);

    }
}
