package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

import java.util.ArrayList;

/*
        0 ----> DIAMONDS -  [0-12]

        1 ----->HEARTS -    [13-25]

        2 ----->SPADES -    [26-38]

        3 -----> CLUBS -    [39-51]

 */
enum Suite{
    DIAMONDS, HEARTS, SPADES, CLUBS;
}

public class Card {

    private ArrayList<Sprite> carte;


    public Card() {
        carte = new ArrayList<>();
        carte.add(new Sprite(new Texture(Gdx.files.internal("cards/ace_of_diamonds.png"))));
        for(int i=2;i<11;i++)
            carte.add(new Sprite(new Texture("cards/"+i+"_of_diamonds.png")));
        carte.add(new Sprite(new Texture(Gdx.files.internal("cards/jack_of_diamonds.png"))));
        carte.add(new Sprite(new Texture(Gdx.files.internal("cards/queen_of_diamonds.png"))));
        carte.add(new Sprite(new Texture(Gdx.files.internal("cards/king_of_diamonds.png"))));

        carte.add(new Sprite(new Texture("cards/"+"ace_of_hearts.png")));
        for(int i=2;i<11;i++)
            carte.add(new Sprite(new Texture("cards/"+i+"_of_hearts.png")));
        carte.add(new Sprite(new Texture("cards/"+"jack_of_hearts.png")));
        carte.add(new Sprite(new Texture("cards/"+"queen_of_hearts.png")));
        carte.add(new Sprite(new Texture("cards/"+"king_of_hearts.png")));

        carte.add(new Sprite(new Texture("cards/"+"ace_of_spades.png")));
        for(int i=2;i<11;i++)
            carte.add(new Sprite(new Texture("cards/"+i+"_of_spades.png")));
        carte.add(new Sprite(new Texture("cards/"+"jack_of_spades.png")));
        carte.add(new Sprite(new Texture("cards/"+"queen_of_spades.png")));
        carte.add(new Sprite(new Texture("cards/"+"king_of_spades.png")));

        carte.add(new Sprite(new Texture("cards/"+"ace_of_clubs.png")));
        for(int i=2;i<11;i++)
            carte.add(new Sprite(new Texture("cards/"+i+"_of_clubs.png")));
        carte.add(new Sprite(new Texture("cards/"+"jack_of_clubs.png")));
        carte.add(new Sprite(new Texture("cards/"+"queen_of_clubs.png")));
        carte.add(new Sprite(new Texture("cards/"+"king_of_clubs.png")));

        for (Sprite i:carte){
            i.setSize(MyGdxGame.CARD_WIDTH,MyGdxGame.CARD_HEIGHT);
        }
    }

    public Sprite getCard(int card, Suite suite){
        if (suite == Suite.DIAMONDS){
            return carte.get((card-1));
        }
        else if(suite == Suite.HEARTS){
            return carte.get(13+(card-1));
        }
        else if(suite == Suite.SPADES){
            return carte.get(26+(card-1));
        }
        else if(suite == Suite.CLUBS){
            return carte.get(39+(card-1));
        }
        Sprite sprit=null;
        return sprit;
    }
    public Sprite setCardPosition(int card, Suite suite,int x,int y){
        if (suite == Suite.DIAMONDS){
            carte.get((card-1)).setPosition(x,y);
        }
        else if(suite == Suite.HEARTS){
            carte.get(13+(card-1)).setPosition(x,y);
        }
        else if(suite == Suite.SPADES){
            carte.get(26+(card-1)).setPosition(x,y);
        }
        else if(suite == Suite.CLUBS){
            carte.get(39+(card-1)).setPosition(x,y);
        }
        Sprite sprit=null;
        return sprit;
    }
}
