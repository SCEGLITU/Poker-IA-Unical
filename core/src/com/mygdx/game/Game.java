package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;

enum PlayerDirection{UP_PLAYER(0), LEFT_PLAYER(1), RIGHT_PLAYER(2), DOWN_PLAYER(3);
int value = 5;
    PlayerDirection(int i)
    {
        this.value = i;
    }
    public int getValue()
    {
        return value;
    }
    public static PlayerDirection getPlayerDirection(int i)
    {
        switch (i)
        {
            case 0:
                return UP_PLAYER;

            case 1:
                return LEFT_PLAYER;

            case 2:
                return RIGHT_PLAYER;

            case 3:
                return DOWN_PLAYER;
        }
        return UP_PLAYER;
    }
};

public class Game {

    private Sprite plus;
    private Sprite min;
    private Sprite check;
    private Sprite raise;
    private Sprite fold;
    private Cursor cursor;
    private int playerShift = DOWN_PLAYER;
    private  int round = 2;
    static final public int UP_PLAYER = 0;
    static final public int LEFT_PLAYER = 1;
    static final public int RIGHT_PLAYER = 2;
    static final public int DOWN_PLAYER = 3;
    static final public int NUM_OF_PLAYERS = 4;
    static final public int NUM_OF_CARDS = 5;
    static public Dealer dealer;
    private ArrayList<Player> players;
    public Deck deck;

    public Game() {
        dealer = new Dealer();
        players = new ArrayList<>();
        deck = Deck.getIstance();
        initButton();
        initCursor();
        createPlayers();
        setAllCardForAllPlayer();
    }

    public void createPlayers()
    {
        for(int i = 0; i < NUM_OF_PLAYERS - 1; i++)
            players.add(new Enemy(PlayerDirection.getPlayerDirection(i)));
        players.add(new Human(PlayerDirection.getPlayerDirection(NUM_OF_PLAYERS - 1)));
    }

    private void initCursor() {
        cursor = new Cursor();
    }

    private void initButton() {
        plus = new Sprite(new Texture("game/Plus.png"));
        plus.setSize(70,50);
        plus.setPosition(150,MyGdxGame.WORLD_HEIGHT/2-20);

        min = new Sprite(new Texture("game/Min.png"));
        min.setSize(70,50);
        min.setPosition(300,MyGdxGame.WORLD_HEIGHT/2-30);

        check = new Sprite(new Texture("game/Check.png"));
        check.setSize(150,80);
        check.setPosition(450,MyGdxGame.WORLD_HEIGHT/2-30);

        fold = new Sprite(new Texture("game/Fold.png"));
        fold.setSize(150,80);
        fold.setPosition(600,MyGdxGame.WORLD_HEIGHT/2-30);

        raise = new Sprite(new Texture("game/Raise.png"));
        raise.setSize(150,80);
        raise.setPosition(750,MyGdxGame.WORLD_HEIGHT/2-30);
    }

    public void setAllCardForAllPlayer(){
        dealer.shuffle();
        for(int i = 0; i < NUM_OF_PLAYERS; i++) {
            for (int j = 0; j < NUM_OF_CARDS; j++) {
                players.get(i).addCard(dealer.getCard());
            }
        }
    }

    public void changeCardforPlayer(int cpuindex, ArrayList<Integer> cardposition){
        for (int i = 0; i < cardposition.size(); i++) {
            players.get(cpuindex).removeCard(cardposition.get(i));
        }
        players.get(cpuindex).addCard(dealer.getCard());
    }

    public void drawButtonToSelect(Batch batch){
        if(playerShift ==1 && (round==1 ||round==3)){
            check.draw(batch);
            fold.draw(batch);
            min.draw(batch);
            raise.draw(batch);
            plus.draw(batch);
        }
    }

    public void draw(Batch batch){
        drawButtonToSelect(batch);
        for(Player player:players)
            player.draw(batch);
        keyboard(batch);
    }

    // if you are on the first turn or on the 3rd, move the cursor between the buttons
    // otherwise move it between the cards to choose which to discard
    // PS: remember to create a new button to confirm the end of cards discard
    public void keyboard(Batch batch){
        boolean useKeyboard = false;
        if(round == 1 && players.get(playerShift) instanceof Human){
            players.get(playerShift).drawKeybord(batch);

            if(Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) && cursor.getX()!=raise.getX()){
                cursor.setX(cursor.getX() + 150);
                useKeyboard = true;
            }
            else if(Gdx.input.isKeyJustPressed(Input.Keys.LEFT) && cursor.getX()!=plus.getX()){
                cursor.setX(cursor.getX() - 150);
                useKeyboard = true;
            }
        }
        else if(round == 2 && players.get(playerShift) instanceof Human) {

            if ((Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) || Gdx.input.isKeyJustPressed(Input.Keys.LEFT)))
            {
                int count = 0;
                int sizeCards = players.get(DOWN_PLAYER).getCards().size();
                boolean setFirstPos = true;
                for(Card card:players.get(DOWN_PLAYER).getCards())
                {
                    Sprite cardSprite = deck.getCard(card);
                    if(cursor.intersectSprite(cardSprite))
                    {
                        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT))
                            count++;
                        else
                            count--;

                        if(count == -1)
                        {
                            count = sizeCards - 1;
                        }
                        cursor.setX(deck.getCard(players.get(DOWN_PLAYER).getCard((count) % sizeCards)).getX() + 5);
                        cursor.setY(Gdx.graphics.getHeight() - MyGdxGame.CARD_HEIGHT - deck.getCard(players.get(DOWN_PLAYER).getCard((count) % sizeCards)).getY() + 5);
                        setFirstPos = false;
                        break;
                    }
                    count++;
                }

                if(setFirstPos){
                    cursor.setX(deck.getCard(players.get(DOWN_PLAYER).getCard(0)).getX() + 5);
                    cursor.setY(Gdx.graphics.getHeight() - MyGdxGame.CARD_HEIGHT - (deck.getCard(players.get(DOWN_PLAYER).getCard(0)).getY()) + 5);
                }
                useKeyboard = true;
            }
            else if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
                cursor.setX(cursor.getX() + (MyGdxGame.CARD_WIDTH + 30));
                useKeyboard = true;
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT) && cursor.getX() != deck.getCard(players.get(DOWN_PLAYER).getCard(0)).getX()) {
                cursor.setX(cursor.getX() - (MyGdxGame.CARD_WIDTH + 30));
                useKeyboard = true;
            }
        }


        if(!useKeyboard && cursor.isChangedPosition())
            for(Card card:players.get(DOWN_PLAYER).getCards())
            {
                Rectangle rectangleCard = Deck.getIstance().getCard(card).getBoundingRectangle();
                if(rectangleCard.contains(cursor.getX(), cursor.getY()))
                    System.out.println("Carta giocatore in basso: " + card.getSuite() + " " + card.getNumber());
            }
    }

    public void dispose(){
        deck.dispose();
    }


}
