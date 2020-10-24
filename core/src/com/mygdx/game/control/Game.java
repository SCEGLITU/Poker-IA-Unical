package com.mygdx.game.control;

import com.badlogic.gdx.graphics.g2d.Batch;

import com.mygdx.game.graphicsGDX.PrinterGDX;
import com.mygdx.game.logic.*;

public class Game {

    private boolean roundFinished = false;

    private PrinterGDX printerGDX;
    private LogicGame logicGame;

    public Game(Batch batch) {
        logicGame = new LogicGame(this);
        this.printerGDX = new PrinterGDX(batch, logicGame.getPlayers());

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
