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



public class Game {

    private BitmapFont bitmapFont = new BitmapFont();
    Sprite finish;
    private Cursor cursor;
    int winner =-1;
    boolean blind=false;
    int cash=40;
    private int playerShift = 0;
    private int round = 1;

    static public int currentValue = 0;
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
            encodingChoiseRaise = "core/assets/encodings/choiceRaise2.dlv";
        }else
        {
            pathDlv = "./desktop/build/resources/main/dlv2.win.x64_5";
            encodingNormalRound = "./desktop/build/resources/main/encodings/normalRound.dlv";
            encodingDiscardCardsRound = "./desktop/build/resources/main/encodings/discardCardsRound.dlv";
            encodingChoiseRaise = "./desktop/build/resources/main/encodings/choiceRaise2.dlv";
        }
    }

    public void moveByAI(int cpuIndex,Batch batch) {

        handler = new DesktopHandler(new DLV2DesktopService(pathDlv));

//        add facts to program
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

//        add encoding according to the round to the program
        InputProgram encoding = new ASPInputProgram();
        if(round==2)
            encoding.addFilesPath(encodingDiscardCardsRound);
        else
            encoding.addFilesPath(encodingNormalRound);
        handler.addProgram(encoding);

//        catch output of the program
        Output o = handler.startSync();
        AnswerSets answers = (AnswerSets) o;

//        if is empty print "no answersets", it's better a RunTimeException
        if (answers.getAnswersets().size() == 0) {
            throw new RuntimeException("NO ANSWERSET!");
        }
        else
        {

            System.out.println("Player: "+ cpuIndex);
            //System.out.println("NORMALROUND FACTS: "+facts.getPrograms());

            if(round!=2){

                // to value the next move, there is another ASP program called "choiceRaise.dlv"

                //Insert all facts for round 1-3 in choiseRaise.dlv
                encoding = new ASPInputProgram();
                handler = new DesktopHandler(new DLV2DesktopService(pathDlv));

                encoding.addFilesPath(encodingChoiseRaise);

                facts = new ASPInputProgram();




                facts.addProgram("myWallet("+ players.get(cpuIndex).getMoney()+").");
                int plate=0;
                for(int i=0;i<playerShift;i++)
                    if(!players.get(i).isFold())
                        plate+=currentValue;
                plate+=cash; //if I sum cash raiseChose.dlv goes in loop
                //probably problem: code in dlv || missing parameters in input like AVG
                facts.addProgram("plate("+ plate+").");
                facts.addProgram("currentValue("+currentValue+").");
                facts.addProgram(String.format("betsPointsAvg(%d).",15));
                facts.addProgram(String.format("confidence(%d).", 8));

                for (AnswerSet an : answers.getAnswersets()) {
                    for (String atom : an.getAnswerSet()) {
                        facts.addProgram(atom + ".");
                    }
                }

                handler.addProgram(facts);
                handler.addProgram(encoding);
                o = handler.startSync();

                // manage output choiceRaise
                answers = (AnswerSets) o;
                if (answers.getAnswersets().size() == 0) {
                    //throw new RuntimeException("NO ANSWERSET!");
                    System.out.println("CHOICERAISE FACTS "+facts.getPrograms());
                }
                else{
                    //System.out.println("CHOICERAISE FACTS "+facts.getPrograms());
                    for(AnswerSet an: answers.getAnswersets())
                    {
                        Pattern patternRaise=Pattern.compile("raise\\((\\d+)\\)");
                        Matcher matcher;
                        Integer raiseSum = null;
                        boolean check=false;
                        boolean fold = false;

                        for(String atom:an.getAnswerSet()) {
                            System.out.println(atom);
                            matcher=patternRaise.matcher(atom);

                            if(matcher.find()){
                                raiseSum=Integer.parseInt(matcher.group(1));
                            }
                            if(atom.matches("check")){
                                check=true;
                            }
                            if(atom.matches("fold")){
                                fold=true;
                            }
                        }

                        // if there is raise
                        if(raiseSum!=null) {
                            System.out.println("currentValue = " + currentValue);

                            if(currentValue < raiseSum) {

                                System.out.println("DLV RAISE. I'll do raise 'cause CURRENT VALUE:"+currentValue+"< "+raiseSum);
                                currentValue = raiseSum;
                                currentPlayerValue = currentValue;

                                players.get(playerShift).setCurrentChecked(currentValue);


                                if (playerShift != 0)
                                    playerShift = -1;
                            }
                            else{
                                players.get(playerShift).setCurrentChecked(currentValue);
                            }
                                System.out.println("if there is an error in DLV code and " +
                                        "I'm trying to raise a raise<currentValue, then check");
                            //printRaise(currentValue,playerShift,batch);
                        }
                        else if(check){
                            players.get(playerShift).setCurrentChecked(currentValue);
                            System.out.println("DLV CHECK");
                        }
                        else if(fold){
                            players.get(playerShift).setFold(true);
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
        players.add(new Enemy(PlayerDirection.getPlayerDirection(LEFT_PLAYER)));
        players.add(new Enemy(PlayerDirection.getPlayerDirection(UP_PLAYER)));
        players.add(new Enemy(PlayerDirection.getPlayerDirection(RIGHT_PLAYER)));

        for(int i=0; i<3; i++){
            players.get(i).setName("ENEMY-" + i);
        }

        players.add(new Human(PlayerDirection.getPlayerDirection(NUM_OF_PLAYERS - 1)));
    }

    private void initCursor() {
        cursor = new Cursor();
    }

    private void initButton() {
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

    static int g = 0;

    public void draw(Batch batch){

        for(Player player:players) {
            if(player.isFold()==false)
                player.draw(batch);
           bitmapFont.draw(batch,"" + player.getName() + "\nm: " + player.getMoney(),
                   player.getUpgradeX(), player.getUpgradeY());
        }

        gameCicle(batch);
        if(g<=4){
            System.out.println("Game Cicle");
        }

        g++;
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
        if(!blind) {
            for (Player p : players)
                p.setMoney(p.getMoney() - 10);
            blind=true;
        }
        allFold(batch);
        if(!isRoundFinished){

            Player player = players.get(playerShift);

            // if the player hasn't folded and the current checked sum is less than the current value of the plate
            if(!player.isFold()) {

                // if it's in the round where the player can raise the sum
                if ((round == 1 || round == 3) && player instanceof Human) {
                    bitmapFont.draw(batch, ""+currentPlayerValue, 250,MyGdxGame.WORLD_HEIGHT/2f+15);
                    player.drawKeybord(batch);

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
                                cursor.setY(Gdx.graphics.getHeight() - MyGdxGame.CARD_HEIGHT -
                                        deck.getCard(player.getCard((count) % sizeCards)).getY() + 5);
                                setFirstPos = false;
                                break;
                            }
                            count++;
                        }

                        if (setFirstPos) {
                            cursor.setX(deck.getCard(player.getCard(0)).getX() + 5);
                            cursor.setY(Gdx.graphics.getHeight() - MyGdxGame.CARD_HEIGHT -
                                    (deck.getCard(player.getCard(0)).getY()) + 5);
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
                    increaseRound(batch);
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
        if (currentValue==0)
            printNotify(playerShift, "CHECK", batch);
        else
            printNotify(playerShift, "CALL", batch);

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
                    p.setFold(false);
                }
                isRoundFinished = true;
                dealer.shuffle();
                cash=40;
                playerShift=0;
                round=1;
                blind=false;
                currentValue=0;
                setAllCardForAllPlayer();
            }
        }
    }

    private void win(Batch batch) {
        winner = evaluator.valueCards();
        bitmapFont.draw(batch, "VINCE: " + winner,
                MyGdxGame.WORLD_WIDTH/2, MyGdxGame.WORLD_HEIGHT/2);
        printIn = true;
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

    public void allFold(Batch batch){
        int cont=0;
        for(Player p: players)
            if(p.isFold())
                cont++;
        if(cont==3){
            playerShift=3;
            round=3;
            increaseRound(batch);
        }
    }
    public void dispose(){
        deck.dispose();
    }


}
