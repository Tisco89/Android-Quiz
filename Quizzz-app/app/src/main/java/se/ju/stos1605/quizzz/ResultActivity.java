package se.ju.stos1605.quizzz;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Game game = Game.getInstance();
        if (!game.mIsSinglePlayerGame) {
            TextView scoreView = findViewById(R.id.textView);

            if(game.mMyScore > game.mOpponentScore){
                String resultString = getString(R.string.result_multiplayer_win)+  "\n"  + game.mMyScore + " " + getString(R.string.vs) + " " + game.mOpponentScore;
                scoreView.setText(resultString);

            }else if(game.mMyScore == game.mOpponentScore) {
                String resultString = getString(R.string.result_multiplayer_tie)+ "\n" + game.mMyScore + " " + getString(R.string.vs) + " " + game.mOpponentScore;
                scoreView.setText(resultString);

            }else{
                String resultString = getString(R.string.result_multiplayer_lose)+ "\n" + game.mMyScore + " " + getString(R.string.vs) + " " + game.mOpponentScore;
                scoreView.setText(resultString);
            }
        }
        else{
            TextView scoreView = findViewById(R.id.textView);
            String scoreString = getString(R.string.Your_score) + " " + game.mMyScore;
            scoreView.setText(scoreString);
        }
        game.mMyScore = 0;
        game.mOpponentScore = 0;
    }

    /** Overrides the back-button so that the user will be going back to startpage */
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        goBackToStart();
        finish();

    }
    /** Buttonfunction -> Calls function to reset game and go to startpage */
    public void goBackToStartButtonClicked(View view) {
        goBackToStart();
    }
    /** Resets game and goes to startpage*/
    private void goBackToStart(){
        Game game = Game.getInstance();
        game.resetGame();
        navigateUpTo(new Intent(getBaseContext(), MainActivity.class));
    }
}
