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
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EnemyAI extends Player {

    private DLVManager dlvManager = new DLVManager();
    private OnIntelligenceListener aiListener;

    public EnemyAI(String name, int money) {
        super(name, money);
    }

    @Override
    public void move(int currentValue) {
        dlvManager.doAnAIChoise(currentPlayerValue, currentValue);
    }

    @Override
    public void removeCard() {
        dlvManager.doAnAICardRemove(currentPlayerValue);
    }

    private class DLVManager {
        private List<String> oldTurn;

        private String encodingNormalRound;
        private String encodingChoiseRaise;
        private String encodingDiscardCardsRound;
        private String pathDlv;

        private ASPInputProgram normalRound;
        private ASPInputProgram choiceRaise;
        private ASPInputProgram discardCardsRound;

        public static final int NORMALROUND  = 0;
        public static final int CHOICERAISE  = 1;
        public static final int DISCARDCARDS = 2;

        private InputProgram facts;

        {
            setPathResources();
            discardCardsRound = new ASPInputProgram();
            discardCardsRound.addFilesPath(encodingDiscardCardsRound);

            choiceRaise = new ASPInputProgram();
            choiceRaise.addFilesPath(encodingChoiseRaise);

            normalRound = new ASPInputProgram();
            normalRound.addFilesPath(encodingNormalRound);

            oldTurn = new ArrayList<>();
        }

        private void setPathResources() {
            if (System.getProperty("os.name").contains("Linux")) {
                pathDlv =
                        (new File(Game.class.getResource("/").toString()).getParentFile().getParentFile().getParent()
                                + "/resources/main/dlv2").substring(5);
                encodingNormalRound = "core/assets/encodings/normalRound.dlv";
                encodingDiscardCardsRound = "core/assets/encodings/discardCardsRound.dlv";
                encodingChoiseRaise = "core/assets/encodings/choiceRaise2.dlv";
            } else {
                pathDlv = "./desktop/build/resources/main/dlv2.win.x64_5";
                encodingNormalRound = "./desktop/build/resources/main/encodings/normalRound.dlv";
                encodingDiscardCardsRound = "./desktop/build/resources/main/encodings/discardCardsRound.dlv";
                encodingChoiseRaise = "./desktop/build/resources/main/encodings/choiceRaise2.dlv";
            }
        }

        private Handler initHandlerWithEncoding(int typeRound) {
            Handler handler = new DesktopHandler(new DLV2DesktopService(pathDlv));

            switch (typeRound){
                case NORMALROUND:
                    handler.addProgram(normalRound);
                    break;
                case CHOICERAISE:
                    handler.addProgram(choiceRaise);
                    break;
                case DISCARDCARDS:
                    handler.addProgram(discardCardsRound);
                    break;
            }

            return handler;
        }

        private void addFactsCards() {
            facts = new ASPInputProgram();
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

            facts = new ASPInputProgram();
            facts.addProgram("myWallet(" + getMoney() + ").");
            facts.addProgram("plate(" + plate + ").");
            facts.addProgram("currentValue(" + currentValue + ").");
            facts.addProgram(String.format("betsPointsAvg(%d).", 15));
            facts.addProgram(String.format("confidence(%d).", 8));
            facts.addProgram(String.format("currentChecked(%d).", currentChecked));

        }

        private void addWithOldFacts() {
            oldTurn.forEach(new Consumer<String>() {
                @Override
                public void accept(String s) {
                    facts.addProgram(s + ".");
                }
            });
        }

        private void addOldAnswerSets(AnswerSets answers){
            for (AnswerSet an : answers.getAnswersets()) {
                for (String atom : an.getAnswerSet()) {
                    facts.addProgram(atom + ".");
                }
                break;
            }
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

        // round 1 e round 3
        public void doAnAIChoise(int currentPlayerValue, int currentValue) {
            Handler handler = initHandlerWithEncoding(NORMALROUND);
            addFactsCards();

            handler.addProgram(facts);

            // manage output choiceRaise
            AnswerSets answers = null;
            try {
                answers = getOutput(handler);
                System.out.println("output dlv = " + answers.getAnswerSetsString());
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            oldTurn.clear();
            for (AnswerSet an : answers.getAnswersets()) {
                oldTurn.addAll(an.getAnswerSet());
            }

            handler = initHandlerWithEncoding(CHOICERAISE);
            addFactsMoney(currentValue);
            addWithOldFacts();
            handler.addProgram(facts);

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


                for (String atom : an.getAnswerSet()) {
                    //        System.out.println(atom);
                    matcher = patternRaise.matcher(atom);

                    if (matcher.find()) {
                        raiseSum = Integer.parseInt(matcher.group(1));
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


        // round 2
        public void doAnAICardRemove(int currentPlayerValue) {
            Handler handler = initHandlerWithEncoding(DISCARDCARDS);
            facts = new InputProgram();
            addWithOldFacts();
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
        // other data
    }
}