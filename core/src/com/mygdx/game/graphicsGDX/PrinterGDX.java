package com.mygdx.game.graphicsGDX;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.graphicsGDX.card.Deck;
import com.mygdx.game.graphicsGDX.player.PlayerGDXPrinter;
import com.mygdx.game.graphicsGDX.player.PlayerGraphic;
import com.mygdx.game.logic.player.Player;
import com.mygdx.game.printer.Printer;
import com.mygdx.game.printer.PrinterText;
import com.mygdx.game.printer.TextSprite;

import java.util.ArrayList;

/*
    input player e deck

    print player

//                    if (listener.right() || listener.left()) {
//                        int count = 0;
//                        int sizeCards = player.getCards().size();
//                        boolean setFirstPos = true;
//                        for (Card card : player.getCards()) {
//                            Sprite cardSprite = deck.getCard(card);
//                            if (cursor.intersectSprite(cardSprite)) {
//                                if (listener.right())
//                                    count++;
//                                else
//                                    count--;
//
//                                if (count == -1) {
//                                    count = sizeCards - 1;
//                                }
//                                cursor.setX(deck.getCard(player.getCard((count) % sizeCards)).getX() + 5);
//                                cursor.setY(Gdx.graphics.getHeight() - MyGdxGame.CARD_HEIGHT -
//                                        deck.getCard(player.getCard((count) % sizeCards)).getY() + 5);
//                                setFirstPos = false;
//                                break;
//                            }
//                            count++;
//                        }
//
//                        if (setFirstPos) {
//                            cursor.setX(deck.getCard(player.getCard(0)).getX() + 5);
//                            cursor.setY(Gdx.graphics.getHeight() - MyGdxGame.CARD_HEIGHT -
//                                    (deck.getCard(player.getCard(0)).getY()) + 5);
//                        }
//                        useKeyboard = true;
//                    }
//                    for(Card card: player.getCards())
//                    {
//                        Rectangle rectangleCard = Deck.getInstance().getCard(card).getBoundingRectangle();
//                        if(rectangleCard.contains(cursor.getX(), cursor.getY()))
//                            if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && (!(rmve.contains(card)))) {
//                                rmve.add(card);
//                                System.out.println("remove this card");
//                            }
//                    }
 */


public class PrinterGDX implements Printer, ManagerSpriteGDX, PrinterText, PlayerGDXPrinter {

    private Cursor cursor;

    private FinishWritten finishWritten = new FinishWritten();

    private ArrayList<PlayerGraphic> players;
    private Deck deck;
    private BitmapFont bitmapFont = new BitmapFont();
    private Batch batch;

    private boolean printIn = false;

    private double fps = 10;

    public PrinterGDX(Batch batch, ArrayList<Player> plys)
    {
        this.batch = batch;
        this.deck = new Deck();
        plys.forEach(player ->
                players.add( new PlayerGraphic(player,players.size()) ));
        this.cursor = new Cursor();
        bitmapFont.getData().setScale(2);
    }

    public void printNormal()
    {
        players.forEach(this::print);
    }

    @Override
    public void print(Sprite sprite) {
        sprite.draw(batch);
    }

    @Override
    public void print(String text, int x, int y) {
        bitmapFont.draw(batch, text, x, y);
    }

    @Override
    public void print(TextSprite textSprite) {
        print(textSprite.getText(), textSprite.getX(), textSprite.getY());
    }

    @Override
    public void print(PlayerGraphic player) {
        player.setCardPositionToDraw(deck);

        for(int i=0; i<5; i++)
            print(player.getSpriteCard(deck, i));

        printUpgrade(player);
    }

    public void printFinish(String winner)
    {
        printIn = true;
        print(finishWritten);
        bitmapFont.draw(batch, "VINCE: " + winner,
                MyGdxGame.WORLD_WIDTH/2, MyGdxGame.WORLD_HEIGHT/2);
    }

    public boolean drawRound(boolean isRoundFinished)
    {

        if(pressedEnter()){
            isRoundFinished=false;
        }


        try {
            long x = (long)(1000/fps-Gdx.graphics.getDeltaTime());
            Thread.sleep(x>0?x:2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(printIn) {
            fps = 2;
            printIn = false;
        }
        else
            fps = 15;

        return isRoundFinished;
    }

    protected void printNotify(String text, PlayerGraphic player) {
        printIn = true;
        print(text, player.getNotifyX(), player.getNotifyY());
    }

    protected void printUpgrade(PlayerGraphic player) {
        print(player.getName() + "\nm: " + player.getMoney(), player.getUpgradeX(), player.getUpgradeY());
    }

    protected void printChangeCard(int ncards, PlayerGraphic player){
        printNotify("CHANGE " + ncards + " CARDS", player);
    }

    protected void printRaise(int money, PlayerGraphic player){
        printNotify("RAISE " + money, player);
    }

    protected void printFold(PlayerGraphic player){
        printNotify("FOLD", player);
    }

    protected void printCheck(PlayerGraphic player){
        printNotify("CHECK", player);
    }

    public boolean pressedEnter()
    {
        return Gdx.input.isKeyJustPressed(Input.Keys.ENTER);
    }

    public void dispose(){
        deck.dispose();
    }

}
