package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.Batch;

import java.util.ArrayList;

enum PlayerDirection{
    DOWN, LEFT, UP, RIGHT;
}

public class Game {

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
        deck = new Deck();
        for(int i=0;i<3;i++)
            cpu.add(new Player());
    }

    public void setAllCardForAllPlayer(){
        for(int i=0;i<5;i++){
            player.addCard(dealer.getCard());}
        for(int i=0;i<3;i++)
            for(int j=0;j<5;j++)
                cpu.get(i).addCard(dealer.getCard());
    }

    public void changeCardforPlayer(int cpuindex,ArrayList<Integer> cardposition){
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

    public void draw(Batch batch){
        for(int i=0;i<5;i++){
            deck.getCard(player.getCard(i)).draw(batch);
        }

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
    }

    public void dispose(){
        deck.dispose();
    }
}
