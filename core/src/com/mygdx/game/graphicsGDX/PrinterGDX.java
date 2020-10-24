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
import com.mygdx.game.logic.player.Player;
import com.mygdx.game.printer.Printer;
import com.mygdx.game.printer.PrinterText;
import com.mygdx.game.printer.TextSprite;

import java.util.ArrayList;

import static com.mygdx.game.graphicsGDX.player.human.KeyboardHuman.CALL;
import static com.mygdx.game.graphicsGDX.player.human.KeyboardHuman.CHECK;

public class PrinterGDX implements Printer, ManagerSpriteGDX, PrinterText, PlayerGDXPrinter {

    private Cursor cursor;

    private FinishWritten finishWritten = new FinishWritten();

    private ArrayList<PlayerGraphic> players = new ArrayList<>();
    private Deck deck;
    private BitmapFont bitmapFont = new BitmapFont();
    private Batch batch;

    private boolean printIn = false;

    private double fps = 10;

    public PrinterGDX(Batch batch, ArrayList<Player> plys)
    {
        this.batch = batch;
        this.deck = new Deck();
        for(Player player:plys){
            players.add( new PlayerGraphic(player,players.size()) );
        }
        this.cursor = new Cursor();
        bitmapFont.getData().setScale(2);
    }

    public void printNormal()
    {
        for(PlayerGraphic player:players){
            print(player);
        }
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

    public void printKeyboard(HumanGraphic player, int value, boolean callMode){
        Sprite[] sprites = ((HumanGraphic)player).keyboard.sprites;
        for(Sprite sprite:sprites){
            if(callMode){
                if(!(sprite == sprites[CHECK]))
                    print(sprite);
            }else {
                if(!(sprite == sprites[CALL]))
                print(sprite);
            }
        }

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

    public void printNotify(String text, PlayerGraphic player) {
        printIn = true;
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
