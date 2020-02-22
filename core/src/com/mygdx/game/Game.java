package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.Batch;

import java.util.ArrayList;

enum PlayerDirection{
    DOWN, LEFT, UP, RIGHT;
}

public class Game {


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
            System.out.println(player.getCard(i).number + "---"+ player.getCard(i).suite);
            deck.setCardPosition(player.getCard(i).number,player.getCard(i).suite,x,y);
            x+=MyGdxGame.CARD_WIDTH+30;
        }
    }

    public void draw(Batch batch){
        for(int i=0;i<5;i++){
            deck.getCard(player.getCard(i)).draw(batch);
        }
    }

    public void dispose(){
        deck.dispose();
    }
}
