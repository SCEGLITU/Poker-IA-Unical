package com.mygdx.game.control;

import com.badlogic.gdx.graphics.g2d.Batch;

import com.mygdx.game.graphicsGDX.PrinterGDX;
import com.mygdx.game.graphicsGDX.player.PlayerGraphic;
import com.mygdx.game.graphicsGDX.player.human.HumanGraphic;
import com.mygdx.game.logic.*;
import com.mygdx.game.logic.card.Card;
import com.mygdx.game.logic.player.Human;
import com.mygdx.game.logic.player.Player;

import java.util.HashMap;

public class Game {

    private boolean roundFinished = false;

    private PrinterGDX printerGDX;
    private LogicGame logicGame;

    private HashMap<Player, PlayerGraphic> playerMap = new HashMap<>();

    public Game(Batch batch) {
        logicGame = new LogicGame(this);
        this.printerGDX = new PrinterGDX(batch, logicGame.getPlayers(), logicGame.getPlate());

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
                Player player = human;
                if(playerMap.get(player) instanceof HumanGraphic) {
                    printerGDX.printKeyboard((HumanGraphic) playerMap.get(player), !human.isCheck());
                }
            }

            @Override
            public boolean humanCheck(Human human) {
                PlayerGraphic playerGraphic = playerMap.get((Player) human);
                if(playerGraphic instanceof HumanGraphic){
                    return ((HumanGraphic) playerGraphic).checkSelected();
                }
                return false;
            }

            @Override
            public boolean humanCall(Human human) {
                PlayerGraphic playerGraphic = playerMap.get((Player) human);
                if(playerGraphic instanceof HumanGraphic){
                    return ((HumanGraphic) playerGraphic).callSelected();
                }
                return false;
            }

            @Override
            public boolean humanRaise(Human human) {
                PlayerGraphic playerGraphic = playerMap.get((Player) human);
                if(playerGraphic instanceof HumanGraphic){
                    return ((HumanGraphic) playerGraphic).raiseSelected();
                }
                return false;
            }

            @Override
            public boolean humanFold(Human human) {
                PlayerGraphic playerGraphic = playerMap.get((Player) human);
                if(playerGraphic instanceof HumanGraphic){
                    return ((HumanGraphic) playerGraphic).foldSelected();
                }
                return false;
            }

            @Override
            public boolean humanPlus(Human human) {
                PlayerGraphic playerGraphic = playerMap.get((Player) human);
                if(playerGraphic instanceof HumanGraphic){
                    return ((HumanGraphic) playerGraphic).plusSelected();
                }
                return false;
            }

            @Override
            public boolean humanMinus(Human human) {
                PlayerGraphic playerGraphic = playerMap.get((Player) human);
                if(playerGraphic instanceof HumanGraphic){
                    return ((HumanGraphic) playerGraphic).minusSelected();
                }
                return false;
            }

            @Override
            public void finishRound(String winner) {
                printerGDX.printFinish(winner);
            }

            @Override
            public boolean right() {
                return printerGDX.pressedRight();
            }

            @Override
            public boolean left() {
                return printerGDX.pressedLeft();
            }

            @Override
            public Card removeCard(Human human) {
                if(playerMap.get(human) instanceof HumanGraphic) {
                    return ((HumanGraphic) playerMap.get(human)).selectedCard();
                }
                return null;
            }

            @Override
            public boolean endRemoveCard(Human human) {
                return printerGDX.pressedEnter();
            }
        });

    }

    public void draw() {
        if(!isRoundFinished()) {
            printerGDX.drawRound();
            logicGame.gameCicle();
        }
    }

    public boolean isRoundFinished() {
        return roundFinished;
    }
    public void setRoundFinished(boolean roundFinished) { this.roundFinished = roundFinished; }
}
