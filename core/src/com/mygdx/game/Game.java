package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;

import java.util.ArrayList;

enum PlayerDirection{
    DOWN, LEFT, UP, RIGHT;
}

public class Game {

    private Sprite plus;
    private Sprite min;
    private Sprite check;
    private Sprite raise;
    private Sprite fold;
    private Sprite cursor;
    private int gameShift;
    private  int round= 1;
    static final protected int CPU_UP = 0;
    static final protected int CPU_LEFT = 1;
    static final protected int CPU_RIGHT = 2;
    static public Dealer dealer;
    private Player player;
    private ArrayList<Player> cpu;
    public Deck deck;

    public Game() {
        player = new Player();
        dealer = new Dealer();
        cpu = new ArrayList<>();
        gameShift = 1;
        deck = new Deck();
        plus = new Sprite(new Texture("game/Plus.png"));
        cursor = new Sprite(new Texture("game/cursor.png"));
        min = new Sprite(new Texture("game/Min.png"));
        check = new Sprite(new Texture("game/Check.png"));
        fold = new Sprite(new Texture("game/Fold.png"));
        raise = new Sprite(new Texture("game/Raise.png"));
        cursor.setSize(70,70);
        plus.setSize(70,50);
        min.setSize(70,50);
        check.setSize(150,80);
        fold.setSize(150,80);
        raise.setSize(150,80);
        cursor.setPosition(450,MyGdxGame.WORLD_HEIGHT/2-60);
        plus.setPosition(150,MyGdxGame.WORLD_HEIGHT/2-20);
        min.setPosition(300,MyGdxGame.WORLD_HEIGHT/2-30);
        check.setPosition(450,MyGdxGame.WORLD_HEIGHT/2-30);
        fold.setPosition(600,MyGdxGame.WORLD_HEIGHT/2-30);
        raise.setPosition(750,MyGdxGame.WORLD_HEIGHT/2-30);
        for(int i=0;i<3;i++)
            cpu.add(new Player());
    }

    public void setAllCardForAllPlayer(){
        dealer.shuffle();
        for(int i=0;i<5;i++){
            player.addCard(dealer.getCard());}
        for(int i=0;i<3;i++)
            for(int j=0;j<5;j++)
                cpu.get(i).addCard(dealer.getCard());
    }

    public void changeCardforPlayer(int cpuindex, ArrayList<Integer> cardposition){
        if(cpuindex == 0) {
            for (int i = 0; i < cardposition.size(); i++) {
                player.removeCard(cardposition.get(i));
            }
            player.addCard(dealer.getCard());
        }
        else{
            for (int i = 0; i < cardposition.size(); i++) {
                cpu.get(cpuindex-1).removeCard(cardposition.get(i));
            }
            cpu.get(cpuindex-1).addCard(dealer.getCard());
        }
    }

    public void setCardPositionToDraw(){
        int x=270;
        int y=0;
        for(int i=0;i<5;i++){
            System.out.println(player.getCard(i).getNumber() + "---"+ player.getCard(i).getSuite());
            deck.setCardPosition(player.getCard(i).getNumber(),player.getCard(i).getSuite(),x,y);
            x += MyGdxGame.CARD_WIDTH+30;
        }

        for(int i = 0; i < 3; i++){
            // player left
            switch (i)
            {
                case CPU_LEFT:
                    x = 10;
                    y = 50;
                    break;

                case CPU_RIGHT:
                    x = MyGdxGame.WORLD_WIDTH - MyGdxGame.CARD_HEIGHT + 10;
                    y = 50;
                    break;

                case CPU_UP:
                    x = 270;
                    y = MyGdxGame.WORLD_HEIGHT - MyGdxGame.CARD_HEIGHT;
                    break;
            }

            for(Card card:cpu.get(i).cards) {
                deck.setCardPosition(card.getNumber(), card.getSuite(), x, y);
                System.out.println("TIPO GIOCATORE: " + i + " ," + card.getNumber() + " " +
                        card.getSuite() + " , COORDINATE: " + x + " - " + y);
                if(i != CPU_UP)
                    y += MyGdxGame.CARD_WIDTH + 30;
                else
                    x += MyGdxGame.CARD_WIDTH + 30;
            }
        }
    }

    public void drawButtonToSelect(Batch batch){
        if(gameShift ==1 && (round==1 ||round==3)){
            check.draw(batch);
            fold.draw(batch);
            min.draw(batch);
            raise.draw(batch);
            plus.draw(batch);
            cursor.draw(batch);
        }
        else if(gameShift ==1 && round==2){
            cursor.draw(batch);
        }
    }

    public void draw(Batch batch){
        for(int i=0;i<5;i++){
            deck.getCard(player.getCard(i)).draw(batch);
        }

        drawButtonToSelect(batch);

        for (int i = 0; i < 3; i++) {
            float angle = 0f;
            switch (i)
            {
                case CPU_UP:
                    angle = 180f;
                    break;
                case CPU_LEFT:
                    angle = 270f;
                    break;
                case CPU_RIGHT:
                    angle = 90f;
                    break;
            }
            for (Card card : cpu.get(i).cards) {
                deck.getCard(card).setOrigin(MyGdxGame.CARD_WIDTH / 2, MyGdxGame.CARD_HEIGHT / 2);
                deck.getCard(card).setRotation(angle);
                deck.getAsDarkCard(card).draw(batch);
            }
        }
        keyboard();
    }

    //if you are on the first turn or on the 3rd, move the cursor between the buttons
    // otherwise move it between the cards to choose which to discard
    // PS: remember to create a new button to confirm the end of cards discard
    public void keyboard(){
        if(round==1 && gameShift==1){
            if(Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) && cursor.getX()!=raise.getX()){
                cursor.setX(cursor.getX()+150);
            }
            if(Gdx.input.isKeyJustPressed(Input.Keys.LEFT) && cursor.getX()!=plus.getX()){
                cursor.setX(cursor.getX()-150);
            }
        }
        else if(round==2 && gameShift ==1){
            if(cursor.getY()!=0){
                cursor.setX(deck.getCard(player.getCard(0)).getX());
                cursor.setY(0);
            }
            if(Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) && cursor.getX()!=deck.getCard(player.getCard(4)).getX()){
                cursor.setX(cursor.getX()+(MyGdxGame.CARD_WIDTH+30));
            }
            if(Gdx.input.isKeyJustPressed(Input.Keys.LEFT) && cursor.getX()!=deck.getCard(player.getCard(0)).getX()){
                cursor.setX(cursor.getX()-(MyGdxGame.CARD_WIDTH+30));
            }

        }
    }

    public void dispose(){
        deck.dispose();
    }


}
