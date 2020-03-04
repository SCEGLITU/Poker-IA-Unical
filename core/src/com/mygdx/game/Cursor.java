package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;

public class Cursor {

    protected float x = 0;
    protected float y = 0;
    protected Pixmap p;

    Cursor()
    {
        p = new Pixmap(Gdx.files.internal("game/cursor.png"));
        com.badlogic.gdx.graphics.Cursor c = Gdx.graphics.newCursor(p,0,0);
        Gdx.graphics.setCursor(c);
    }

    public boolean isChangedPosition()
    {
        return x != Gdx.input.getX() || y != Gdx.input.getY();
    }


    public float getX() {
        x = InputTransform.getCursorToModelX(Gdx.graphics.getWidth(), Gdx.input.getX());
        return x;
    }

    public void setX(float x) {
        this.x = x;
        Gdx.input.setCursorPosition((int)x, (int)y);
    }

    public float getY() {
        y = InputTransform.getCursorToModelY(Gdx.graphics.getHeight(), Gdx.input.getY());
        return y;
    }

    public void setY(float y) {
        this.y = y;
        Gdx.input.setCursorPosition((int)x, (int)y);
    }

    public boolean intersectSprite(Sprite sprite)
    {
        Rectangle rectangle = sprite.getBoundingRectangle();
        return rectangle.contains(getX(), getY());
    }
}
