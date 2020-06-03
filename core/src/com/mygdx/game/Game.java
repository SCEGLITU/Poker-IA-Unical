package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;

import java.net.URL;
import java.util.List;

import java.io.File;
import java.util.ArrayList;

import it.unical.mat.embasp.base.Handler;
import it.unical.mat.embasp.base.InputProgram;
import it.unical.mat.embasp.base.Output;
import it.unical.mat.embasp.languages.asp.ASPInputProgram;
import it.unical.mat.embasp.languages.asp.AnswerSet;
import it.unical.mat.embasp.languages.asp.AnswerSets;
import it.unical.mat.embasp.platforms.desktop.DesktopHandler;
import it.unical.mat.embasp.specializations.dlv2.desktop.DLV2DesktopService;

enum PlayerDirection{UP_PLAYER(0), LEFT_PLAYER(1), RIGHT_PLAYER(2), DOWN_PLAYER(3);
int value = 5;
    PlayerDirection(int i)
    {
        this.value = i;
    }
    public int getValue()
    {
        return value;
    }
    public static PlayerDirection getPlayerDirection(int i)
    {
        switch (i)
        {
            case 0:
                return UP_PLAYER;

            case 1:
                return LEFT_PLAYER;

            case 2:
                return RIGHT_PLAYER;

            case 3:
                return DOWN_PLAYER;
        }
        return UP_PLAYER;
    }
};

public class Game {

    private Sprite plus;
    private Sprite min;
    private Sprite check;
    private Sprite raise;
    private Sprite fold;
    Sprite finish;
    private Cursor cursor;
    private int playerShift = 0;
    private int round = 1;
    private int currentValue = 20;
    private int currentPlayerValue = 0;
    boolean isRoundFinished = false;
    public ArrayList rmve;
    static final public int UP_PLAYER = 0;
    static final public int LEFT_PLAYER = 1;
    static final public int RIGHT_PLAYER = 2;
    static final public int DOWN_PLAYER = 3;
    static final public int NUM_OF_PLAYERS = 4;
    static final public int NUM_OF_CARDS = 5;
    static public Dealer dealer;
    private ArrayList<Player> players;
    public Deck deck;
    private String encodingNormalRound;
    private String encodingDiscardCardsRound;
    private String pathDlv;
    private static Handler handler;
    InputProgram facts;

    public Game() {
        dealer = new Dealer();
        players = new ArrayList<>();
        rmve = new ArrayList<>();
        deck = Deck.getIstance();
        initButton();
        initCursor();
        createPlayers();
        setAllCardForAllPlayer();
        setPathResources();
    }

    private void setPathResources()
    {
        if (System.getProperty("os.name").contains("Linux")) {
            pathDlv =
                    (new File(Game.class.getResource("/").toString()).getParentFile().getParentFile().getParent()
                            +"/resources/main/dlv2").substring(5);
            encodingNormalRound = "encodings/normalRound.dlv";
            encodingDiscardCardsRound= "encodings/discardCardsRound.dlv";
        }else
        {
            pathDlv = "./desktop/build/resources/main/dlv2.win.x64_5";
            encodingNormalRound = "./desktop/build/resources/main/encodings/normalRound.dlv";
            encodingDiscardCardsRound = "./desktop/build/resources/main/encodings/discardCardsRound.dlv";
        }
    }

