package com.mygdx.game.logic.player;

import com.mygdx.game.control.Game;
import com.mygdx.game.logic.card.Card;
import com.mygdx.game.logic.card.Suite;
import it.unical.mat.embasp.base.Handler;
import it.unical.mat.embasp.base.InputProgram;
import it.unical.mat.embasp.base.Output;
import it.unical.mat.embasp.languages.asp.ASPInputProgram;
import it.unical.mat.embasp.languages.asp.AnswerSet;
import it.unical.mat.embasp.languages.asp.AnswerSets;
import it.unical.mat.embasp.platforms.desktop.DesktopHandler;
import it.unical.mat.embasp.specializations.dlv2.desktop.DLV2DesktopService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EnemyAI extends Player {

    private DLVManager dlvManager = new DLVManager();
    private OnIntelligenceListener aiListener;

    private int round = 0;
    private boolean isBluff = false;
    protected int numberGame = 0;

    private List<PlayerInfo> players;

    static int RANDOM_SEED = 1;
    int randomSeed = RANDOM_SEED;

    public EnemyAI(String name, int money) { super(name, money);
        randomSeed = (RANDOM_SEED *= 2);
    }

    @Override
    public void newGame(int initValue) {
        super.newGame(initValue);
        numberGame++;
        round = 0;
        isBluff=false;
        if(numberGame > 1) {
            countAboutLastGame();
        }
    }

    private void countAboutLastGame() {
        createArrayPlayers();

        PlayerInfo winner = players.get(aiListener.getIndexPlayerWinner());
        winner.gameWin++;
        winner.pointsWin += aiListener.getPointWinnerPlayer();
        winner.moneyWin  += winner.actuallyMoneyBet;

        for(PlayerInfo playerInfo:players){
            playerInfo.moneyBet += playerInfo.actuallyMoneyBet;
            playerInfo.actuallyMoneyBet = 0;
            playerInfo.avgMoneyBet = playerInfo.moneyBet/(double)numberGame;
        }
    }

    private void createArrayPlayers() {
        if(players == null){
            players = new ArrayList<>();
            for(int i = 0; i < aiListener.getSizePlayers(); i++) {
                players.add(new PlayerInfo());
            }
        }
    }

    @Override
    public void move(int currentValue) {

        countAboutShift();

        if(round == 0)
            dlvManager.doAnAIChoiceRoundOne(currentPlayerValue, currentValue);
        else
            dlvManager.doAnAIChoiceRoundThree(currentPlayerValue, currentValue);
    }

    private void countAboutShift() {
        createArrayPlayers();
        for(int i=0; i<players.size(); i++){
            players.get(i).actuallyMoneyBet = aiListener.getMoneyBet(i);
            players.get(i).actuallyMoneyBetRound =
                    aiListener.getMoneyBetOnRound(i);
        }
    }

    @Override
    public void removeCard() {
        round = 1;
        dlvManager.doAnAICardRemove(currentPlayerValue);
    }


    private class DLVManager {

        private String pathDlv;

        private String encodingBluff    ;
        private String encodingProfiling;
        private String encodingSemiScore;
        private String encodingNormalRound;
        private String encodingChoiceRaise;
        private String encodingChoiceRaise2;
        private String encodingDiscardCardsRound;

        private ASPInputProgram bluff;
        private ASPInputProgram choiceRaise;
        private ASPInputProgram choiceRaise2;
        private ASPInputProgram discardCardsRound;

        public static final int BLUFF         = 0;
        public static final int CHOICERAISE   = 1;
        public static final int CHOICERAISE2  = 2;
        public static final int DISCARDCARDS  = 3;

        private InputProgram facts;

        {
            setPathResources();

            choiceRaise = new ASPInputProgram();
            choiceRaise.addFilesPath(encodingNormalRound);
            choiceRaise.addFilesPath(encodingChoiceRaise);
            choiceRaise.addFilesPath(encodingProfiling);
            choiceRaise.addFilesPath(encodingSemiScore);

            discardCardsRound = new ASPInputProgram();
            discardCardsRound.addFilesPath(encodingNormalRound);
            discardCardsRound.addFilesPath(encodingSemiScore);
            discardCardsRound.addFilesPath(encodingDiscardCardsRound);

            choiceRaise2 = new ASPInputProgram();
            choiceRaise2.addFilesPath(encodingNormalRound);
            choiceRaise2.addFilesPath(encodingChoiceRaise2);
            choiceRaise2.addFilesPath(encodingProfiling);

            bluff = new ASPInputProgram();
            bluff.addFilesPath(encodingBluff);
        }

        private void setPathResources() {
            if (System.getProperty("os.name").contains("Linux")) {
                pathDlv =
                        (new File(Game.class.getResource("/").toString()).getParentFile().getParentFile().getParent()
                                + "/resources/main/dlv2").substring(5);
                encodingNormalRound = "core/assets/encodings/normalRound.dlv";
                encodingDiscardCardsRound = "core/assets/encodings/discardCardsRound.dlv";
                encodingChoiceRaise = "core/assets/encodings/choiceRaise.dlv";
                encodingChoiceRaise2 = "core/assets/encodings/choiceRaise2.dlv";
                encodingBluff = "core/assets/encodings/bluff.dlv";
                encodingProfiling = "core/assets/encodings/profiling.dlv";
                encodingSemiScore = "core/assets/encodings/semiScore.dlv";
            } else {
                pathDlv = "./desktop/build/resources/main/dlv2.win.x64_5";
                encodingNormalRound = "./desktop/build/resources/main/encodings/normalRound.dlv";
                encodingDiscardCardsRound = "./desktop/build/resources/main/encodings/discardCardsRound.dlv";
                encodingChoiceRaise = "./desktop/build/resources/main/encodings/choiceRaise.dlv";
                encodingChoiceRaise2 = "./desktop/build/resources/main/encodings/choiceRaise2.dlv";
                encodingBluff = "./desktop/build/resources/main/encodings/bluff.dlv";
                encodingProfiling = "./desktop/build/resources/main/encodings/profiling.dlv";
                encodingSemiScore = "./desktop/build/resources/main/encodings/semiScore.dlv";
            }
        }

        private Handler initHandlerWithEncoding(int typeRound) {
            Handler handler = new DesktopHandler(new DLV2DesktopService(pathDlv));

            // handler.addProgram(domain);

            switch (typeRound){
                case BLUFF:
                    handler.addProgram(bluff);
                    break;
                case CHOICERAISE:
                    handler.addProgram(choiceRaise);
                    break;
                case CHOICERAISE2:
                    handler.addProgram(choiceRaise2);
                    break;
                case DISCARDCARDS:
                    handler.addProgram(discardCardsRound);
                    break;
            }

            return handler;
        }

        private void addFactsCards() {
            try {
                for (int i = 0; i < 5; i++) {
                    facts.addObjectInput(new Card(cards.get(i).suite, cards.get(i).number));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void addFactsMoney(int currentValue) {

            int plate = aiListener.getSumPlate();

            facts.addProgram(String.format("myWallet(%d).", getMoney()));
            facts.addProgram(String.format("plate(%d).", plate));
            facts.addProgram(String.format("currentValue(%d).", currentValue));
//            facts.addProgram(String.format("confidence(%d).", 8));
            facts.addProgram(String.format("currentChecked(%d).", currentChecked));

        }

        private void addFactsProfilesPlayers(){
            for(int i=0; i<players.size(); i++){
                if(!aiListener.isFold(i)){
                    addFactProfilePlayer(players.get(i), i);
                }
            }
        }

        private void addFactProfilePlayer(PlayerInfo playerInfo, int index){
            int moneyBluff = (int) (playerInfo.avgMoneyBet * 1.5);
            int estimatedPoints = playerInfo.actuallyMoneyBet
                    *(playerInfo.moneyWin == 0?0:
                            playerInfo.pointsWin/playerInfo.moneyWin
            );
            
            facts.addProgram(String.format("avgMoneyBet     (%d, %d).", index, (int)playerInfo.avgMoneyBet));
            facts.addProgram(String.format("gameWin         (%d, %d).", index, playerInfo.gameWin));
            facts.addProgram(String.format("pointsWin       (%d, %d).", index, playerInfo.pointsWin));
            facts.addProgram(String.format("moneyBet        (%d, %d).", index, playerInfo.moneyBet));
            facts.addProgram(String.format("moneyWin        (%d, %d).", index, playerInfo.moneyWin));
            facts.addProgram(String.format("actuallyMoneyBet(%d, %d).", index, playerInfo.actuallyMoneyBet));
            facts.addProgram(String.format("actuallyMoneyBetRound(%d, %d)."
                    , index, playerInfo.actuallyMoneyBetRound));

            facts.addProgram(String.format("moneyBluff      (%d, %d).", index, moneyBluff));
            if(playerInfo.moneyWin != 0)
                facts.addProgram(String.format("estimatedPoints (%d, %d).", index, estimatedPoints));
            facts.addProgram(String.format("game      (%d).", numberGame));
        }

        private AnswerSets getOutput(Handler handler) throws Exception {
            Output o = handler.startSync();
            try {
                for(int i=0; ; i++){
                    System.out.println("input dlv = " + handler.getInputProgram(i).getPrograms());
                }}catch (Exception ignore){}
            if (((AnswerSets) o).getAnswersets().size() == 0) {
                fold();
                throw new Exception("NO ANSWERSET!");
            }
            for (AnswerSet an : ((AnswerSets) o).getAnswersets()) {
                System.out.println("output = " + an.getAnswerSet());
            }
            return (AnswerSets) o;
        }

        public void doAnAIChoiceRoundOne(int currentPlayerValue, int currentValue){
            Random r = new Random(System.currentTimeMillis() + randomSeed);
            if(r.nextInt(10) == 0 || isBluff) {
                isBluff=true;
                doAnAIChoise(currentPlayerValue, currentValue, BLUFF);
            }else
                doAnAIChoise(currentPlayerValue, currentValue, CHOICERAISE);
        }

        public void doAnAIChoiceRoundThree(int currentPlayerValue, int currentValue){
            System.out.println("isBluff = " + isBluff);
            if(!isBluff)
                doAnAIChoise(currentPlayerValue, currentValue, CHOICERAISE2);
            else
                doAnAIChoise(currentPlayerValue, currentValue, BLUFF);
        }


        // round 1 e round 3
        public void doAnAIChoise(int currentPlayerValue, int currentValue, int typeOfRound) {
            Handler handler = initHandlerWithEncoding(typeOfRound);
            facts = new ASPInputProgram();

            addFactsCards();
            addFactsMoney(currentValue);
            addFactsProfilesPlayers();

            handler.addProgram(facts);

            AnswerSets answers;
            try {
                answers = getOutput(handler);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }


            boolean doSomething = false;
            for (AnswerSet an : answers.getAnswersets()) {
                Pattern patternRaise = Pattern.compile("raise\\((\\d+)\\)");
                Matcher matcher;
                Integer raiseSum = null;
                if(isBluff)
                    System.out.println("BLUFF");;
                for (String atom : an.getAnswerSet()) {
                    //        System.out.println(atom);
                    matcher = patternRaise.matcher(atom);

                    if (matcher.find()) {
                        raiseSum = Integer.parseInt(matcher.group(1));
                        if(isBluff){
                            Random r = new Random();
                            int tmp = r.nextInt(1000);
                            raiseSum = raiseSum + (tmp - (tmp%10));
                        }
                        raise(raiseSum, currentValue);
                        doSomething = true;
                    }
                    if (atom.matches("check")) {
                        check(currentValue);
                        doSomething = true;
                    }
                    else if(atom.matches("fold")){
                        fold();
                        doSomething = true;
                    }
                }

            }
            if(!doSomething){
                fold();
            }
        }

        public void doAnAICardRemove(int currentPlayerValue) {
            Handler handler = initHandlerWithEncoding(DISCARDCARDS);
            facts = new ASPInputProgram();

            addFactsCards();

            handler.addProgram(facts);

            AnswerSets answers = null;
            try {
                answers = getOutput(handler);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            Pattern patternRaise = Pattern.compile("discard\\(\"(\\w+)\",(\\d+)\\)");
            Matcher matcher;

            for (AnswerSet a : answers.getAnswersets()) {
                for (String atom : a.getAnswerSet()) {
                    matcher = patternRaise.matcher(atom);
                    String str = "";
                    Suite s = Suite.DIAMONDS;
                    if (matcher.find()) {
                        str = matcher.group(1);
                        if (str.contains("DIAMONDS"))
                            s = Suite.DIAMONDS;
                        else if (str.contains("HEARTS"))
                            s = Suite.HEARTS;
                        else if (str.contains("SPADES"))
                            s = Suite.SPADES;
                        else if (str.contains("CLUBS"))
                            s = Suite.CLUBS;
                        Card card = new Card((s), Integer.parseInt(matcher.group(2)));
                        if(!isBluff)
                            rmv.add(card);
                    }
                }
            }
            changeCards();
        }
    }

    public void setOnIntelligenceListener(OnIntelligenceListener listener) {
        this.aiListener = listener;
    }

    public interface OnIntelligenceListener {
        int getSumPlate();
        int getSizePlayers();

        boolean isFold(int index);
        int getMoneyBet(int index);
        int getMoneyBetOnRound(int index);

        int getIndexPlayerWinner();
        int getPointWinnerPlayer();
    }
}