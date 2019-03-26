package se.ju.stos1605.quizzz;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

public class SelectDifficultyActivity extends AppCompatActivity {

    /** 100 = 10 seconds
     * apiCounter is a variable that is being added with 1 each second, if apiCounter >= WAITFORQUESTIONS,
     * fetching is cancelled and error message appears */
    int WAIT_FOR_QESTIONS = 100; 
  
    int mAPICounter = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_difficulty);
        mAPICounter = 0;
    }

    /** Function connected to 'easybutton', fetches questions with easy difficulty and then calls startGame()*/
    public void buttonEasyClicked(View view) {
        Toast.makeText(getApplicationContext(), R.string.fetching, Toast.LENGTH_SHORT).show();
        APIData apiData = new APIData();
        apiData.setDifficulty(1);
        apiData.execute();
        startGame();
    }

    /** Function connected to 'mediumbutton', fetches questions with medium difficulty and then calls startGame()*/
    public void buttonMediumClicked(View view) {
        Toast.makeText(getApplicationContext(), R.string.fetching, Toast.LENGTH_SHORT).show();
        APIData apiData = new APIData();
        apiData.setDifficulty(2);
        apiData.execute();
        startGame();
    }

    /** Function connected to 'hardbutton', fetches questions with hard difficulty and then calls startGame()*/
    public void buttonHardClicked(View view) {
        Toast.makeText(getApplicationContext(), R.string.fetching, Toast.LENGTH_SHORT).show();
        APIData apiData = new APIData();
        apiData.setDifficulty(3);
        apiData.execute(); //Fetch data!!
        startGame();
    }

    /** If data is fetched correctly, starts game, otherwise cancels fetching after 10 seconds*/
    private void startGame(){
        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        if ( ! APIData.isDataReady()){
            mAPICounter += 1;
            if (mAPICounter >= WAIT_FOR_QESTIONS){
                Game game = Game.getInstance();
                if(! game.mIsSinglePlayerGame){
                    BluetoothCommunicator communicator = game.mBluetoothHandler.mBluetoothCommunicator;
                    communicator.sendErrorToOpponent(communicator.buildStringFromError(0)); //Error code 0 for data fetch error
                }
                String errorMessage = getResources().getString(R.string.error_fetching_questions);
                showAlert(errorMessage);

            }
            else{
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startGame();
                    }
                }, 100);
            }
        }
        else {
            Intent intent;
            Game game = Game.getInstance();
            if (game.mIsSinglePlayerGame){
                intent = new Intent(this, QuestionActivity.class);
                startActivity(intent);
            }else{
                BluetoothCommunicator communicator = game.mBluetoothHandler.mBluetoothCommunicator;

                communicator.sendNameToOpponent(communicator.buildStringFromName(game.mMyName));
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Game game = Game.getInstance();
                        BluetoothCommunicator communicator = game.mBluetoothHandler.mBluetoothCommunicator;
                        communicator.sendQuestionsToOpponent(communicator.buildStringFromQuestions(game.mQuestions));
                        Intent intent = new Intent(SelectDifficultyActivity.this, QuestionActivity.class);
                        startActivity(intent);
                    }
                }, 1000);

            }
        }
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

    /** Overrides the back-button so that the user will be going back to startpage
     * Resets game */
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        Game game = Game.getInstance();
        game.resetGame();
        navigateUpTo(new Intent(getBaseContext(), MainActivity.class));
        finish();
    }
}
