package com.mygdx.game.logic.card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Shuffler {
    public static void shuffle(ArrayList<?> arrayList)
    {
        Random random = new Random();
        for(int i = 0; i < arrayList.size(); i++)
        {
            int j = random.nextInt(arrayList.size());
            Collections.swap(arrayList, i, j);
        }
    }
}
