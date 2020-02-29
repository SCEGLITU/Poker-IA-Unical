package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.Batch;

import java.util.ArrayList;

public class Player {
    protected String name;
    protected ArrayList<Card> cards;
    protected int money;
    protected boolean fold;

    public Player() {
        this.name = "SONY";
        this.money = 5000;
        this.fold = false;
        this.cards = new ArrayList<Card>();
    }

    public Player(String name, int money) {
        this.name = name;
        this.money = money;
        this.fold = false;
        this.cards = new ArrayList<Card>();
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

    public void removeCard(int i)
    {
        cards.remove(i);
    }

    public Card getCard(int i)
    {
        return cards.get(i);
    }

    public void removeCard(Card card)
    {
        cards.remove(card);
    }

    public void reset()
    {
        cards.clear();
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

    public void draw(Batch batch,PlayerDirection direction){

    }

}
