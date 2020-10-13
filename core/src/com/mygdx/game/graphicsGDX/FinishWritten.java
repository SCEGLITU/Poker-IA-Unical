package com.mygdx.game.graphicsGDX;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.mygdx.game.MyGdxGame;

public class FinishWritten extends Sprite{

    {
        initButton();
    }

    private void initButton() {
        setTexture(new Texture("finish.png"));
        setSize(getWidth()-250,getHeight()-20);
        setPosition(0, MyGdxGame.WORLD_HEIGHT/2);
    }
}
