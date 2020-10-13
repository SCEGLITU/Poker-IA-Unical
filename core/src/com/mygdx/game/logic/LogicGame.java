package com.mygdx.game.logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.graphicsGDX.card.Deck;
import com.mygdx.game.graphicsGDX.player.PlayerDirection;
import com.mygdx.game.logic.card.Card;
import com.mygdx.game.logic.card.Dealer;
import com.mygdx.game.logic.player.Enemy;
import com.mygdx.game.logic.player.Human;
import com.mygdx.game.logic.player.Player;
import it.unical.mat.embasp.base.Handler;

import java.util.ArrayList;

public class LogicGame {

    int winner =-1;
    boolean blind=false;
    int cash=40;
    private int playerShift = 0;
    private int round = 1;
    private ArrayList<ArrayList<String>> oldTurn =
            new ArrayList<ArrayList<String> >();
    static public int currentValue = 0;
    private int currentPlayerValue = 0;

    static final public int UP_PLAYER = 0;
    static final public int LEFT_PLAYER = 1;
    static final public int RIGHT_PLAYER = 2;
    static final public int DOWN_PLAYER = 3;
    static final public int NUM_OF_PLAYERS = 4;
    static final public int NUM_OF_CARDS = 5;

    static public Dealer dealer;
    private ArrayList<Player> players;

    private boolean isRoundFinished = false;
    private ArrayList<Card> rmve;

    private static Handler handler;
    private Evaluator evaluator;

    public LogicGame()
    {
        this.dealer = new Dealer();
        this.players = new ArrayList<>();
        this.rmve = new ArrayList<>();
        this.evaluator = new Evaluator(players);
        createPlayers();
        setAllCardForAllPlayer();
        oldTurn.add(null);oldTurn.add(null);oldTurn.add(null);
    }

    public void gameCicle(){

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
                    bitmapFont.draw(batch, ""+currentPlayerValue, 250, MyGdxGame.WORLD_HEIGHT/2f+15);
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
                        Rectangle rectangleCard = Deck.getInstance().getCard(card).getBoundingRectangle();
                        if(rectangleCard.contains(cursor.getX(), cursor.getY()))
                            if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && (!(rmve.contains(card)))) {
                                rmve.add(card);
                                System.out.println("remove this card");
                            }
                    }
                    if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                        changeCardforPlayer(playerShift,rmve);
                        printChangeCard(rmve.size(), playerShift, batch);
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
                    moveByAI(playerShift,batch); //scarta carte
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
                Rectangle rectangleCard = Deck.getInstance().getCard(card).getBoundingRectangle();
                //if(rectangleCard.contains(cursor.getX(), cursor.getY()))
                //System.out.println("Carta giocatore in basso: " + card.getSuite() + " " + card.getNumber());

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

    public void setAllCardForAllPlayer(){
        dealer.shuffle();
        for(int i = 0; i < NUM_OF_PLAYERS; i++) {
            players.get(i).reset();
            for (int j = 0; j < NUM_OF_CARDS; j++) {
                players.get(i).addCard(dealer.getCard());
            }
        }
    }

    public void changeCardforPlayer(int cpuindex, ArrayList<Card> cardposition){
        for (int i = 0; i < cardposition.size(); i++) {
            if(!players.get(cpuindex).removeCard(cardposition.get(i)))
                throw new RuntimeException("Remove a no-held card " + cpuindex);
        }
        for(int i=0;i<cardposition.size();i++)
            players.get(cpuindex).addCard(dealer.getCard());
        //:'D
    }

    public boolean isRoundFinished() {
        return isRoundFinished;
    }

    public ArrayList<Player> getPlayers() {
        return players;
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
//                oldTurn.clear();
                setAllCardForAllPlayer();
            }
        }
    }
}
