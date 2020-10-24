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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EnemyAI extends Player {

    private DLVManager dlvManager = new DLVManager();
    private OnPlateListener plateListener;


    public EnemyAI(String name, int money, OnActionListerner listener, OnPlateListener plateListener) {
        super(name, money, listener);
        this.plateListener = plateListener;
    }

    @Override
    public void moveChoice(int currentPlayerValue, int currentValue) {

    }

    @Override
    public void removeCardChoice(int currentPlayerValue, int currentValue) {

    }

    private class DLVManager {
        private List<String> oldTurn;

        private String encodingNormalRound;
        private String encodingChoiseRaise;
        private String encodingDiscardCardsRound;
        private String pathDlv;

        private ASPInputProgram choiceRaise;
        private ASPInputProgram discardCardsRound;

        public static final boolean CHOICERAISE = false;
        public static final boolean DISCARDCARDS = true;

        private InputProgram facts;


        {
            setPathResources();
            discardCardsRound = new ASPInputProgram();
            discardCardsRound.addProgram(encodingDiscardCardsRound);

            choiceRaise = new ASPInputProgram();
            choiceRaise.addProgram(encodingChoiseRaise);
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

        private Handler initHandlerWithEncoding(boolean isChoiceRaise) {
            Handler handler = new DesktopHandler(new DLV2DesktopService(pathDlv));
            handler.addProgram(isChoiceRaise == CHOICERAISE ? choiceRaise : discardCardsRound);
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

            int plate = plateListener.getSumPlate();
            //                for(int i=0;i<playerShift;i++)
            //                    if(!isFold())
            //                        plate+=currentValue;
            //                plate+=cash; //if I sum cash raiseChose.dlv goes in loop
            //probably problem: code in dlv || missing parameters in input like AVG

            facts = new ASPInputProgram();
            facts.addProgram("myWallet(" + getMoney() + ").");
            facts.addProgram("plate(" + plate + ").");
            facts.addProgram("currentValue(" + currentValue + ").");
            facts.addProgram(String.format("betsPointsAvg(%d).", 15));
            facts.addProgram(String.format("confidence(%d).", 8));

        }

        private void addWithOldFacts() {
            oldTurn.forEach(atom -> facts.addProgram(atom + "."));
        }

        private AnswerSets getOutput(Handler handler) throws Exception {
            Output o = handler.startSync();
            if (((AnswerSets) o).getAnswersets().size() == 0) {
                throw new Exception("NO ANSWERSET!");
            }
            return (AnswerSets) o;
        }

        // round 1 e round 3
        public void doAnAIChoise(int currentPlayerValue, int currentValue) {
            Handler handler = initHandlerWithEncoding(true);
            addFactsCards();
            AnswerSets answers = null;

            handler.addProgram(facts);

            // manage output choiceRaise
            try {
                answers = (AnswerSets) getOutput(handler);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            for (AnswerSet an : answers.getAnswersets()) {
                Pattern patternRaise = Pattern.compile("raise\\((\\d+)\\)");
                Matcher matcher;
                Integer raiseSum = null;
                boolean check = false;
                boolean fold = false;

                for (String atom : an.getAnswerSet()) {
                    //        System.out.println(atom);
                    matcher = patternRaise.matcher(atom);

                    if (matcher.find()) {
                        raiseSum = Integer.parseInt(matcher.group(1));
                    }
                    if (atom.matches("check")) {
                        check = true;
                    }
                    if (atom.matches("fold")) {
                        fold = true;
                    }
                }

                // if there is raise
                if (raiseSum != null) {
                    //System.out.println("currentValue = " + currentValue);

                    if (currentValue < raiseSum) {
                        currentValue = raiseSum;
                    }
                    setCurrentChecked(currentValue);
                    raise(currentValue);
                } else if (check) {
                    setCurrentChecked(currentValue);
                    check();
                } else if (fold) {
                    fold();
                }
            }

            oldTurn.clear();
            for (AnswerSet an : answers.getAnswersets()) {
                oldTurn.addAll(an.getAnswerSet());
            }
        }


        // round 2
        public void doAnAICardRemove(int currentPlayerValue, int currentValue) {
            Handler handler = initHandlerWithEncoding(false);
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

    public interface OnPlateListener {
        int getSumPlate();
    }
}