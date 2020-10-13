package com.mygdx.game.graphicsGDX.player.human;

import com.mygdx.game.graphicsGDX.Cursor;
import com.mygdx.game.graphicsGDX.player.PlayerGraphic;
import com.mygdx.game.logic.player.Human;

public class HumanGraphic extends PlayerGraphic {

    protected KeyboardHuman keyboard;

    public HumanGraphic(Human player, int index) {
        super(player, index);
        keyboard = new KeyboardHuman();
    }

    public boolean checkSelected(Cursor cursor) {
        return cursor.intersectSprite(keyboard.check);
    }

    public boolean callSelected(Cursor cursor){
        return cursor.intersectSprite(keyboard.call);
    }

    public boolean raiseSelected(Cursor cursor) {
        return cursor.intersectSprite(keyboard.raise);
    }

    public boolean foldSelected(Cursor cursor) {
        return cursor.intersectSprite(keyboard.fold);
    }
}
