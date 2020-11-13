package com.mygdx.game.logic.player;

import com.mygdx.game.logic.card.Card;

public class Human extends Player {

    public Human(String name, int money) {
        super(name, money);
    }

    protected boolean check = false;

    public boolean isCheck() {
        return check;
    }

    @Override
    public void move(int currentValue) {
        check = currentValue != currentChecked;

        if(currentPlayerValue < currentValue){
            currentPlayerValue = currentValue;
        }

        // mouse pressing input
        // press plus
        if (listener.humanPlus()) {
            currentPlayerValue += 10;
        }

        // press min
        if (listener.humanMinus()) {
            if (currentPlayerValue > currentValue)
                currentPlayerValue -= 10;
        }

        // press fold
        if (listener.humanFold()) {
            fold();
        }

        // press check
        if (listener.humanCheck() || listener.humanCall()) {
            currentPlayerValue = currentValue;
            check(currentValue);
            return;
        }
        // press raise
        else if (listener.humanRaise()) {

            if (currentPlayerValue > currentValue) {
                raise(currentPlayerValue-currentValue, currentValue);
            }
        }

        listener.performKeyboard(currentPlayerValue);
    }

    @Override
    public void removeCard() {
        Card rmvCard = (rmvCard = listener.removeCard());
        if(rmvCard != null) {
            rmv.add(rmvCard);
        }

        if(listener.endRemoveCard()){
            changeCards();
        }
    }

    private OnHumanListener listener;

    public void setOnHumanListener(OnHumanListener listener) {
        this.listener = listener;
    }

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

        void performKeyboard(int value);
    }

}
