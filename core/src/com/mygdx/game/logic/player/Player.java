package com.mygdx.game.logic.player;

import com.mygdx.game.logic.card.Card;

import java.util.ArrayList;

public abstract class Player {
    protected String name;
    protected ArrayList<Card> cards;
    protected ArrayList<Card> rmv = new ArrayList<>();
    protected int money;

    /* current sum of money of player in one round */
    protected int currentChecked = 0;

    /* sum of money will be on plate */
    protected int currentPlayerValue = 0;

    /* sum of money in one game */
    protected int currentOnPlate = 0;

    protected boolean fold;

    public boolean shift = true;

    public Player(String name, int money) {
        this.name = name;
        this.money = money;
        this.fold = false;
        this.cards = new ArrayList<Card>();
    }

    public void moveChoice         (int currentValue) {
        move(currentValue);
    }

    public abstract void move(int currentValue);

    public void removeCardChoice   (){
        removeCard();
    }

    public abstract void removeCard();

    public void changeCards(){
        for (Card card : rmv) {
            if (!removeCard(card))
                throw new RuntimeException("Remove a no-held card " + name);
        }

        onActionListerner.changeCard(rmv.size());
        rmv.clear();
    }

    public void newRound(int initValue){
        if(!fold) {
            currentPlayerValue = initValue;
            currentChecked = 0;
        }
    }

    public void newGame(int initValue) {
        fold = false;
        currentOnPlate = 0;
        newRound(initValue);
    }

    public void check(int currentValue) {
        int difference = currentValue - currentChecked;

        if(onActionListerner != null){
            if(difference != 0) {
                this.money -= difference;
                currentOnPlate += difference;
                onActionListerner.checkPerform();
                System.out.println("CHECK " + getName());
            }
            else {
                onActionListerner.callPerform();
                System.out.println("CALL " + getName());
            }
        }
        currentChecked = currentValue;
        currentPlayerValue = currentValue;
    }

    public void fold() {
        fold = true;
        if(onActionListerner != null){
            System.out.println("FOLD " + getName());
            onActionListerner.foldPerform();
        }
    }

    public void raise(int raisedSum, int currentValue) {
        money -= (raisedSum + currentValue);
        currentChecked = (raisedSum + currentValue);
        currentOnPlate += (raisedSum + currentValue);
        if(onActionListerner != null){
            System.out.println("RAISE " + getName());
            onActionListerner.raisePerform(raisedSum);
        }
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

    public void setCurrentPlayerValue(int currentPlayerValue) {
        this.currentPlayerValue = currentPlayerValue;
    }

    public int getCurrentPlayerValue() {
        return currentPlayerValue;
    }

    private OnActionListerner onActionListerner;

    public void setOnActionListerner(OnActionListerner onActionListerner){
        this.onActionListerner = onActionListerner;
    }

    public interface OnActionListerner{
        void checkPerform   ();
        void callPerform    ();
        void raisePerform   (int money);
        void foldPerform    ();

        void changeCard     (int sizeRmv);
    }
}
