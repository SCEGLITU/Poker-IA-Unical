package com.mygdx.game.control;

import com.badlogic.gdx.graphics.g2d.Batch;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mygdx.game.MyGdxGame;
import com.mygdx.game.graphicsGDX.PrinterGDX;
import com.mygdx.game.graphicsGDX.player.PlayerDirection;
import com.mygdx.game.logic.*;
import com.mygdx.game.logic.card.Card;
import com.mygdx.game.logic.card.Dealer;
import com.mygdx.game.logic.card.Suite;
import com.mygdx.game.logic.player.Enemy;
import com.mygdx.game.logic.player.Human;
import com.mygdx.game.logic.player.Player;
import it.unical.mat.embasp.base.InputProgram;
import it.unical.mat.embasp.base.Output;
import it.unical.mat.embasp.languages.asp.ASPInputProgram;
import it.unical.mat.embasp.languages.asp.AnswerSet;
import it.unical.mat.embasp.languages.asp.AnswerSets;
import it.unical.mat.embasp.platforms.desktop.DesktopHandler;
import it.unical.mat.embasp.specializations.dlv2.desktop.DLV2DesktopService;

// 1. Metodo draw e drawCards in Player

public class Game {


    private int countFpsToPrint = 0;

    private PrinterGDX printerGDX;
    private LogicGame logicGame;

    public Game(Batch batch) {
        logicGame = new LogicGame();
        this.printerGDX = new PrinterGDX(batch, logicGame.getPlayers());

    }


    public void draw(){
        printerGDX.printNormal();
        logicGame.gameCicle();
        printerGDX.drawRound(logicGame.isRoundFinished());
    }



    private void win(Batch batch) {
        winner = evaluator.valueCards();

    }

        /*
        cosa copiata:
        Adesso passiamo al valore delle combinazioni delle carte nel Poker all’italiana.
        In questo elenco vediamo le combinazioni da quella piu’ forte a quella piu’ debole:

            Scala reale (5 carte consecutive dello stesso seme)
            Poker (4 carte dello stesso valore)
            Colore (5 carte con lo stesso seme)
            Full (3 carte dello stesso valore + 2 carte dello stesso valore)
            Scala (5 carte consecutive con semi differenti)
            Tris (3 carte dello stesso valore)
            Doppia coppia (2 carte dello stesso valore + altre 2 carte dello stesso valore)
            Coppia (2 carte dello stesso valore)
            Carte piu’ alta
         */

    public void allFold(Batch batch){
        int cont=0;
        for(Player p: players)
            if(p.isFold())
                cont++;
        if(cont==3){
            playerShift=3;
            round=3;
            increaseRound(batch);
        }
    }
    public void dispose(){
        deck.dispose();
    }


}
