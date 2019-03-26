package se.ju.stos1605.quizzz;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class BluetoothCommunicator extends Thread {

    public byte[] mByteArr;

    private BluetoothSocket mSocket;

    private OutputStream mOutputStream;

    private InputStream mInputStream;

    /** Start a Communicator
     *  Initiate input- and outputstreams*/
    public BluetoothCommunicator(BluetoothSocket socket){
        mSocket = socket;
        try {
            mInputStream = mSocket.getInputStream();
        } catch (IOException e) {
            Log.e("UH", "Failed to get inputStream: ", e);
        }
        try {
            mOutputStream = mSocket.getOutputStream();
        } catch (IOException e) {
            Log.e("UH", "Failed to create outputStream: ", e);
        }
        Log.e("UH","Started output- and input streams.");
        Game game = Game.getInstance();
        game.mIsPlayerConnected = true;
        mByteArr = new byte[1024];
    }

    /** Start the thread
     *  Continuously listen for messages over bluetooth*/
    public void run(){
        StringBuilder message = new StringBuilder();
        while(true){
            try {
                int length = mInputStream.read(mByteArr);
                if (length > 0){
                    for (int i = 0; i < length; ++i){
                        message.append((char)mByteArr[i]);
                    }
                }
                if(Integer.parseInt(String.valueOf(message.charAt(0))) == 1) {
                    if(isStringComplete(message.toString())) {
                        whatToDecode(message);
                        message = new StringBuilder();
                    }
                }
                else{
                    whatToDecode(message);
                    message = new StringBuilder();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /** Check if the question message is complete*/
    private boolean isStringComplete(String str){
        int length = str.length();
        StringBuilder strToCheck = new StringBuilder();
        for (int i = length - 11; i < length; i++){
            strToCheck.append(str.charAt(i));
        }
        return strToCheck.toString().equals("_ENDOFLINE_");
    }

    /** Determines what to decode from a bluetooth message
     *  The first number determines what the message contains*/
    public void whatToDecode(StringBuilder message){
        Integer firstChar= Integer.parseInt(String.valueOf(message.charAt(0)));

        Decoder decoder = new Decoder();
        Game game = Game.getInstance();
        switch (firstChar){
            case 1:
                game.mQuestions = decoder.decodeQuestions(message);
                game.mIsBluetoothDataReady = true;
                break;
            case 2:
                Integer opponentPreviousAnswer = decoder.decodePreviousAnswer(message);
                game.updateOpponentAnswer(opponentPreviousAnswer);
                game.mHasOpponentAnswered = true;
                break;
            case 3:
                game.mOpponentName = decoder.decodeName(message);
                break;
            case 9:
                game.errorOccured(decoder.decodeError(message).toString());
            default:
                break;

        }

    }

    /** Write a message to the paired bluetooth device */
    public void write(byte[] bytes){
        try {
            mOutputStream.flush();
            mOutputStream.write(bytes);
        } catch (IOException e) {
            Log.e("UH", "Failed to write to output stream:", e);
        }
    }

    /** Send the string of questions to opponent */
    public void sendQuestionsToOpponent(String questionsString){
        byte[] byteArr = new byte[4096];
        byteArr = questionsString.getBytes();
        write(byteArr);
    }

    /** Construct String from name to send to opponent */
    public String buildStringFromName(String nameString){
        String emptyString = "";
        StringBuilder sb = new StringBuilder(emptyString);
        sb.append("3");
        sb.append(nameString);
        return sb.toString();
    }

    /** Send name over bluetooth*/
    public void sendNameToOpponent(String nameString){
        byte[] byteArr = new byte[1024];
        byteArr = nameString.getBytes();
        write(byteArr);
    }

    /** Build string from the answer to the previous question */
    public String buildStringFromAnswer(Integer answer){
        String answerString = "";
        StringBuilder sb = new StringBuilder(answerString);
        sb.append("2");
        sb.append(answer.toString());
        return sb.toString();
    }

    /** Sends a string with an error to the opponent*/
    public void sendErrorToOpponent(String errorString){
        byte[] byteArr = new byte[1024];
        byteArr = errorString.getBytes();
        write(byteArr);
    }

    /** Constructs a string to send to an opponent. Represents some error*/
    public String buildStringFromError(Integer errorType){
        String answerString = "";
        StringBuilder sb = new StringBuilder(answerString);
        sb.append("9");
        sb.append(errorType.toString());
        return sb.toString();
    }

    /** Send answer to previous question to opponent*/
    public void sendAnswerToOpponent(String answerString){
        byte[] byteArr = new byte[1024];
        byteArr = answerString.getBytes();
        write(byteArr);
    }

    /** Takes the list of questions and answers and parses them down to a string */
    public String buildStringFromQuestions(ArrayList<Question> questions) {
        String questionArrString = "";
        StringBuilder sb = new StringBuilder(questionArrString);
        sb.setLength(0);
        sb.append("1");
        for (int i = 0; i < questions.size(); i++) {
            //Mark each question text with QUESTION plus its index in the array.
            sb.append("QUESTION").append(Integer.toString(i));
            sb.append(questions.get(i).getQuestion());
            ArrayList<Question.Option> options = questions.get(i).getOptions();
            for (int j = 0; j < 4; j++){
                //Mark each option with OPTION plus question's index plus option's index.
                sb.append("OPTION").append(Integer.toString(i)).append(Integer.toString(j));
                sb.append(options.get(j).getOptionText());
                if (options.get(j).mIsCorrectAnswer){
                    //Mark which option is correct.
                    sb.append("COR_ANS");
                }
            }
            if (i == questions.size() -1){
                sb.append("_ENDOFLINE_");
            }
        }
        return sb.toString();
    }

    /** Closes the connected socket */
    public void cancel(){
        try {
            mSocket.close();
        } catch (IOException e) {
            Log.e("UH", "Failed to close Socket:", e);
        }
    }

    public void sendTestMessage(String message){
        write(message.getBytes());
    }
}
