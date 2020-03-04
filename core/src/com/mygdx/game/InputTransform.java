package com.mygdx.game;

public class InputTransform
{
    private static int appWidth = MyGdxGame.WORLD_WIDTH;
    private static int appHeight = MyGdxGame.WORLD_HEIGHT;

    public static float getCursorToModelX(int screenX, int cursorX)
    {
        return (((float)cursorX) * appWidth) / ((float)screenX);
    }

    public static float getCursorToModelX(int screenX, float cursorX)
    {
        return ((cursorX) * appWidth) / ((float)screenX);
    }

    public static float getCursorToModelY(int screenY, int cursorY)
    {
        return ((float)(screenY - cursorY)) * appHeight / ((float)screenY) ;
    }

    public static float getCursorToModelY(int screenY, float cursorY)
    {
        return ((screenY - cursorY)) * appHeight / ((float)screenY) ;
    }
}
