package com.mygdx.game.logic;

import com.mygdx.game.control.Game;
import com.mygdx.game.logic.card.Card;
import com.mygdx.game.logic.card.Dealer;
import com.mygdx.game.logic.player.Enemy;
import com.mygdx.game.logic.player.Human;
import com.mygdx.game.logic.player.Player;
import it.unical.mat.embasp.base.Handler;

import java.util.ArrayList;

public class LogicGame {

    private PokerListener listener;

    public void setPokerListener(PokerListener listener) {
        this.listener = listener;
    }

    int winner =-1;
    boolean blind=false;
    int cash=40;
    private int playerShift = 0;
    private int round = 1;
    static public int currentValue = 0;
    private int currentPlayerValue = 0;
    static final public int NUM_OF_PLAYERS = 4;
    static final public int NUM_OF_CARDS = 5;

    static public Dealer dealer;
    private ArrayList<Player> players;

    private Evaluator evaluator;

    private Game game;

    public LogicGame(Game game)
    {
        this.game = game;
        this.dealer = new Dealer();
        this.players = new ArrayList<>();
        this.evaluator = new Evaluator(players);
        createPlayers();
        setAllCardForAllPlayer();
    }

    public LogicGame(Game game, PokerListener pokerListener)
    {
        this.listener = pokerListener;
        this.game = game;
        this.dealer = new Dealer();
        this.players = new ArrayList<>();
        this.evaluator = new Evaluator(players);
        createPlayers();
        setAllCardForAllPlayer();
    }

    public void gameCicle(){

        allFold();

        if(!blind) {
            for (Player p : players)
                p.setMoney(p.getMoney() - 10);
            blind=true;
        }
        if(!game.isRoundFinished()){

            Player player = players.get(playerShift);

            // if the player hasn't folded and the current checked sum is less than the current value of the plate
            if(!player.isFold()) {
                // if it's in the round where the player can raise the sum
                if (round == 1 || round == 3) {
                    player.moveChoice(currentPlayerValue, currentValue);
                    //END
                }
                else if (round == 2) {
                        player.removeCardChoice(currentPlayerValue, currentValue);
                    }
            }
            else{
                increaseRound();
            }
        }
    }

    public void createPlayers()
    {
        players.add(new Enemy("LEFT_PLAYER", 4000, new PlayerListener()));
        players.add(new Enemy("UP_PLAYER", 4000, new PlayerListener()));
        players.add(new Enemy("RIGHT_PLAYER", 4000, new PlayerListener()));

        for(int i=0; i<3; i++){
            players.get(i).setName("ENEMY-" + i);
        }

        players.add(new Human("SONY", 4000,
                new PlayerListener(), new HumanListener()));
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


    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void increaseRound(){
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


                players.get(winner).setMoney(
                        players.get(evaluator.getWinner()).getMoney()
                                + cash);
                for(Player p:players){
                    p.setCurrentChecked(0);
                    p.setFold(false);
                }
                listener.finishRound(players.get(winner).getName());
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

    public void allFold(){
        int cont=0;
        for(Player p: players)
            if(p.isFold())
                cont++;
        if(cont==3){
            playerShift=3;
            round=3;
            increaseRound();
        }
    }


    public class PlayerListener implements Player.OnActionListerner {

        private Player player;

        private void setPlayer(){
            if(this.player == null)
                this.player = players.get(playerShift);
        }

        @Override
        public void checkPerform() {
            setPlayer();
            if(listener != null)
                listener.check(player);
            increaseRound();
        }

        @Override
        public void callPerform() {
            setPlayer();
            if(listener != null)
                listener.call(player);
            increaseRound();
        }

        @Override
        public void raisePerform(int money) {
            setPlayer();
            if(listener != null)
                listener.raise(player, money);
            increaseRound();
        }

        @Override
        public void foldPerform() {
            setPlayer();
            if(listener != null)
                listener.check(player);
            increaseRound();
        }

        @Override
        public void changeCard(int sizeRmv) {
            setPlayer();
            if(listener != null)
                listener.raise(player, sizeRmv);
            increaseRound();
        }
    }

    public class HumanListener implements Human.OnHumanListener{

        private Human human;

        public HumanListener() {
            if(players.get(playerShift) instanceof Human)
                this.human = (Human) players.get(playerShift);
            else
                throw new RuntimeException("Error to init HumanListener");
        }

        @Override
        public boolean humanCheck() {
            if(listener != null)
                return listener.humanCheck(human);
            return false;
        }

        @Override
        public boolean humanCall() {
            if(listener != null)
                return listener.humanCall(human);
            return false;
        }

        @Override
        public boolean humanRaise() {
            if(listener != null)
                return listener.humanRaise(human);
            return false;
        }

        @Override
        public boolean humanFold() {
            if(listener != null)
                return listener.humanFold(human);
            return false;
        }

        @Override
        public boolean humanPlus() {
            if(listener != null)
                return listener.humanPlus(human);
            return false;
        }

        @Override
        public boolean humanMinus() {
            if(listener != null)
                return listener.humanMinus(human);
            return false;
        }

        @Override
        public boolean left() {
            if(listener != null)
                return listener.left();
            return false;
        }

        @Override
        public boolean right() {
            if(listener != null)
                return listener.right();
            return false;
        }

        @Override
        public Card removeCard() {
            if(listener != null)
                return listener.removeCard(human);
            return null;
        }

        @Override
        public boolean endRemoveCard() {
            if(listener != null)
                return listener.endRemoveCard(human);
            return false;
        }
    }

    public interface PokerListener {

        void raise(Player player, int v);
        void check(Player player);
        void call(Player player);
        void fold(Player player);
        void changeCard(Player player, int numberOfRMVCard);

        // bitmapFont.draw(batch, ""+currentPlayerValue, 250, MyGdxGame.WORLD_HEIGHT/2f+15);
        //                    player.drawKeybord(batch);
        void printKeyboard(Human human, int currentValue);

        //cursor.intersectSprite(((Human) player).plus)
        boolean humanCheck(Human human);
        boolean humanCall (Human human);
        boolean humanRaise(Human human);
        boolean humanFold (Human human);
        boolean humanPlus (Human human);
        boolean humanMinus(Human human);

        void finishRound(String winner);
        boolean right();
        boolean left();

        Card removeCard(Human human);
        boolean endRemoveCard(Human human);

    }
}
