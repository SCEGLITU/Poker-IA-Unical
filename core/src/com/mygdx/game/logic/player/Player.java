package com.mygdx.game.logic.player;

import com.mygdx.game.logic.LogicGame;
import com.mygdx.game.logic.card.Card;

import java.util.ArrayList;


public abstract class Player {
    protected String name;
    protected ArrayList<Card> cards;
    protected ArrayList<Card> rmv;
    protected int money;
    protected int currentChecked=0;

    protected boolean check;
    protected boolean raise;
    protected boolean fold;

    public boolean shift = true;

    public Player(String name, int money, OnActionListerner listener) {
        this.name = name;
        this.money = money;
        this.fold = false;
        this.cards = new ArrayList<Card>();
        this.onActionListerner = listener;
    }

    public abstract void moveChoice         (int currentPlayerValue, int currentValue);
    public abstract void removeCardChoice   (int currentPlayerValue, int currentValue);


    public void changeCards(){
        for (int i = 0; i < rmv.size(); i++) {
            if(!removeCard(rmv.get(i)))
                throw new RuntimeException("Remove a no-held card " + name);
        }

        onActionListerner.changeCard(rmv.size());
        rmv.clear();
//        for(int i=0;i<cardposition.size();i++)
//            players.get(cpuindex).addCard(dealer.getCard());
        //:'D
    }

    public void newRound(){
        check = raise = false;
    }

    public void newGame() {
        newRound();
        fold = false;
    }

    public void call(){
        check = true;
        if(onActionListerner != null){
            onActionListerner.checkPerform();
        }
    }

    public void check() {
        check = true;
        if(onActionListerner != null){
            onActionListerner.checkPerform();
        }
    }

    public void fold() {
        fold = true;
        if(onActionListerner != null){
            onActionListerner.foldPerform();
        }
    }

    public void raise(int money) {
        raise = true;
        this.money -= money;
        if(onActionListerner != null){
            onActionListerner.raisePerform(money);
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
