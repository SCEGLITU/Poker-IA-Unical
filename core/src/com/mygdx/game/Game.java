package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;

import java.net.URL;
import java.util.HashSet;
import java.util.List;

import java.io.File;
import java.util.ArrayList;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
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

    private BitmapFont bitmapFont = new BitmapFont();
    private Sprite plus;
    private Sprite min;
    private Sprite check;
    private Sprite raise;
    private Sprite fold;
    Sprite finish;
    private Cursor cursor;
    int winner =-1;
    int cash=0;
    private int playerShift = 0;
    private int round = 1;
    private int currentValue = 20;
    private int currentPlayerValue = 0;
    private boolean printIn = false;
    private int countFpsToPrint = 0;
    private double fps = 10;
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
    private String encodingChoiseRaise;
    private String pathDlv;
    private static Handler handler;
    private Evaluator evaluator;
    InputProgram facts;

    public Game() {
        dealer = new Dealer();
        players = new ArrayList<>();
        rmve = new ArrayList<>();
        bitmapFont.getData().setScale(2);
        deck = Deck.getIstance();
        initButton();
        initCursor();
        createPlayers();
        setAllCardForAllPlayer();
        setPathResources();
        evaluator = new Evaluator(players);
    }

    private void setPathResources()
    {
        if (System.getProperty("os.name").contains("Linux")) {
            pathDlv =
                    (new File(Game.class.getResource("/").toString()).getParentFile().getParentFile().getParent()
                            +"/resources/main/dlv2").substring(5);
            encodingNormalRound = "core/assets/encodings/normalRound.dlv";
            encodingDiscardCardsRound= "core/assets/encodings/discardCardsRound.dlv";
            encodingChoiseRaise = "core/assets/encodings/choiceRaise.dlv";
        }else
        {
            pathDlv = "./desktop/build/resources/main/dlv2.win.x64_5";
            encodingNormalRound = "./desktop/build/resources/main/encodings/normalRound.dlv";
            encodingDiscardCardsRound = "./desktop/build/resources/main/encodings/discardCardsRound.dlv";
            encodingChoiseRaise = "./desktop/build/resources/main/encodings/choiceRaise.dlv";
        }
    }

    public void moveByAI(int cpuIndex,Batch batch) {
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
            System.out.println("Player: "+ cpuIndex);
            //System.out.println("NORMALROUND FACTS: "+facts.getPrograms());
            if(round!=2){
                //Insert all facts for round 1-3 in choiseRaise.dlv
                encoding = new ASPInputProgram();
                handler = new DesktopHandler(new DLV2DesktopService(pathDlv));
                encoding.addFilesPath(encodingChoiseRaise);
                facts = new ASPInputProgram();
                facts.addProgram("myWallet("+ players.get(cpuIndex).getMoney()+").");
                int plate=0;
                for(int i=0;i<playerShift;i++)
                    if(players.get(i).isFold() == false )
                        plate+=currentValue;
                //plate+=cash; if I sum cash raiseChose.dlv goes in loop
                //probably problem: code in dlv || missing parameters in input like AVG
                facts.addProgram("plate("+ plate+").");
            }
            for(AnswerSet an: answers.getAnswersets())
            {
                for(String buh:an.getAnswerSet())
                {
                    if(round!=2){
                        facts.addProgram(buh+".");
                        //End insert facts round 1-3 choiseRaise.dlv
                    }
                }
            }
            if(round!=2){
                handler.addProgram(facts);
                handler.addProgram(encoding);
                o = handler.startSync();
                answers = (AnswerSets) o;
                if (answers.getAnswersets().size() == 0) {
                    System.out.println("NO ANSWERSETS!!");
                    //System.out.println("CHOICERAISE FACTS "+facts.getPrograms());
                    System.exit(0);
                }
                else{
                    //System.out.println("CHOICERAISE FACTS "+facts.getPrograms());
                    for(AnswerSet an: answers.getAnswersets())
                    {
                        Pattern patternRaise=Pattern.compile("raise\\((\\d+)\\)");
                        Matcher matcher;
                        String r=null;
                        boolean check=false;
                        boolean fold = false;
                        System.out.println("----------ANSWERSET: -----------");
                        for(String buh:an.getAnswerSet()) {
                            System.out.println(buh);
                            matcher=patternRaise.matcher(buh);
                            if(matcher.find()){
                                r=matcher.group(1);
                                System.out.println(r+" RAISE LIFE x0x0");
                            }
                            if(buh.matches("check")){
                                check=true;
                            }
                            if(buh.matches("fold")){
                                fold=true;
                            }
                        }
                        System.out.println("----------END ANSWERSET------------");
                        if(r!=null) {
                            if( currentValue < Integer.parseInt(r)) {
                                System.out.println("DLV RAISE. I'll do raise 'cause CURRENT VALUE:"+currentValue+"< "+r);
                                currentValue = Integer.parseInt(r);
                                currentPlayerValue = currentValue;
                                players.get(playerShift).setCurrentChecked(currentValue);
                                if (playerShift != 0)
                                    playerShift = -1;
                                else
                                    increaseRound(batch);
                            }
                            else
                                players.get(playerShift).setCurrentChecked(currentValue);
                                System.out.println("if there is an error in DLV code and I'm trying to raise a raise<currentValue, then check");
                                increaseRound(batch);
                        }
                        else if(r==null && check){
                            players.get(playerShift).setCurrentChecked(currentValue);
                            increaseRound(batch);
                            System.out.println("DLV CHECK");
                        }
                        else if(r==null && check==false &&fold==true){
                            players.get(playerShift).setFold(true);
                            increaseRound(batch);
                            System.out.println("DLV FOLD");
                        }
                    }
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
            players.get(i).reset();
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
        for(Player player:players) {
           player.draw(batch);
           bitmapFont.draw(batch,"" + player.getName() + "\nm: " + player.getMoney(),
                   player.getUpgradeX(), player.getUpgradeY());
        }

        gameCicle(batch);
        if(isRoundFinished) {
           finish.draw(batch);
           if(Gdx.input.isKeyJustPressed(Input.Keys.ENTER)){
               isRoundFinished=false;
           }
        }

        try {
            Thread.sleep((long)(1000/fps-Gdx.graphics.getDeltaTime()));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(printIn) {
            fps = 2;
            printIn = false;
        }
        else
            fps = 15;
    }

    public void gameCicle(Batch batch){

        boolean useKeyboard = false;


        if(!isRoundFinished){

            Player player = players.get(playerShift);

            // if the player hasn't folded and the current checked sum is less than the current value of the plate
            if(!player.isFold()) {

                // if it's in the round where the player can raise the sum
                if ((round == 1 || round == 3) && player instanceof Human) {
                    bitmapFont.draw(batch, ""+currentPlayerValue, 250,MyGdxGame.WORLD_HEIGHT/2f+15);
                    player.drawKeybord(batch);

                    if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT))
                        System.out.println("Mouse x: " + cursor.getX() + " y: " + cursor.getY());

                    // mouse pressing input


                    // press plus
                    if (cursor.intersectSprite(((Human) player).plus)) {
                        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                            currentPlayerValue += 10;
                            System.out.println(currentPlayerValue); //get current raise value
                        }
                    }

                    // press min
                    if (cursor.intersectSprite(((Human) player).min)) {
                        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                            if (currentPlayerValue > currentValue)
                                currentPlayerValue -= 10;
                            System.out.println(currentPlayerValue); // get current raise value
                        }
                    }

                    // press fold
                    if (cursor.intersectSprite(((Human) player).fold)) {
                        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                            player.setFold(true);
                            printFold(playerShift, batch);
                            System.out.println(player.isFold()); // set fold true
                        }
                    }

                    // press check
                    if (cursor.intersectSprite(((Human) player).check)) {
                        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                            if(player.getCurrentChecked() < currentValue){
                                player.setCurrentChecked(currentValue);
                                currentPlayerValue=currentValue;
                                System.out.println(currentValue); //get current check value
                            }
                            printCheck(playerShift, batch);
                            increaseRound(batch);
                        }
                    }
                    // press raise
                    else if (cursor.intersectSprite(((Human) player).raise)) {
                        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && currentPlayerValue >currentValue) {
                            currentValue = currentPlayerValue;
                            player.setCurrentChecked(currentPlayerValue);
                            System.out.println(currentValue); //get current check value
                            printRaise(currentValue, playerShift, batch);
                            if(playerShift==0)
                                increaseRound(batch);
                            else
                                playerShift=0;
                        }
                    }
                    //END
                }
                else if (round == 2 && player instanceof Human) {
                    if ((Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) || Gdx.input.isKeyJustPressed(Input.Keys.LEFT))) {
                        int count = 0;
                        int sizeCards = player.getCards().size();
                        boolean setFirstPos = true;
                        for (Card card : player.getCards()) {
                            Sprite cardSprite = deck.getCard(card);
                            if (cursor.intersectSprite(cardSprite)) {
                                if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT))
                                    count++;
                                else
                                    count--;

                                if (count == -1) {
                                    count = sizeCards - 1;
                                }
                                cursor.setX(deck.getCard(player.getCard((count) % sizeCards)).getX() + 5);
                                cursor.setY(Gdx.graphics.getHeight() - MyGdxGame.CARD_HEIGHT - deck.getCard(player.getCard((count) % sizeCards)).getY() + 5);
                                setFirstPos = false;
                                break;
                            }
                            count++;
                        }

                        if (setFirstPos) {
                            cursor.setX(deck.getCard(player.getCard(0)).getX() + 5);
                            cursor.setY(Gdx.graphics.getHeight() - MyGdxGame.CARD_HEIGHT - (deck.getCard(player.getCard(0)).getY()) + 5);
                        }
                        useKeyboard = true;
                    }
                    for(Card card: player.getCards())
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
                        increaseRound(batch);
                    }
                }
                else if (round == 1 || round == 3) {
                    moveByAI(playerShift,batch); //decidere check fold o raise
                }
                else if (round == 2) {
                    //moveByAI(playerShift,batch); //scarta carte
                    increaseRound(batch);
                }
            }
            else{
                increaseRound(batch);
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

    private void printRaise(int currentValue, int playerShift, Batch batch) {
       printNotify(playerShift, "RAISE " + currentValue, batch);
    }

    private void printFold(int playerShift, Batch batch) {
        printNotify(playerShift, "FOLD", batch);
    }

    private void printCheck(int playerShift, Batch batch) {
        printNotify(playerShift, "CHECK", batch);
    }

    private void printNotify(int playerShift, String s, Batch batch)
    {
        printIn = true;
        bitmapFont.draw(batch, s,
                players.get(playerShift).getNotifyX(),players.get(playerShift).getNotifyY());
    }

    public void increaseRound(Batch batch){
        System.out.println(playerShift+" --ROUND--> "+round);
        playerShift++;
        currentPlayerValue=currentValue;
        if(playerShift==4){
            currentValue=0;
            currentPlayerValue=20;
            for(Player p:players)
                if(!p.isFold()) {
                    if(p.getMoney()>=p.getCurrentChecked()){
                        p.setMoney(p.getMoney() - p.getCurrentChecked());
                        cash+=p.getCurrentChecked();
                    }
                    p.setCurrentChecked(0);
                }
            playerShift=0;
            round++;
            if(round==4){
                
                win(batch);
                players.get(winner).setMoney(players.get(winner).getMoney()+cash);
                for(Player p:players){
                    p.setCurrentChecked(0);
                }
                isRoundFinished = true;
                dealer.shuffle();
                cash=0;
                playerShift=0;
                round=1;
                setAllCardForAllPlayer();
            }
        }
    }

    private void win(Batch batch) {
        winner = evaluator.valueCards();
        System.out.println("VINCE: " + winner);
        bitmapFont.draw(batch, "VINCE: " + winner,
                MyGdxGame.WORLD_WIDTH/2, MyGdxGame.WORLD_HEIGHT/2);
        printIn = true;
        //printNotify(playerShift, "VINCE: " + evaluator.valueCards(), batch);
    }

        /*
        cosa copiata:
        Adesso passiamo al valore delle combinazioni delle carte nel Poker all’italiana.
        In questo elenco vediamo le combinazioni da quella piu’ forte a quella piu’ debole:

            Scala reale (5 carte consecutive dello stesso seme)
            Poker (4 carte dello stesso valore)
            Colore (5 carte con lo stesso seme)
            Full (3 carte dello stesso valore + 2 carte dello stesso valore)
            Scala (5 carte consecutive con semi differenti)
            Tris (3 carte dello stesso valore)
            Doppia coppia (2 carte dello stesso valore + altre 2 carte dello stesso valore)
            Coppia (2 carte dello stesso valore)
            Carte piu’ alta
         */

    public void dispose(){
        deck.dispose();
    }


}
