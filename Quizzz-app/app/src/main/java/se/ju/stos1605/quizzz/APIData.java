package se.ju.stos1605.quizzz;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class APIData extends AsyncTask<Void, Void, Void> {
    private String mUrl = "https://opentdb.com/api.php?amount=10&type=multiple";

    private static boolean mIsDataReady = false;

    static public Boolean isDataReady(){
        return mIsDataReady;
    }


    APIData() {
        mIsDataReady = false;
    }

    /**Set dificulty for the questions that will be fetched from the database */
    public void setDifficulty(int difficulty){
        if ( difficulty == 1 )
            this.mUrl = "https://opentdb.com/api.php?amount=10&type=multiple&difficulty=easy";
        else if (difficulty == 2)
            this.mUrl = "https://opentdb.com/api.php?amount=10&type=multiple&difficulty=medium";
        else if (difficulty == 3)
            this.mUrl = "https://opentdb.com/api.php?amount=10&type=multiple&difficulty=hard";
        else this.mUrl = "https://opentdb.com/api.php?amount=10&type=multiple";
    }

    /**Executes before ascync data is fetched from the database*/
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    /**Fetches the data ascync from the database*/
    @Override
    protected Void doInBackground(Void... arg0) {
        mIsDataReady = false;
        Game game = Game.getInstance();
        game.mQuestions.clear();
        HttpHandler sh = new HttpHandler();
        // Making a request to mUrl and getting response
        String jsonStr = sh.makeServiceCall(this.mUrl);

        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);

                // Getting JSON Array node
                JSONArray questions = jsonObj.getJSONArray("results");

                // looping through All Contacts
                for (int i = 0; i < questions.length(); i++) {
                    JSONObject c = questions.getJSONObject(i);
                    String question = c.getString("question");
                    String correct_answer = c.getString("correct_answer");

                    JSONArray incorrect_answers = c.getJSONArray("incorrect_answers");
                    ArrayList<String> incorrectAnswers = new ArrayList<String>();
                    for (int b = 0; b < 3; b++){
                        incorrectAnswers.add(incorrect_answers.getString(b));
                    }

                    Question questionObj = new Question();
                    questionObj.populateQuestion(question, correct_answer, incorrectAnswers);
                    questionObj.shuffleAnswers();
                    // adding question to question list
                    game.mQuestions.add(questionObj);
                }
                mIsDataReady = true;
            } catch (final JSONException e) {
                Log.e(TAG, "Json parsing error: " + e.getMessage());
            }
        } else {
            Log.e(TAG, "Couldn't get json from server.");
        }
        return null;
    }

    /**Executes after the data is fetched*/
    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
    }

}
