package com.mygdx.game;

import java.util.ArrayList;

public class Player {
    protected String name;
    protected ArrayList<Card> cards;
    protected int money;
    protected boolean fold;

    public Player() {
    }

    public Player(String name, int money, boolean fold) {
        this.name = name;
        this.money = money;
        this.fold = fold;
    }

    public void check()
    {

    }

    public void fold()
    {

    }

    public void raise(int money)
    {
        this.money -= money;
    }

    public void addCard(Card c)
    {
        if(cards.size() == 5)
            throw new RuntimeException("The player can't take more of 5 card");
        cards.add(c);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    public void setCards(ArrayList<Card> cards) {
        this.cards = cards;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public boolean isFold() {
        return fold;
    }

    public void setFold(boolean fold) {
        this.fold = fold;
    }
}
