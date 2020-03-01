package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.Batch;

import java.util.ArrayList;

import static com.mygdx.game.PlayerDirection.*;

public abstract class Player {
    protected String name;
    protected ArrayList<Card> cards;
    protected int money;
    protected boolean fold;
    protected int startXCard = 0;
    protected int startYCard = 0;
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
                break;

            case RIGHT_PLAYER:
                startXCard = MyGdxGame.WORLD_WIDTH - MyGdxGame.CARD_HEIGHT + 10;
                startYCard = 50;
                break;

            case UP_PLAYER:
                startXCard = 270;
                startYCard = MyGdxGame.WORLD_HEIGHT - MyGdxGame.CARD_HEIGHT;
                break;

            case DOWN_PLAYER:
                startXCard = 270;
                startYCard = 0;
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
            System.out.println("TIPO GIOCATORE: " + direction + " ," + card.getNumber() + " " +
                    card.getSuite() + " , COORDINATE: " + x + " - " + y);
            if(direction == LEFT_PLAYER || direction == RIGHT_PLAYER)
                y += MyGdxGame.CARD_WIDTH + 30;
            else
                x += MyGdxGame.CARD_WIDTH + 30;
        }

    }

}
