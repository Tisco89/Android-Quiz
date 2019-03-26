package se.ju.stos1605.quizzz;



import java.util.ArrayList;
import java.util.Collections;

public class Question {

    private String mQuestionText;

    private ArrayList<Option> mOptionList;

    Question(){
        mOptionList = new ArrayList<>();
        mQuestionText = "";
    }

    public void populateQuestion(String questionText, String correctAnswer, ArrayList<String> wrongAnswers){
        mQuestionText = questionText;
        mOptionList.add(new Option(correctAnswer, true));
        for (String question : wrongAnswers){
            mOptionList.add(new Option(question, false));
        }
        Game game = Game.getInstance();
        if(game.mIsSinglePlayerGame){
            shuffleAnswers();
        }
    }

    public void populateDecodedQuestion(String questionText, ArrayList<Option> options){
        mQuestionText = questionText;
        mOptionList = options;
    }

    public void shuffleAnswers(){
        Collections.shuffle(mOptionList);
    }

    public String getQuestion(){
        return mQuestionText;
    }

    public ArrayList<Option> getOptions(){
        return mOptionList;
    }

    public static class Option{

        String mOptionText;

        Boolean mIsCorrectAnswer;

        Option(){
            this.mIsCorrectAnswer = false;
            this.mOptionText = "";
        }
        Option(String optionText, Boolean isCorrectAnswer){
            this.mIsCorrectAnswer = isCorrectAnswer;
            this.mOptionText = optionText;
        }

        public String getOptionText(){
            return mOptionText;
        }
    }

}