    public void moveByAI(int cpuIndex) {
        handler = new DesktopHandler(new DLV2DesktopService(pathDlv));
        facts = new ASPInputProgram();
        ArrayList<Card> crds = players.get(cpuIndex).getCards();
        try {
            for (int i = 0; i < 5; i++) {
                facts.addObjectInput(new Card(crds.get(i).suite, crds.get(i).number));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        handler.addProgram(facts);
        InputProgram encoding = new ASPInputProgram();
        if(round==2)
            encoding.addFilesPath(encodingDiscardCardsRound);
        else
            encoding.addFilesPath(encodingNormalRound);
        handler.addProgram(encoding);

        Output o = handler.startSync();
        AnswerSets answers = (AnswerSets) o;
        if (answers.getAnswersets().size() == 0) {
            System.out.println("NO ANSWERSETS!!");
            System.exit(0);
        }
        else
        {
            for(AnswerSet an: answers.getAnswersets())
            {
                for(String buh:an.getAnswerSet())
                {
                    System.out.println(buh);
                }
            }
        }

        boolean trovato = false;

        //discard cards if it's the correct round
        if (round == 2) {
            ArrayList rmv = new ArrayList();
            for (AnswerSet a : answers.getAnswersets()) {
                if (trovato)
                    break;
                try {
                    for (Object obj : a.getAtoms()) {
                        if (obj instanceof Card) { //We haven't need Card obj but CardToDiscard or something like that (obv objs that DLV want to discard)
                            rmv.add(obj);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            //changeCardforPlayer(cpuIndex,rmv); //discard cards here
        }
        else{
            //DLV select check raise or fold
        }
    }
    public void createPlayers()
    {
        for(int i = 0; i < NUM_OF_PLAYERS - 1; i++)
            players.add(new Enemy(PlayerDirection.getPlayerDirection(i)));
        players.add(new Human(PlayerDirection.getPlayerDirection(NUM_OF_PLAYERS - 1)));
    }

    private void initCursor() {
        cursor = new Cursor();
    }

    private void initButton() {
        plus = new Sprite(new Texture("game/Plus.png"));
        plus.setSize(70,50);
        plus.setPosition(150,MyGdxGame.WORLD_HEIGHT/2-20);

        min = new Sprite(new Texture("game/Min.png"));
        min.setSize(70,50);
        min.setPosition(300,MyGdxGame.WORLD_HEIGHT/2-30);

        check = new Sprite(new Texture("game/Check.png"));
        check.setSize(150,80);
        check.setPosition(450,MyGdxGame.WORLD_HEIGHT/2-30);

        fold = new Sprite(new Texture("game/Fold.png"));
        fold.setSize(150,80);
        fold.setPosition(600,MyGdxGame.WORLD_HEIGHT/2-30);

        raise = new Sprite(new Texture("game/Raise.png"));
        raise.setSize(150,80);
        raise.setPosition(750,MyGdxGame.WORLD_HEIGHT/2-30);
        finish = new Sprite(new Texture("finish.png"));
        finish.setSize(finish.getWidth()-250,finish.getHeight()-20);
        finish.setPosition(0,MyGdxGame.WORLD_HEIGHT/2);
    }

    public void setAllCardForAllPlayer(){
        dealer.shuffle();
        for(int i = 0; i < NUM_OF_PLAYERS; i++) {
            for (int j = 0; j < NUM_OF_CARDS; j++) {
                players.get(i).addCard(dealer.getCard());
            }
        }
    }

    public void changeCardforPlayerForPosition(int cpuindex, ArrayList<Integer> cardposition){
        for (int i = 0; i < cardposition.size(); i++) {
            players.get(cpuindex).removeCard(cardposition.get(i));
        }
        for(int i=0;i<cardposition.size();i++)
            players.get(cpuindex).addCard(dealer.getCard());
    }

    public void changeCardforPlayer(int cpuindex, ArrayList<Card> cardposition){
        for (int i = 0; i < cardposition.size(); i++) {
            players.get(cpuindex).removeCard(cardposition.get(i));
        }

        for(int i=0;i<cardposition.size();i++)
            players.get(cpuindex).addCard(dealer.getCard());
        //:'D
    }

    /*public void drawButtonToSelect(Batch batch){
        if(playerShift ==1 && (round==1 ||round==3)){
            check.draw(batch);
            fold.draw(batch);
            min.draw(batch);
            raise.draw(batch);
            plus.draw(batch);
        }
    }*/

    public void draw(Batch batch){
       for(Player player:players)
            player.draw(batch);
       gameCicle(batch);
       if(isRoundFinished) {
           finish.draw(batch);
           if(Gdx.input.isKeyJustPressed(Input.Keys.ENTER)){
               isRoundFinished=false;
           }
       }
    }

    public void gameCicle(Batch batch){
        boolean useKeyboard = false;
        if(!isRoundFinished){
            if(!players.get(playerShift).isFold() && players.get(playerShift).getCurrentChecked()<currentValue) {
                if ((round == 1 || round == 3) && players.get(playerShift) instanceof Human) {
                    players.get(playerShift).drawKeybord(batch);

                    //mouse pressing input
                    if (cursor.intersectSprite(((Human) players.get(playerShift)).plus)) {
                        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                            currentPlayerValue += 10;
                            System.out.println(currentPlayerValue); //get current raise value
                        }
                    }
                    if (cursor.intersectSprite(((Human) players.get(playerShift)).min)) {
                        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                            if (currentPlayerValue > currentValue)
                                currentPlayerValue -= 10;
                            System.out.println(currentPlayerValue); //get current raise value
                        }
                    }
                    if (cursor.intersectSprite(((Human) players.get(playerShift)).fold)) {
                        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                            (players.get(playerShift)).setFold(true);
                            System.out.println(players.get(playerShift).isFold()); //set fold true
                        }
                    }
                    if (cursor.intersectSprite(((Human) players.get(playerShift)).check)) {
                        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                            players.get(playerShift).setCurrentChecked(currentValue);
                            currentPlayerValue=currentValue;
                            System.out.println(currentValue); //get current check value
                            increaseRound();
                        }
                    }
                    else if (cursor.intersectSprite(((Human) players.get(playerShift)).raise)) {
                        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && currentPlayerValue >currentValue) {
                            currentValue = currentPlayerValue;
                            players.get(playerShift).setCurrentChecked(currentPlayerValue);
                            System.out.println(currentValue); //get current check value
                            if(playerShift==0)
                                increaseRound();
                            else
                                playerShift=0;
                        }
                    }
                    //END
                }
                else if (round == 2 && players.get(playerShift) instanceof Human) {
                    if ((Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) || Gdx.input.isKeyJustPressed(Input.Keys.LEFT))) {
                        int count = 0;
                        int sizeCards = players.get(playerShift).getCards().size();
                        boolean setFirstPos = true;
                        for (Card card : players.get(playerShift).getCards()) {
                            Sprite cardSprite = deck.getCard(card);
                            if (cursor.intersectSprite(cardSprite)) {
                                if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT))
                                    count++;
                                else
                                    count--;

                                if (count == -1) {
                                    count = sizeCards - 1;
                                }
                                cursor.setX(deck.getCard(players.get(playerShift).getCard((count) % sizeCards)).getX() + 5);
                                cursor.setY(Gdx.graphics.getHeight() - MyGdxGame.CARD_HEIGHT - deck.getCard(players.get(playerShift).getCard((count) % sizeCards)).getY() + 5);
                                setFirstPos = false;
                                break;
                            }
                            count++;
                        }

                        if (setFirstPos) {
                            cursor.setX(deck.getCard(players.get(playerShift).getCard(0)).getX() + 5);
                            cursor.setY(Gdx.graphics.getHeight() - MyGdxGame.CARD_HEIGHT - (deck.getCard(players.get(playerShift).getCard(0)).getY()) + 5);
                        }
                        useKeyboard = true;
                    }
                    for(Card card:players.get(playerShift).getCards())
                    {
                        Rectangle rectangleCard = Deck.getIstance().getCard(card).getBoundingRectangle();
                        if(rectangleCard.contains(cursor.getX(), cursor.getY()))
                            if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && (!(rmve.contains(card)))) {
                                rmve.add(card);
                                System.out.println("remove this card");
                            }
                    }
                    if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                        changeCardforPlayer(playerShift,rmve);
                        System.out.println("removed all selected cards");
                        rmve.clear();
                        increaseRound();
                    }
                }
                else if ((round == 1 || round == 3) && (!(players.get(playerShift) instanceof Human))) {
                    moveByAI(playerShift); //decidere check fold o raise
                    increaseRound();
                }
                else if ((round == 2) && (!(players.get(playerShift) instanceof Human))) {
                    moveByAI(playerShift); //scarta carte
                    increaseRound();
                }
            }
            else{
                increaseRound();
            }
        }
        if(!useKeyboard && cursor.isChangedPosition())
            for(Card card:players.get(DOWN_PLAYER).getCards())
            {
                Rectangle rectangleCard = Deck.getIstance().getCard(card).getBoundingRectangle();
                //if(rectangleCard.contains(cursor.getX(), cursor.getY()))
                    //System.out.println("Carta giocatore in basso: " + card.getSuite() + " " + card.getNumber());
            }
    }

    public void increaseRound(){
        System.out.println(playerShift+" --ROUND--> "+round);
        playerShift++;
        currentPlayerValue=currentValue;
        if(playerShift==4){
            currentValue=20;
            currentPlayerValue=20;
            for(Player p:players)
                if(!p.isFold()) {
                    if(p.getMoney()>=p.getCurrentChecked())
                        p.setMoney(p.getMoney() - p.getCurrentChecked());
                    p.setCurrentChecked(0);
                }
            playerShift=0;
            round++;
            if(round==4){
                for(Player p:players){
                    p.setCurrentChecked(0);
                }
                isRoundFinished = true;
                playerShift=0;
                round=1;
            }
        }
    }

    public void dispose(){
        deck.dispose();
    }


}
