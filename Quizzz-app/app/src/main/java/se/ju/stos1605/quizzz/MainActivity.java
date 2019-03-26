package se.ju.stos1605.quizzz;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getResources().getColor(R.color.background);
        setContentView(R.layout.activity_main);
        TextView myTextView = findViewById(R.id.textView);
        myTextView.animate().rotation(360).setDuration(1000).start();
    }

    /**Starts a Singelplayer game*/
    public void startSinglePlayer(View view) {
        Intent intent = new Intent(this, SelectDifficultyActivity.class);
        Game game = new Game();
        game = Game.getInstance();
        game.initiateSinglePlayerGame();
        startActivity(intent);
    }

    /**Starts a Multiplayer game*/
    public void startMultiPlayer(View view) {
        Game game = new Game();
        game = Game.getInstance();
        game.initiateMultiplayerGame();
        Intent intent = new Intent(this, ChooseNameActivity.class);
        startActivity(intent);
    }
}
