package com.mygdx.game.graphicsGDX.player;

public enum PlayerDirection{UP_PLAYER(0), LEFT_PLAYER(1), RIGHT_PLAYER(2), DOWN_PLAYER(3);
    int value = 5;
    PlayerDirection(int i)
    {
        this.value = i;
    }

    public int getValue()
    {
        return value;
    }

    public static PlayerDirection getPlayerDirection(int i)
    {
        switch (i)
        {
            case 0:
                return UP_PLAYER;

            case 1:
                return LEFT_PLAYER;

            case 2:
                return RIGHT_PLAYER;

            case 3:
                return DOWN_PLAYER;
        }
        return UP_PLAYER;
    }
};