package com.mygdx.game.logic.player;

import com.mygdx.game.logic.card.Card;

public class Human extends Player {

    public Human(String name, int money, OnActionListerner onActionListerner,
                 OnHumanListener listener) {
        super(name, money, onActionListerner);
        this.listener = listener;
    }

    @Override
    public void moveChoice(int currentPlayerValue, int currentValue) {
//        listener.printKeyboard(this, currentPlayerValue);

        // mouse pressing input


        // press plus
        if (listener.humanPlus()) {
            if (listener.left()) {
                currentPlayerValue += 10;
                System.out.println(currentPlayerValue); //get current raise value
            }
        }

        // press min
        if (listener.humanMinus()) {
            if (listener.left()) {
                if (currentPlayerValue > currentValue)
                    currentPlayerValue -= 10;
            }
        }

        // press fold
        if (listener.humanFold()) {
            if (listener.left()) {
                fold();
            }
        }

        // press check
        if (listener.humanCheck()) {
            if (listener.left()) {
                if(getCurrentChecked() < currentValue){
                    setCurrentChecked(currentValue);
                    currentPlayerValue=currentValue;
                }
                check();
                return;
            }
        }
        // press raise
        else if (listener.humanRaise()) {
            if (listener.left() && currentPlayerValue >currentValue) {

                currentValue = currentPlayerValue;

                setCurrentChecked(currentPlayerValue);
                raise(currentValue);
            }
        }
    }

    @Override
    public void removeCardChoice(int currentPlayerValue, int currentPlayer) {
        Card rmvCard = null;
        if((rmvCard = listener.removeCard()) != null) {
            rmv.add(rmvCard);
        }

        if(listener.endRemoveCard()){
            changeCards();
        }
    }

    private OnHumanListener listener;

    public interface OnHumanListener{
        boolean humanCheck();
        boolean humanCall();
        boolean humanRaise();
        boolean humanFold();
        boolean humanPlus();
        boolean humanMinus();

        boolean left();
        boolean right();

        Card removeCard();

        boolean endRemoveCard();
    }

}
