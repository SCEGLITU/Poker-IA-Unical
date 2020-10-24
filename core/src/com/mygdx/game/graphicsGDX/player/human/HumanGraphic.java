package com.mygdx.game.graphicsGDX.player.human;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.mygdx.game.graphicsGDX.player.PlayerGraphic;
import com.mygdx.game.logic.card.Card;
import com.mygdx.game.logic.player.Human;

import static com.mygdx.game.graphicsGDX.player.human.KeyboardHuman.*;

public class HumanGraphic extends PlayerGraphic {

    public KeyboardHuman keyboard;
    protected boolean printKeyboard = false;

    public HumanGraphic(Human player, int index) {
        super(player, index);
        keyboard = new KeyboardHuman();
    }

    public boolean checkSelected() {
        return listener.clickSprite(keyboard.sprites[CHECK]);
    }

    public boolean callSelected(){
        return listener.clickSprite(keyboard.sprites[CALL]);
    }

    public boolean plusSelected(){
        return listener.clickSprite(keyboard.sprites[PLUS]);
    }

    public boolean minusSelected(){
        return listener.clickSprite(keyboard.sprites[MIN]);
    }

    public boolean raiseSelected() {
        return listener.clickSprite(keyboard.sprites[RAISE]);
    }

    public boolean foldSelected() {
        return listener.clickSprite(keyboard.sprites[FOLD]);
    }

    // return selected card
    public Card selectedCard(){
        for(Card card: player.getCards())
//        {
//            Rectangle rectangleCard = Deck.getInstance().getCard(card).getBoundingRectangle();
//            if(rectangleCard.contains(cursor.getX(), cursor.getY()))
//                if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
//                    return card;
//                }

            if(listener.clickCard(card)){
                return card;
        }
        return null;
    }

    public void cursorKeyboard(boolean right, boolean left){
        if (right || left) {
            int count = 0;
            int sizeCards = player.getCards().size();
            boolean setFirstPos = true;
            for (Card card : player.getCards()) {
                if (listener.intersectCard(card)) {
                    if (right)
                        count++;
                    else
                        count--;

                    if (count == -1) {
                        count = sizeCards - 1;
                    }
                    listener.setXCursor(card);
                    listener.setYCursor(card);
//                    cursor.setX(deck.getCard(player.getCard((count) % sizeCards)).getX() + 5);
//                    cursor.setY(Gdx.graphics.getHeight() - MyGdxGame.CARD_HEIGHT -
//                            deck.getCard(player.getCard((count) % sizeCards)).getY() + 5);
                    setFirstPos = false;
                    break;
                }
                count++;
            }

            if (setFirstPos) {
                listener.setXCursor(player.getCard(0));
                listener.setYCursor(player.getCard(0));
//                cursor.setX(deck.getCard(player.getCard(0)).getX() + 5);
//                cursor.setY(Gdx.graphics.getHeight() - MyGdxGame.CARD_HEIGHT -
//                        (deck.getCard(player.getCard(0)).getY()) + 5);
            }
        }
    }

    private OnCursorListener listener;

    public void setListener(OnCursorListener listener) {
        this.listener = listener;
    }

    interface OnCursorListener{
        boolean intersectCard(Card card);
        void setXCursor(Card card);
        void setYCursor(Card card);
        boolean clickCard(Card card);
        void getDiscardedCard(Card card);
        boolean clickSprite(Sprite sprite);
    }

}
