package com.mygdx.game;

import java.util.ArrayList;

/*
        0 ----> DIAMONDS -  [0-12]

        1 ----->HEARTS -    [13-25]

        2 ----->SPADES -    [26-38]

        3 -----> CLUBS -    [39-51]

*/

public class Dealer {
    private ArrayList<Card> valuecards;
    private int index;

    public Dealer() {
        index = -1;
        valuecards = new ArrayList<>();
        for(int i=1;i<14;i++)
            valuecards.add(new Card(Suite.DIAMONDS,i));
        for(int i=1;i<14;i++)
            valuecards.add(new Card(Suite.HEARTS,i));
        for(int i=1;i<14;i++)
            valuecards.add(new Card(Suite.SPADES,i));
        for(int i=1;i<14;i++)
            valuecards.add(new Card(Suite.CLUBS,i));
    }

    public Card getCard(){
        index= (index+1)%valuecards.size();
        return valuecards.get(index);
    }
}
