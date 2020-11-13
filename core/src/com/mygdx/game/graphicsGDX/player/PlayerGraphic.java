package com.mygdx.game.graphicsGDX.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.graphicsGDX.card.Deck;
import com.mygdx.game.logic.card.Card;
import com.mygdx.game.logic.player.Player;

import java.awt.*;

import static com.mygdx.game.graphicsGDX.player.PlayerDirection.LEFT_PLAYER;
import static com.mygdx.game.graphicsGDX.player.PlayerDirection.RIGHT_PLAYER;

public class PlayerGraphic implements ManagerCoordinatePlayer {

    protected int[] xCoordinate = new int[3];
    protected int[] yCoordinate = new int[3];

    protected static final int START = 0;
    protected static final int UPGRADE = 1;
    protected static final int NOTIFY = 2;

    protected PlayerDirection direction;
    protected Player player;

    public PlayerGraphic(Player player, int index) {
        this.player = player;
        direction = PlayerDirection.getPlayerDirection(index);
        switchDirection();
    }

    private void switchDirection() {
        switch (direction) {
            case LEFT_PLAYER:
                setAllCordinate(10, 50, 8, 640, 96, 193);
                break;

            case RIGHT_PLAYER:
                setAllCordinate(MyGdxGame.WORLD_WIDTH - MyGdxGame.CARD_HEIGHT + 10,
                        50, 950, 647, 813, 225);
                break;

            case UP_PLAYER:
                setAllCordinate(270, MyGdxGame.WORLD_HEIGHT - MyGdxGame.CARD_HEIGHT,
                        775, 644, 375, 500);
                break;

            case DOWN_PLAYER:
                setAllCordinate(270, 0, 779, 91, 387, 133);
                break;
        }
    }

    @Override
    public int getStartXCard() {
        return xCoordinate[START];
    }

    @Override
    public void setStartXCard(int startXCard) {
        xCoordinate[START] = startXCard;
    }

    @Override
    public int getStartYCard() {
        return yCoordinate[START];
    }

    @Override
    public void setStartYCard(int startYCard) {
        yCoordinate[START] = startYCard;
    }

    @Override
    public int getUpgradeX() {
        return xCoordinate[UPGRADE];
    }

    @Override
    public void setUpgradeX(int upgradeX) {
        xCoordinate[UPGRADE] = upgradeX;
    }

    @Override
    public int getUpgradeY() {
        return yCoordinate[UPGRADE];
    }

    @Override
    public void setUpgradeY(int upgradeY) {
        yCoordinate[UPGRADE] = upgradeY;
    }

    @Override
    public int getNotifyX() {
        return xCoordinate[NOTIFY];
    }

    @Override
    public void setNotifyX(int notifyX) {
        xCoordinate[NOTIFY] = notifyX;
    }

    @Override
    public int getNotifyY() {
        return yCoordinate[NOTIFY];
    }

    @Override
    public void setNotifyY(int notifyY) {
        yCoordinate[NOTIFY] = notifyY;
    }

    protected void setAllCordinate(int sx, int sy, int ux, int uy, int nx, int ny) {
        setStartXCard(sx);
        setStartYCard(sy);

        setUpgradeX(ux);
        setUpgradeY(uy);

        setNotifyX(nx);
        setNotifyY(ny);
    }

    public void setCardPositionToDraw(Deck deck) {
        int x = getStartXCard();
        int y = getStartYCard();
        for (Card card : player.getCards()) {
            deck.setCardPosition(card.getNumber(), card.getSuite(), x, y);
            if (direction == LEFT_PLAYER || direction == RIGHT_PLAYER)
                y += MyGdxGame.CARD_WIDTH + 30;
            else
                x += MyGdxGame.CARD_WIDTH + 30;
        }
    }

    public String getName() {
        return player.getName();
    }

    public int getMoney() {
        return player.getMoney();
    }

    public Sprite getSpriteCard(Deck deck, int index)
    {
        if(player.isShift()){
            return deck.getCard(player.getCard(index));
        }
        else{
            return deck.getAsDarkCard(player.getCard(index));
        }
    }
}
