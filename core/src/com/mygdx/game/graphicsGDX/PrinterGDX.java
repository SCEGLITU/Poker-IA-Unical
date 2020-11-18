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
import com.mygdx.game.graphicsGDX.player.human.HumanGraphic;
import com.mygdx.game.logic.Plate;
import com.mygdx.game.logic.card.Card;
import com.mygdx.game.logic.player.Human;
import com.mygdx.game.logic.player.Player;
import com.mygdx.game.printer.Printer;
import com.mygdx.game.printer.PrinterText;
import com.mygdx.game.printer.TextSprite;

import java.util.ArrayList;

import static com.mygdx.game.graphicsGDX.player.human.KeyboardHuman.CALL;
import static com.mygdx.game.graphicsGDX.player.human.KeyboardHuman.CHECK;

public class PrinterGDX implements Printer, ManagerSpriteGDX, PrinterText, PlayerGDXPrinter {

    public static final double FPS = 5;
    public static final double FPS_WRITE = 5;
    private Cursor cursor;

    private FinishWritten finishWritten = new FinishWritten();

    private ArrayList<PlayerGraphic> players = new ArrayList<>();
    private Deck deck;
    private BitmapFont bitmapFont = new BitmapFont();
    private Batch batch;

    private boolean printIn = false;
    private boolean printFinish = false;

    private double fps = FPS;
    private Plate plate;

    public PrinterGDX(Batch batch, ArrayList<Player> plys, Plate plate)
    {
        this.plate = plate;
        this.batch = batch;
        this.deck = new Deck();
        int index = 0;
        for(Player player:plys){
            if(player instanceof Human){
                HumanGraphic humanGraphic = new HumanGraphic((Human) player, index++);
                humanGraphic.setListener(new HumanGraphic.OnCursorListener() {
                    @Override
                    public boolean intersectCard(Card card) {
                        return cursor.intersectSprite(deck.getCard(card));
                    }

                    @Override
                    public void setXCursor(Card card) {
                        cursor.setX(deck.getCard(card).getX());
                    }

                    @Override
                    public void setYCursor(Card card) {
                        cursor.setY(deck.getCard(card).getY());
                    }

                    @Override
                    public boolean clickCard(Card card) {
                        return clickSprite(deck.getCard(card));
                    }

                    @Override
                    public boolean clickSprite(Sprite sprite) {
                        return
                                sprite.getBoundingRectangle().contains(cursor.getX(), cursor.getY())
                                        &&
                                        Gdx.input.isButtonJustPressed(Input.Buttons.LEFT);
                    }
                });
                players.add(humanGraphic);
            }else {
                players.add(new PlayerGraphic(player, index++));
            }
        }
        this.cursor = new Cursor();
        bitmapFont.getData().setScale(2);
    }

    public void printNormal()
    {
        for(PlayerGraphic player:players){
            print(player);
        }
        print(plate.toString(), 400, 450);
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

    public void printKeyboard(HumanGraphic player, boolean callMode){
        Sprite[] sprites = player.keyboard.sprites;
        for(Sprite sprite:sprites){
            if(callMode){
                if(!(sprite == sprites[CHECK]))
                    print(sprite);
            }else {
                if(!(sprite == sprites[CALL]))
                    print(sprite);
            }
        }

        print(""+player.getCurrentPlayerValue(), 250, (int) (MyGdxGame.WORLD_HEIGHT/2f+15));

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
        printFinish = true;
        print(finishWritten);
        bitmapFont.draw(batch, "VINCE: " + winner,
                MyGdxGame.WORLD_WIDTH/2, MyGdxGame.WORLD_HEIGHT/2);
    }

    public void drawRound()
    {
        printNormal();
        if(printIn){
            fps = FPS_WRITE;
        }

        if(printFinish){
            try {
                long x = 1000L;
                Thread.sleep(x);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            long x = (long)(1000/fps-Gdx.graphics.getDeltaTime());
            Thread.sleep(x>0?x:2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(printIn) {
            fps = FPS_WRITE;
            printIn = false;
        }
        else
            fps = FPS;

        if(printFinish){
            printFinish = false;
        }
        else
            fps = FPS;
    }

    public void printNotify(String text, PlayerGraphic player) {
        printIn = true;
        System.out.println("[N] " + player.getName() + " " + text );
        print(text, player.getNotifyX(), player.getNotifyY());
    }

    public void printUpgrade(PlayerGraphic player) {
        print(player.getName() + "\nm: " + player.getMoney(), player.getUpgradeX(), player.getUpgradeY());
    }

    public void printChangeCard(int ncards, PlayerGraphic player){
        printNotify("CHANGE " + ncards + " CARDS", player);
    }

    public void printRaise(int money, PlayerGraphic player){
        printNotify("RAISE " + money, player);
    }

    public void printFold(PlayerGraphic player){
        printNotify("FOLD", player);
    }

    public void printCheck(PlayerGraphic player){
        printNotify("CHECK", player);
    }

    public void printCall(PlayerGraphic player){
        printNotify("CALL", player);
    }

    public boolean pressedEnter()
    {
        return Gdx.input.isKeyJustPressed(Input.Keys.ENTER);
    }

    public boolean pressedRight()
    {
        return Gdx.input.isKeyJustPressed(Input.Keys.RIGHT);
    }

    public boolean pressedLeft()
    {
        return Gdx.input.isKeyJustPressed(Input.Keys.LEFT);
    }

    public void dispose(){
        deck.dispose();
    }

    public ArrayList<PlayerGraphic> getPlayers() {
        return players;
    }
}
