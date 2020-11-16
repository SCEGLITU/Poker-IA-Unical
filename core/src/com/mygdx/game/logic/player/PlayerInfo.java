package com.mygdx.game.logic.player;

public class PlayerInfo{
    /**
     * soldi scommessi in genere (moneyBet / numberGame)
     */
    public double avgMoneyBet;

    /**
     * numero di game vinti
     */
    public int gameWin = 0;

    /**
     * numero di punti vinti in tutte le partite
     */
    public int pointsWin = 0;

    /**
     * soldi puntati in tutte le partite
     */
    public int moneyBet;

    /**
     * soldi vinti in tutte le partite
     */
    public int moneyWin = 0;

    /**
     * soldi sul piatto nel game
     */
    public int actuallyMoneyBet;

    /**
     * soldi sul piatto nel round corrente
     */
    public int actuallyMoneyBetRound;

}
