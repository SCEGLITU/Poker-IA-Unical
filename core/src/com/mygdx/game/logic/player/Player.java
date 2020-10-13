package com.mygdx.game.logic.player;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.mygdx.game.logic.card.Card;

import java.util.ArrayList;


public abstract class Player {
    protected String name;
    protected ArrayList<Card> cards;
    protected int money;
    protected int currentChecked=0;

    protected boolean check;
    protected boolean raise;
    protected boolean fold;

    public boolean shift = true;

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

    public void newRound()
    {
        check = false;
        raise = false;
    }

    public void check() {
        check = true;
    }

    public void fold() {
        fold = true;
    }

    public void raise(int money) {
        raise = true;
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

    public boolean removeCard(Card card)
    {
        return cards.remove(card);
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

    public int getCurrentChecked() { return currentChecked; }

    public boolean isShift() {
        return shift;
    }

    public void setCurrentChecked(int currentChecked) {this.currentChecked = currentChecked; }

//    public void draw(Batch batch)
//    {
//        setCardPositionToDraw();
//        drawCards(batch);
//    }
//
    public abstract void drawCards(Batch batch);
//
//    public void setCardPositionToDraw(){
//        int x=startXCard;
//        int y=startYCard;
//        for(Card card: cards) {
//            Deck.getInstance().setCardPosition(card.getNumber(), card.getSuite(), x, y);
//            if(direction == LEFT_PLAYER || direction == RIGHT_PLAYER)
//                y += MyGdxGame.CARD_WIDTH + 30;
//            else
//                x += MyGdxGame.CARD_WIDTH + 30;
//        }
//
//    }

    public abstract void drawKeybord(Batch batch);
}
