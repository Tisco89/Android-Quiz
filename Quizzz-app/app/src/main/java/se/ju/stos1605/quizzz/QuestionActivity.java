package se.ju.stos1605.quizzz;

import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

public class QuestionActivity extends AppCompatActivity {

    private Question mQuestion;

    private CountDownTimer mCountDownTimer;

    private ObjectAnimator mProgressBarAnimation;

    int TIMEOUTDISCONNECT = 120;

    int mTimeOutCounter = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        mTimeOutCounter = 0;
        this.putQuestions();
    }

    /** Overrides the back-button so that the user will be going back to startpage
     * Resets game
     * Reset countdowntimer
     * Cancels animation*/
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        Game game = Game.getInstance();
        if(! game.mIsSinglePlayerGame){
            game.mBluetoothHandler.mBluetoothCommunicator.sendErrorToOpponent(game.mBluetoothHandler.mBluetoothCommunicator.buildStringFromError(1));
        }
        game.resetGame();
        mCountDownTimer.cancel();
        mProgressBarAnimation.cancel();
        navigateUpTo(new Intent(getBaseContext(), MainActivity.class));
        finish();

    }

    /** Function that starts a 10 second timer on every new mQuestion
     * Disables buttons and check answer when clicked or timer runs out */
    private void startTimer(){
        /**Length of game*/
        int TIMER_LENGTH = 10000;

        /**PROGRESSBAR:*/
        ProgressBar progressBar = findViewById(R.id.progressbar);
        mProgressBarAnimation = ObjectAnimator.ofInt(progressBar, "progress", 100, 0);
        mProgressBarAnimation.setDuration(TIMER_LENGTH);
        mProgressBarAnimation.start();

        /**GAME-TIMER:*/
        mCountDownTimer = new CountDownTimer(TIMER_LENGTH, 1000) {

            public void onTick(long millisUntilFinished) {
                TextView textViewCountDown = findViewById(R.id.textViewCountDown);
                String countdownText = getString(R.string.secunds_remaining) + millisUntilFinished / 1000;
                textViewCountDown.setText(countdownText);
            }

            public void onFinish() {
                disableButtons();
                updateWrongAnswer();
                Game game = Game.getInstance();
                TextView textViewCountDown = findViewById(R.id.textViewCountDown);
                textViewCountDown.setText(getString(R.string.done));
                if(game.mIsSinglePlayerGame){
                    newRound();
                }
                else {
                    game.mBluetoothHandler.mBluetoothCommunicator.sendAnswerToOpponent(game.mBluetoothHandler.mBluetoothCommunicator.buildStringFromAnswer(4));
                    checkIfUpdateOpponentAnswer();
                }
            }
        }.start();

    }

    /** Unpairs devices when a multiplayer game is finished and shows the result */
    private void finishGame(){
        Game game = Game.getInstance();
        if (game.mBluetoothHandler != null) {
            game.mBluetoothHandler.unPairDevice();
        }
        Intent intent = new Intent(this, ResultActivity.class);
        startActivity(intent);
    }

    /** Fetches questions and answers */
    private void putQuestions(){
        ArrayList<Question> questions = Game.getInstance().mQuestions;
        if (questions.size() <= 0) { //finish the game
            finishGame();
        }
        else {
            this.mQuestion = questions.get(0);
            questions.remove(0);
            TextView questionView = findViewById(R.id.questionTextView);
            questionView.setText(Html.fromHtml(mQuestion.getQuestion()));

            Game.getInstance().mCurrentQuestion += 1;
            TextView questionCounterView = findViewById(R.id.questionCounterTextView);
            String questionCounterString = getString(R.string.question_counter) + Game.getInstance().mCurrentQuestion + "/10";
            questionCounterView.setText(questionCounterString);

            if(Game.getInstance().mIsSinglePlayerGame){
                TextView scoreView = findViewById(R.id.textViewScore);
                String scoreString = getString(R.string.Your_score) + Game.getInstance().mMyScore;
                scoreView.setText(scoreString);

            }else{
                TextView scoreView = findViewById(R.id.textViewScore);
                String scoreString = Game.getInstance().mMyName + ": " + Game.getInstance().mMyScore + "\n" + Game.getInstance().mOpponentName + ": " + Game.getInstance().mOpponentScore;
                scoreView.setText(scoreString);

            }

            ArrayList<Question.Option> answers = mQuestion.getOptions();
            ArrayList<Button> btnArray = getButtonArray();
            for (int i = 0; i < 4; i++ ){
                btnArray.get(i).setText(Html.fromHtml(answers.get(i).mOptionText));
            }
            enableButtons();
            startTimer();
        }

    }

    /** Creates array with buttons */
    private ArrayList<Button> getButtonArray(){
        ArrayList<Button> btnArray = new ArrayList<>();
        btnArray.add((Button)findViewById(R.id.button1));
        btnArray.add((Button)findViewById(R.id.button2));
        btnArray.add((Button)findViewById(R.id.button3));
        btnArray.add((Button)findViewById(R.id.button4));
        return btnArray;

    }

    /** disables buttons in array */
    private void disableButtons(){
        ArrayList<Button> btnArray = getButtonArray();
        for (Button btn : btnArray){
            btn.setEnabled(false);
        }
    }

    /** enables buttons in array */
    private void enableButtons(){
        ArrayList<Button> btnArray = getButtonArray();
        for (Button btn : btnArray){
            btn.setEnabled(true);
        }
    }

    /** Start a new round
     * Set all buttons to their original state */
    private void newRound(){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                GradientDrawable draw = (GradientDrawable) getResources().getDrawable(R.drawable.rectangle_button);
                ArrayList<Button> btnArray = getButtonArray();
                for (Button btn : btnArray){
                    btn.setBackground(draw);
                }

                putQuestions();
            }
        }, 3000);
    }

    /** If clicked buttons is wrong answer, show correct option */
    private void updateWrongAnswer(){
        final Button buttonCorrect;
        for (Question.Option option : mQuestion.getOptions()){
            if (option.mIsCorrectAnswer){
                switch (mQuestion.getOptions().indexOf(option)){
                    case 0:
                        buttonCorrect = findViewById(R.id.button1);
                        break;
                    case 1:
                        buttonCorrect = findViewById(R.id.button2);
                        break;
                    case 2:
                        buttonCorrect = findViewById(R.id.button3);
                        break;
                    case 3:
                        buttonCorrect = findViewById(R.id.button4);
                        break;
                    default:
                        buttonCorrect = findViewById(R.id.button1);

                }
                GradientDrawable correctDraw = (GradientDrawable) getResources().getDrawable(R.drawable.button_green);
                buttonCorrect.setBackground(correctDraw);
                ObjectAnimator.ofFloat(buttonCorrect, "rotation", 0, 360).start();
                break;
            }
        }
    }

    /** Check what buttons is clicked,
     * if correct turn green and add score
     * if not, turn red and call previous function
     * if multiplayer, show opponents answer in blue with next function */
    private void updateButtonClicked(final Button button, int questionIndex){
        disableButtons();
        mCountDownTimer.cancel();
        Game game = Game.getInstance();
        if (game.mIsSinglePlayerGame){
            mProgressBarAnimation.cancel();
        }
        game.mMyAnswer = questionIndex;
        if(mQuestion.getOptions().get(questionIndex).mIsCorrectAnswer){
            game.mMyScore += 1;
            GradientDrawable draw = (GradientDrawable) getResources().getDrawable(R.drawable.button_green);
            button.setBackground(draw);
            ObjectAnimator.ofFloat(button, "rotation", 0, 360).start();

        }
        else{
            GradientDrawable draw = (GradientDrawable) getResources().getDrawable(R.drawable.button_red);
            button.setBackground(draw);
            updateWrongAnswer();
        }

        if (Game.getInstance().mIsSinglePlayerGame){
            TextView scoreView = findViewById(R.id.textViewScore);
            String scoreString = getString(R.string.Your_score) + Game.getInstance().mMyScore;
            scoreView.setText(scoreString);
            newRound();
        }
        else{
            game.mBluetoothHandler.mBluetoothCommunicator.sendAnswerToOpponent(game.mBluetoothHandler.mBluetoothCommunicator.buildStringFromAnswer(questionIndex));
            checkIfUpdateOpponentAnswer();
        }

    }

    /** If multiplayer, check what opponent has answered
     * turn button blue
     * or blue border if button is already red or green
     * updates opponents score
     * Shows alertdialog after 12 seconds and cancels game if opponent is disconnected */
    public void checkIfUpdateOpponentAnswer(){
       final Game game = Game.getInstance();
       final Handler handler = new Handler();
       handler.postDelayed(new Runnable() {
           @Override
           public void run() {
               if (game.mErrorHasOccured){
                   game.resetGame();
                   sendAlert(Integer.parseInt(game.mErrorString));
               }
               if (game.mHasOpponentAnswered){
                   mTimeOutCounter = 0;
                   game.mHasOpponentAnswered = false;
                   Button buttonToUpdate;
                   mProgressBarAnimation.cancel();
                   switch(game.mOpponentAnswer){
                       case 0:
                           buttonToUpdate = (Button) findViewById(R.id.button1);
                           break;
                       case 1:
                           buttonToUpdate = (Button) findViewById(R.id.button2);
                           break;
                       case 2:
                           buttonToUpdate = (Button) findViewById(R.id.button3);
                           break;
                       case 3:
                           buttonToUpdate = (Button) findViewById(R.id.button4);
                           break;
                       default:
                           buttonToUpdate = (Button) findViewById(R.id.button1);
                           break;
                   }

                    if(game.mOpponentAnswer != 4){
                        if(mQuestion.getOptions().get(game.mOpponentAnswer).mIsCorrectAnswer){
                            game.mOpponentScore += 1;
                            GradientDrawable draw = (GradientDrawable) getResources().getDrawable(R.drawable.button_greenblue);
                            buttonToUpdate.setBackground(draw);
                        }else if(mQuestion.getOptions().get(game.mOpponentAnswer) == mQuestion.getOptions().get(game.mMyAnswer)){
                            GradientDrawable draw = (GradientDrawable) getResources().getDrawable(R.drawable.button_redblue);
                            buttonToUpdate.setBackground(draw);
                        }else{
                            GradientDrawable draw = (GradientDrawable) getResources().getDrawable(R.drawable.button_blue);
                            buttonToUpdate.setBackground(draw);
                        }
                    }

                    TextView scoreView = findViewById(R.id.textViewScore);
                    String scoreString = Game.getInstance().mMyName + ": " + Game.getInstance().mMyScore + "\n" + Game.getInstance().mOpponentName + ": " + Game.getInstance().mOpponentScore;
                    scoreView.setText(scoreString);
                    newRound();
                    return;
               }
               else{
                   mTimeOutCounter += 1;
                   if(mTimeOutCounter >= TIMEOUTDISCONNECT){ // 12 seconds
                       String errorString = getString(R.string.wrong_message);
                       game.resetGame();
                       showAlert(errorString);
                       return;
                   }
                   checkIfUpdateOpponentAnswer();
                   return;
               }
           }
       }, 100);
    }

    public void button1Clicked(View view) {
        updateButtonClicked((Button) findViewById(R.id.button1), 0);
    }
    public void button2Clicked(View view) {
        updateButtonClicked((Button) findViewById(R.id.button2), 1);
    }
    public void button3Clicked(View view) {
        updateButtonClicked((Button) findViewById(R.id.button3), 2);
    }
    public void button4Clicked(View view) {
        updateButtonClicked((Button) findViewById(R.id.button4), 3);
    }

    /** Sends an alert based on an error recived over bluetooth */
    public void sendAlert(Integer errorNumber){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(R.string.alert);
        switch (errorNumber){
            case 0:
                alertDialog.setMessage(getString(R.string.error_fetching_questions));
            case 1:
                alertDialog.setMessage(getString(R.string.opponent_left));
                break;
            default:
                alertDialog.setMessage(getString(R.string.wrong_message));
                break;
        }
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.OK),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        navigateUpTo(new Intent(getBaseContext(), MainActivity.class));
                    }
                });
        alertDialog.show();
    }

    /** Alertdialog that takes string as parameter, string contains message*/
    private void showAlert(String string){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(R.string.alert);
        alertDialog.setMessage(string);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.OK),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        navigateUpTo(new Intent(getBaseContext(), MainActivity.class));
                    }
                });
        alertDialog.show();

    }

}
