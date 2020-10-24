package com.mygdx.game.control;

import com.badlogic.gdx.graphics.g2d.Batch;

import com.mygdx.game.graphicsGDX.PrinterGDX;
import com.mygdx.game.graphicsGDX.player.PlayerGraphic;
import com.mygdx.game.logic.*;
import com.mygdx.game.logic.card.Card;
import com.mygdx.game.logic.player.Human;
import com.mygdx.game.logic.player.Player;

import java.util.HashMap;

public class Game {

    private boolean roundFinished = false;

    private PrinterGDX printerGDX;
    private LogicGame logicGame;

    private HashMap<Player, PlayerGraphic> playerMap;

    public Game(Batch batch) {
        logicGame = new LogicGame(this);
        this.printerGDX = new PrinterGDX(batch, logicGame.getPlayers());

        for(int i=0; i<logicGame.getPlayers().size(); i++){
            playerMap.put(logicGame.getPlayers().get(i), printerGDX.getPlayers().get(i));
        }

        logicGame.setPokerListener(new LogicGame.PokerListener() {
            @Override
            public void raise(Player player, int v) {
                printerGDX.printRaise(v, playerMap.get(player));
            }

            @Override
            public void check(Player player) {
                printerGDX.printCheck(playerMap.get(player));
            }

            @Override
            public void call(Player player) {
                printerGDX.printCall(playerMap.get(player));
            }

            @Override
            public void fold(Player player) {
                printerGDX.printFold(playerMap.get(player));
            }

            @Override
            public void changeCard(Player player, int numberOfRMVCard) {
                printerGDX.printChangeCard(numberOfRMVCard, playerMap.get(player));
            }

            @Override
            public void printKeyboard(Human human, int currentValue) {
                
            }

            @Override
            public boolean humanCheck() {
                return false;
            }

            @Override
            public boolean humanCall() {
                return false;
            }

            @Override
            public boolean humanRaise() {
                return false;
            }

            @Override
            public boolean humanFold() {
                return false;
            }

            @Override
            public boolean humanPlus() {
                return false;
            }

            @Override
            public boolean humanMinus() {
                return false;
            }

            @Override
            public void finishRound() {

            }

            @Override
            public boolean right() {
                return false;
            }

            @Override
            public boolean left() {
                return false;
            }

            @Override
            public Card removeCard(Human human) {
                return null;
            }

            @Override
            public boolean endRemoveCard(Human human) {
                return false;
            }

            @Override
            public void updatePlayer(Player player) {

            }
        });

    }

    public void draw(){
        printerGDX.printNormal();
        logicGame.gameCicle();
        printerGDX.drawRound(roundFinished);
    }

    private void win(String winner) {
        printerGDX.printFinish(winner);
    }

    public boolean isRoundFinished() {
        return roundFinished;
    }
    public void setRoundFinished(boolean roundFinished) { this.roundFinished = roundFinished; }
}
