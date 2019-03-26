package se.ju.stos1605.quizzz;


import java.util.ArrayList;

/**Singleton class that represents a game in progress. Can be both singleplayer or multiplayer.*/
public class Game {

    public ArrayList<Question> mQuestions;

    private static final Game mOurInstance = new Game();

    public Boolean mIsSinglePlayerGame;

    public int mMyScore;

    public int mOpponentScore;

    public BluetoothHandler mBluetoothHandler;

    public String mMyName;

    public String mOpponentName;

    public Boolean mIsGameInitiator;

    public Boolean mIsBluetoothDataReady;

    public Boolean mIsPlayerConnected;

    public Boolean mHasOpponentAnswered;

    public Integer mOpponentAnswer;

    public Integer mMyAnswer;

    public Integer mCurrentQuestion;

    public Boolean mErrorHasOccured;

    public String mErrorString;

    public static Game getInstance() {
        return mOurInstance;
    }

    public Game(){
        mIsGameInitiator = false;
        mErrorHasOccured = false;
        mIsBluetoothDataReady = false;
        mIsPlayerConnected = false;
        mQuestions = new ArrayList<>();
        mCurrentQuestion = 0;
    }

    /**Initiate all the settings for singleplayer game*/
    public void initiateSinglePlayerGame() {
        mCurrentQuestion = 0;
        this.mIsSinglePlayerGame = true;
        mMyScore = 0;
    }

    /**Initiate all the settings for multiplayer game*/
    public void initiateMultiplayerGame() {
        mCurrentQuestion = 0;
        this.mIsSinglePlayerGame = false;
        this.mHasOpponentAnswered = false;
        mMyScore = 0;
        mOpponentScore = 0;
        mOpponentName = "Anonymous";
        mBluetoothHandler = new BluetoothHandler();
    }

    /**Some error in tha app has occurred and the mErrorString will describe the error*/
    public void errorOccured(String errorString){
        this.mErrorHasOccured = true;
        this.mErrorString = errorString;
    }

    /**Resets the Game class to get ready for an new game */
    public void resetGame(){
        if (this.mBluetoothHandler != null) {
            mBluetoothHandler.unPairDevice();
        }
        mIsPlayerConnected = false;
        mIsBluetoothDataReady = false;
        mErrorHasOccured = false;
    }

    /**Answer from opponent got over bluetooth*/
    public void updateOpponentAnswer(Integer answer){
        mOpponentAnswer = answer;
    }

    /**Set this players multiplayer name*/
    public void setMyName(String myName){
        mMyName = myName;
    }

    /**Set opponents players multiplayer name*/
    public void setOpponentName(String opponentName){
        mOpponentName = opponentName;
    }
}
