package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.Batch;

import java.util.ArrayList;

import static com.mygdx.game.PlayerDirection.*;

public abstract class Player {
    protected String name;
    protected ArrayList<Card> cards;
    protected int money;
    protected int currentChecked=0;
    protected boolean fold;

    // start coordinates is where the cards are printed
    protected int startXCard = 0;
    protected int startYCard = 0;

    // upgrade coordinates is where the name and the amount of money is printed
    protected int upgradeX = 0;
    protected int upgradeY = 0;

    // notify coordinates is where the choice of the player (fold, raise, check...) is printed
    protected int notifyX = 0;
    protected int notifyY = 0;

    protected float angle = 0f;
    PlayerDirection direction;

    private Player()
    {

    }

    public Player(PlayerDirection direction) {
        this.name = "SONY";
        this.money = 5000;
        this.fold = false;
        this.cards = new ArrayList<Card>();
        this.direction = direction;
        switchDirection();
    }

    private void switchDirection()
    {
        switch (direction)
        {
            case LEFT_PLAYER:
                startXCard = 10;
                startYCard = 50;

                upgradeX = 8;
                upgradeY = 640;

                notifyX = 96;
                notifyY = 193;
                break;

            case RIGHT_PLAYER:
                startXCard = MyGdxGame.WORLD_WIDTH - MyGdxGame.CARD_HEIGHT + 10;
                startYCard = 50;

                upgradeX = 950;
                upgradeY = 647;

                notifyX = 813;
                notifyY = 225;
                break;

            case UP_PLAYER:
                startXCard = 270;
                startYCard = MyGdxGame.WORLD_HEIGHT - MyGdxGame.CARD_HEIGHT;

                upgradeX = 775;
                upgradeY = 644;

                notifyX = 375;
                notifyY = 528;
                break;

            case DOWN_PLAYER:
                startXCard = 270;
                startYCard = 0;

                upgradeX = 779;
                upgradeY = 91;

                notifyX = 387;
                notifyY = 133;
                break;
        }
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

    public void setCurrentChecked(int currentChecked) {this.currentChecked = currentChecked; }

    public void draw(Batch batch)
    {
        setCardPositionToDraw();
        drawCards(batch);
    }

    public abstract void drawCards(Batch batch);

    public void setCardPositionToDraw(){
        int x=startXCard;
        int y=startYCard;
        for(Card card: cards) {
            Deck.getIstance().setCardPosition(card.getNumber(), card.getSuite(), x, y);
            if(direction == LEFT_PLAYER || direction == RIGHT_PLAYER)
                y += MyGdxGame.CARD_WIDTH + 30;
            else
                x += MyGdxGame.CARD_WIDTH + 30;
        }

    }

    public abstract void drawKeybord(Batch batch);

    public int getUpgradeX() {
        return upgradeX;
    }

    public void setUpgradeX(int upgradeX) {
        this.upgradeX = upgradeX;
    }

    public int getUpgradeY() {
        return upgradeY;
    }

    public void setUpgradeY(int upgradeY) {
        this.upgradeY = upgradeY;
    }

    public int getNotifyX() {
        return notifyX;
    }

    public void setNotifyX(int notifyX) {
        this.notifyX = notifyX;
    }

    public int getNotifyY() {
        return notifyY;
    }

    public void setNotifyY(int notifyY) {
        this.notifyY = notifyY;
    }
}
