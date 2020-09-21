package com.mygdx.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Evaluator {

    /*
        se la carta è la più alta fai il test di scala
        0. CARTA PIU' ALTA
            prendere la carta più alta del mazzo
        1. TEST SCALA
            verificare scala di 5 e carta più alta*
            se trovo scala e c'e' gia' seme STOP
        2. STESSO SEME
            contare 5 carte con lo stesso seme
            se trovo seme e c'è gia' scala STOP
        3. CARTE UGUALI DOPPIE - se c'è scala stop
            contare carte con stesso valore e conservare la più alta


     */

    // 9 s:  Scala reale (5 carte consecutive dello stesso seme)

    // 8 v:  Poker (4 carte dello stesso valore)

    // 7 s:  Colore (5 carte con lo stesso seme)

    // 6 v:  Full (3 carte dello stesso valore + 2 carte dello stesso valore)

    // 5 s:  Scala (5 carte consecutive con semi differenti)

    // 4 v:  Tris (3 carte dello stesso valore)
    // 3 v:  Doppia coppia (2 carte dello stesso valore + altre 2 carte dello stesso valore)
    // 2 v:  Coppia (2 carte dello stesso valore)
    // 1 hc: Carte piu’ alta

    protected ArrayList<Player> players;
    // map < number card, rank >
    protected Map<Integer, Integer> cardRanks = new HashMap<>();
    // map < name hand, rank hand >
    public Map<String, Integer> hands = new HashMap<>();

    {
        cardRanks.put(1, 14);
        for (int i = 2; i < 14; i++)
            cardRanks.put(i, i);

        int i = 1;
        hands.put("HIGHEST CARD", i++);
        hands.put("COUPLE", i++);
        hands.put("DOUBLE COUPLE", i++);

        hands.put("TRIS", i++);
        hands.put("STRAIGHT", i++);
        hands.put("FULL", i++);

        hands.put("COLOR", i++);
        hands.put("POKER", i++);
        hands.put("ROYAL STRAIGHT", i);
    }

    public Evaluator(ArrayList<Player> players) {
        this.players = players;
    }

    public int valueCards() {

        int indexWinner = -1;

        int[] points = new int[players.size()];

        int highestCard = 0;

        int highestCoupleCard = 0;
        int straight = 0;
        int seedCard = 0;

        int sameCard = 0;
        int maxCard = 0;
        int maxCard2 = 0;

        Card cardCount = null;
        Card cardCount2 = null;

        for (int i = 0; i < players.size(); i++) {
            ArrayList<Card> cards = players.get(i).getCards();
            maxCard = maxCard2 = sameCard = highestCard = straight = seedCard= 0;
            cardCount = cardCount2 = null;


            for (Card card : cards) {
                sameCard = 0;

                // max value card
                boolean checkStraight = false;
                if (card.getNumber() > highestCard) {
                    highestCard = card.getNumber();
                    checkStraight = true;
                }

                for (Card card2 : cards) {
                    if (card != card2) {
                        // test straight
                        if(checkStraight && card.getNumber() + 1 == card2.getNumber())
                                straight++;

                        // test value equal
                        if (card.getNumber() == card2.getNumber()) {
                            sameCard++;
                        }
                        // test seed equal
                        if(seedCard != -1 && seedCard != 5 && card.getSuite().equals(card2.getSuite()))
                            seedCard++;
                    }
                }

                // royal straight
                if(straight == 5 && seedCard == 5)
                {
                    points[i] = hands.get("ROYAL STRAIGHT")*cardRanks.get(highestCard);
                    break;
                }

                if(seedCard != 5)
                    seedCard = -1;

                // groups of equal cards
                if (sameCard > maxCard) {
                    if (cardCount != null) {
                        maxCard2 = maxCard;
                        cardCount2 = cardCount;
                    }

                    cardCount = card;
                    maxCard = sameCard;

                    if (card.getNumber() > highestCoupleCard) {
                        highestCoupleCard = card.getNumber();
                    }
                }

            }

            // poker
            if(maxCard == 4 || maxCard2 == 4)
                points[i] = hands.get("POKER") * cardRanks.get(highestCoupleCard);

            // color
            else if(seedCard == 5)
                points[i] = hands.get("COLOR") * cardRanks.get(highestCard);

            // straight
            else if(straight == 5)
                points[i] = hands.get("STRAIGHT") * cardRanks.get(highestCard);
            // full
            else if(maxCard == 3 && maxCard2 == 2)
                points[i] = hands.get("FULL") * cardRanks.get(highestCard);
            // tris
            else if(maxCard == 3)
                points[i] = hands.get("TRIS") * cardRanks.get(highestCoupleCard);
            // double couple
            else if(maxCard == 2 && maxCard2 == 2)
                points[i] = hands.get("DOUBLE COUPLE") * cardRanks.get(highestCoupleCard);
            // couple
            else if(maxCard == 2)
                points[i] = hands.get("COUPLE") * cardRanks.get(highestCoupleCard);
            // highest card
            else
                points[i] = hands.get("HIGHEST CARD") * cardRanks.get(highestCard);


            if (indexWinner != -1)
                if (points[indexWinner]<points[i])
                indexWinner = i;
        }
        return indexWinner;
    }
}