package com.mygdx.game.logic;

import com.mygdx.game.control.Game;
import com.mygdx.game.logic.card.Card;
import com.mygdx.game.logic.card.Dealer;
import com.mygdx.game.logic.player.Enemy;
import com.mygdx.game.logic.player.EnemyAI;
import com.mygdx.game.logic.player.Human;
import com.mygdx.game.logic.player.Player;
import it.unical.mat.embasp.base.Handler;

import java.util.ArrayList;

public class LogicGame {

    private static final int START_VALUE = 20;
    private PokerListener listener;

    public void setPokerListener(PokerListener listener) {
        this.listener = listener;
    }

    int winner =-1;
    boolean blind=false;
    private int playerShift = 0;
    private int round = 1;
    static public int currentValue = 0;
    static final public int NUM_OF_PLAYERS = 4;
    static final public int NUM_OF_CARDS = 5;

    private Dealer dealer;
    private ArrayList<Player> players;

    private Evaluator evaluator;
    private Plate plate;

    private Game game;

    public LogicGame(Game game)
    {
        this.game = game;
        this.dealer = new Dealer();
        this.players = new ArrayList<>();
        this.evaluator = new Evaluator(players);
        this.plate = new Plate();
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
                    player.moveChoice(currentValue);
                    //END
                }
                else if (round == 2) {
                    player.removeCardChoice();
                }
            }
            else{
                increaseRound();
            }
        }
    }

    public void createPlayers()
    {
//        for(int i=0; i<2; i++)
//        players.add(new Enemy("LEFT_PLAYER", 4000));

        currentValue = START_VALUE;

        for(int i=0; i<3; i++){
            EnemyAI enemyAI = new EnemyAI("RIGHT_PLAYER", 4000);
            enemyAI.setOnIntelligenceListener(new EnemyAI.OnIntelligenceListener() {
                @Override
                public int getSumPlate() {
                    return plate.getCash();
                }
            });

            players.add(enemyAI);
        }

        for(int i=0; i<3; i++){
            players.get(i).setName("ENEMY-" + i);
            players.get(i).setOnActionListerner(new PlayerListener());
        }

        // -----------------------------------------------

        Human human = new Human("SONY", 4000);
        human.setOnActionListerner  (new PlayerListener());
        human.setOnHumanListener    (new HumanListener(human));

        players.add(human);

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

    public boolean satisfiedPlate(){
        for(Player player:players){
            int cc = player.getCurrentChecked();
            if(!player.isFold() && cc != currentValue)
                return false;
        }
        return true;
    }

    public void increaseRound(){
        System.out.println("Player: " + players.get(playerShift).getName() +" --ROUND--> "+round);
        playerShift++;
        if(playerShift==4){
            playerShift=0;

            if(round != 2 && satisfiedPlate()) {
                currentValue = START_VALUE;
                for (Player p : players) {
                    p.newRound(START_VALUE);
                }
                round++;
            }else if (round==2){
                round++;
            }
            if(round==4){
                winner = evaluator.getWinner();
                int moneyWinner = players.get(winner).getMoney();

                 players.get(winner).setMoney(
                        moneyWinner
                                + plate.getCash());
                for(Player p:players){
                    p.newGame(START_VALUE);
                }
                listener.finishRound(players.get(winner).getName());

                dealer.shuffle();
                plate.clear();

                playerShift=0;
                round=1;
                blind=false;
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

    public Plate getPlate() {
        return plate;
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
            plate.increase(currentValue);
            if(listener != null)
                listener.check(player);
            increaseRound();
        }

        @Override
        public void callPerform() {
            setPlayer();
            plate.increase(currentValue);
            if(listener != null)
                listener.call(player);
            increaseRound();
        }

        @Override
        public void raisePerform(int money) {
            setPlayer();
            currentValue += money;
            plate.increase(money+currentValue);
            if(listener != null)
                listener.raise(player, money);
            increaseRound();
        }

        @Override
        public void foldPerform() {
            setPlayer();
            if(listener != null)
                listener.fold(player);
            increaseRound();
        }

        @Override
        public void changeCard(int sizeRmv) {
            setPlayer();
            if(listener != null)
                listener.changeCard(player, sizeRmv);

            for(int i=0; i<sizeRmv; i++){
                player.addCard(dealer.getCard());
            }
            increaseRound();
        }
    }

    public class HumanListener implements Human.OnHumanListener{

        private Human human;

        public HumanListener(Human human) {
            this.human = human;
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

        @Override
        public void performKeyboard(int value) {
            listener.printKeyboard(human, value);
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
