package com.mygdx.game.logic.dlv;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.mygdx.game.control.Game;
import com.mygdx.game.logic.Evaluator;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DLVManager {
    private String encodingNormalRound;
    private String encodingDiscardCardsRound;
    private String encodingChoiseRaise;
    private String pathDlv;

    private static Handler handler;
    private Evaluator evaluator;

    InputProgram facts;

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

    public void moveByAI(int cpuIndex, Batch batch) {

        if (playerShift == -1)
            return;

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

        if (round == 2)
            for(String atom: oldTurn.get(playerShift)) {
                facts.addProgram(atom + ".");
            }
//        catch output of the program
        Output o = handler.startSync();
        AnswerSets answers = (AnswerSets) o;

//        if is empty print "no answersets", it's better a RunTimeException
        if (answers.getAnswersets().size() == 0) {
            throw new RuntimeException("NO ANSWERSET!");
        }
        else if(round!=2){

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
                        //        System.out.println(atom);
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
                        //System.out.println("currentValue = " + currentValue);

                        if(currentValue < raiseSum) {

                            //System.out.println("DLV RAISE. I'll do raise 'cause CURRENT VALUE:"+currentValue+"< "+raiseSum);
                            currentValue = raiseSum;
                            currentPlayerValue = currentValue;

                            players.get(playerShift).setCurrentChecked(currentValue);


                            if (playerShift != 0){
                                playerShift = -1;
                                return;
                            }
                        }
                        else{
                            players.get(playerShift).setCurrentChecked(currentValue);
                        }
                        // System.out.println("if there is an error in DLV code and " +
                        //   "I'm trying to raise a raise<currentValue, then check");
                        printRaise(currentValue,playerShift,batch);
                    }
                    else if(check){
                        players.get(playerShift).setCurrentChecked(currentValue);
                        printCheck(playerShift, batch);
                        System.out.println("DLV CHECK");
                    }
                    else if(fold){
                        players.get(playerShift).setFold(true);
                        printFold(playerShift, batch);
                        System.out.println("DLV FOLD");
                    }
                }
                ArrayList<String> a1 = new ArrayList<String>();
                for (AnswerSet an : answers.getAnswersets()) {
                    for (String atom : an.getAnswerSet()) {
                        a1.add(atom);
                        if(atom.matches("card*") && playerShift == 2)
                            System.out.println("CARTE OLD 2: " + atom);
                    }
                }

                System.out.println("size: " + oldTurn.size());
                System.out.println("playerShift = " + playerShift);

                oldTurn.set(playerShift,a1);
            }
        }
        //discard cards ROUND 2
        else {
            ArrayList rmv = new ArrayList<Card>();
            Pattern patternRaise=Pattern.compile("discard\\(\"(\\w+)\",(\\d+)\\)");
            Matcher matcher;
            if (answers.getAnswersets().size() == 0) {
                System.out.println("NO ANSWERSET!");
            }
            else {
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
                            Card card=new Card((s), Integer.parseInt(matcher.group(2)));
                            if(playerShift == 2)
                                System.out.println("ciao "+card);
                            rmv.add(card);
                        }
                    }
                }

                changeCardforPlayer(playerShift, rmv);
                printChangeCard(rmv.size(), playerShift, batch);
            }//changeCardforPlayer(cpuIndex,rmv); //discard cards here
        }
//        else{
//            //DLV select check raise or fold
//        }
    }

}
