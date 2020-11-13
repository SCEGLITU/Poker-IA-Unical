package com.mygdx.game.logic;

import com.mygdx.game.logic.card.Card;
import com.mygdx.game.logic.player.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Evaluator {

    protected ArrayList<Player> players;
    // map < number card, rank >
    protected Map<Integer, Integer> cardRanks = new HashMap<>();
    // map < name hand, rank hand >
    public Map<String, Integer> hands = new HashMap<>();

    {
        cardRanks.put(0, 0);
        cardRanks.put(1, 14);
        for (int i = 2; i < 14; i++)
            cardRanks.put(i, i);

        int i = 1;
        hands.put("HIGHEST CARD",   i); i += 15;
        hands.put("COUPLE",         i); i += 15;
        hands.put("DOUBLE COUPLE",  i); i += 15;
        hands.put("TRIS",           i); i += 15;
        hands.put("STRAIGHT",       i); i += 15;
        hands.put("FULL",           i); i += 15;
        hands.put("COLOR",          i); i += 15;
        hands.put("POKER",          i); i += 15;
        hands.put("ROYAL STRAIGHT", i);
    }

    public Evaluator(ArrayList<Player> players) {
        this.players = players;
    }

    public int getWinner() {

        int indexWinner = -1;

        int[] points = new int[players.size()];

        for (int i = 0; i < players.size(); i++) {
            points[i] = getPoint(players.get(i));

            if (indexWinner == -1 || points[indexWinner] < points[i] ||
                    (points[indexWinner] == points[i] && getRankHighestCard(players.get(indexWinner)) < getRankHighestCard(players.get(i))) )
                if(!players.get(i).isFold())
                    indexWinner = i;
        }

        for(int i=0; i<players.size(); i++)
            System.out.println("POINT: " + players.get(i).getName() + "-" + points[i]);
        System.out.println("The winner is " + players.get(indexWinner).getName());

        return indexWinner;
    }

    public int getPoint(Player player)
    {
        // highest cards
        int highestCard = 0, highestValueCard = 0, highestCoupleCard = 0;

        // straight variable
        int straight = 1;

        // color variable
        int seedCard = 1;

        // couple, double couple, tris, full variables
        int sameCard, maxCard = 0, maxCard2 = 0, cardCount = 0, cardCount2 = 0;

        ArrayList<Card> cards = player.getCards();

        for (Card card : cards) {
            sameCard = 1;

            // max value card
            boolean checkStraight = false;
            if (highestCard == 0 || cardRanks.get(card.getNumber()) > cardRanks.get(highestCard))
                highestCard = card.getNumber();

            if(card.getNumber() > highestValueCard) {
                checkStraight = true;
                highestValueCard = card.getNumber();
            }

            for (Card card2 : cards) {
                if (card != card2) {
                    // test straight
                    if(checkStraight && card.getNumber() - 1 == card2.getNumber())
                        straight++;

                    // test value equal
                    if (card.getNumber() == card2.getNumber() && card.getNumber() != cardCount
                            && card.getNumber() != cardCount2) {
                        sameCard++;
                    }
                    // test seed equal
                    if(seedCard != -1 && seedCard != 5 && card.getSuite()==card2.getSuite())
                        seedCard++;
                }
            }

            // royal straight
            if(straight == 5 && seedCard == 5)
                return calculatePoint("ROYAL STRAIGHT", highestCard, player);

            // seedCard is only interesting when it's 5 for the color
            if(seedCard != 5)
                seedCard = -1;

            // groups of equal cards
            if (sameCard >= maxCard) {
                maxCard2 = maxCard;
                cardCount2 = cardCount;

                cardCount = card.getNumber();
                maxCard = sameCard;

                if (sameCard >= 2 && cardRanks.get(card.getNumber()) > cardRanks.get(highestCoupleCard))
                    highestCoupleCard = card.getNumber();
            }
        }

        // poker
        if(maxCard == 4 || maxCard2 == 4)
            return calculatePoint("POKER", highestCoupleCard, player);
        // color
        else if(seedCard == 5)
            return calculatePoint("COLOR", highestCard, player);
        // straight
        else if(straight == 5)
            return calculatePoint("STRAIGHT", highestCard, player);
        // full
        else if(maxCard == 3 && maxCard2 == 2)
            return calculatePoint("FULL", highestCoupleCard, player);
        // tris
        else if(maxCard == 3)
            return calculatePoint("TRIS", highestCoupleCard, player);
        // double couple
        else if(maxCard == 2 && maxCard2 == 2)
            return calculatePoint("DOUBLE COUPLE", highestCoupleCard, player);
        // couple
        else if(maxCard == 2)
            return calculatePoint("COUPLE", highestCoupleCard, player);
        // highest card
        else
            return calculatePoint("HIGHEST CARD", highestCard, player);

    }

    public int calculatePoint(String typeOfHand, int highestCardNumber, Player player)
    {
        return hands.get(typeOfHand) * cardRanks.get(highestCardNumber);
    }

    public int getRankHighestCard(Player player)
    {
        ArrayList<Card> cards = player.getCards();

        int hc = 0;

        for(Card card: cards)
            if(hc > cardRanks.get(card.getNumber()))
                hc = cardRanks.get(card.getNumber());

            return hc;
    }
}