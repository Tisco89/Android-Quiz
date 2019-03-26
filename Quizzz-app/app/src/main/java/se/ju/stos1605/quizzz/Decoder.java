package se.ju.stos1605.quizzz;

import android.util.Log;

import java.util.ArrayList;

public class Decoder {

    /**Decodes the questionstring input from StringBuilder message
     *Bluetooth flags: Question: QUESTION0, Option: OPTION01O. For example:
     *QUESTION0Which of these is NOT a map included in the game Counter-Strike: Global Offensive?OPTION00AssaultOPTION01OilrigCOR_ANSOPTION02MirageOPTION03CacheQUESTION1Which artist released the 2012 single &quot;Harlem Shake&quot;, which was used in numerous YouTube videos in 2013?OPTION10RL GrimeOPTION11NGHTMREO12BaauerCOR_ANSOPTION13FlosstradamusQUESTION2Which of these games includes the phrase &quot;Do not pass Go, do not collect $200&quot;?OPTION20CoppitO21Pay DayOPTION22MonopolyCOR_ANSOPTION23CluedoQUESTION3TF2: What code does Soldier put into the door keypad in &quot;Meet the Spy&quot;?OPTION30No codeOPTION311432OPTION321111COR_ANSOPTION331337QUESTION4The Space Needle is located in which city?OPTION40Los AnglesOPTION41VancouverOPTION42SeattleCOR_ANSOPTION43TorontoQUESTION5What video game engine does the videogame Quake 2 run in?OPTION50Unreal EngineOPTION51iD Tech 2COR_ANSOPTION52iD Tech 3OPTION53iD Tech 1QUESTION6When Donkey Kong died in the &quot;Donkey Kong Country&quot; episode &quot;It&#039;s a Wonderful Life&quot;, who was his guardian angel?OPTION60Eddie the Mean Old YetiCOR_ANSOPTION61King K. RoolOPTION62Kiddy KongOPTION63Diddy KongQUESTION7What mythology did the god &quot;Apollo&quot; come from?OPTION70Roman and SpanishOPTION71Greek, Roman and NorseOPTION72Greek and ChineseOPTION73Greek and RomanCOR_ANSQUESTION8Who led the Communist Revolution of Russia?OPTION80Joseph StalinOPTION81Vladimir LeninCOR_ANSOPTION82Vladimir PutinOPTION83Mikhail GorbachevEOF
     */
    public ArrayList<Question> decodeQuestions(StringBuilder message){
        ArrayList<Question> questions = new ArrayList<>();

        Question question =  new Question();

        ArrayList<Question.Option> answers = new ArrayList<>();

        StringBuilder tempQuestion = new StringBuilder();

        StringBuilder tempOption = new StringBuilder();

        boolean isCorrectAnswer = false;

        boolean isQuestion = true;

        for(int index = 1; index < message.length(); index++){
            String temp1 = String.valueOf(message.charAt(index));
            if(temp1.equals("Q")){
                StringBuilder tmpStringBuilder = new StringBuilder();
                for (int charsAhead = index; charsAhead < index + 8; charsAhead++){
                    tmpStringBuilder.append(message.charAt(charsAhead));
                    if(tmpStringBuilder.toString().equals("QUESTION")){
                        if(String.valueOf(message.charAt(charsAhead + 1)).matches("-?\\d+(\\.\\d+)?")){
                            if(tempQuestion.length() != 0){

                                if(tempOption.length() != 0){
                                    answers.add(new Question.Option(tempOption.toString(), isCorrectAnswer));
                                    isCorrectAnswer = false;
                                    tempOption = new StringBuilder();
                                }


                                question.populateDecodedQuestion(tempQuestion.toString(), answers);
                                questions.add(question);
                                question = new Question();
                            }
                            tempQuestion = new StringBuilder();
                            answers = new ArrayList<>();
                            isQuestion = true;
                            isCorrectAnswer = false;
                            index = charsAhead + 2;
                        }
                    }
                }
            }
            else if(temp1.equals("O")){
                StringBuilder tmpStringBuilder = new StringBuilder();
                for (int charsAhead = index; charsAhead < index + 6; charsAhead++){
                    tmpStringBuilder.append(message.charAt(charsAhead));
                    if(tmpStringBuilder.toString().equals("OPTION")){
                        if(String.valueOf(message.charAt(charsAhead + 1)).matches("-?\\d+(\\.\\d+)?") &&
                                String.valueOf(message.charAt(charsAhead + 2)).matches("-?\\d+(\\.\\d+)?")){
                            if(tempOption.length() != 0){
                                answers.add(new Question.Option(tempOption.toString(), isCorrectAnswer));
                                isCorrectAnswer = false;
                            }
                            tempOption = new StringBuilder();
                            isQuestion = false;
                            index = charsAhead + 3;
                            break;
                        }
                    }
                }
            }
            else if (temp1.equals("C")){
                StringBuilder tempCorrAns = new StringBuilder();
                for(int charsAhead = index; charsAhead < index + 7; charsAhead++){
                    tempCorrAns.append(message.charAt(charsAhead));
                }
                if(tempCorrAns.toString().equals("COR_ANS")){
                    isCorrectAnswer = true;
                    index = index + 6;
                    continue;
                }
            }
            else if (temp1.contains("_")){
                StringBuilder tmpStringBuilder = new StringBuilder();
                for (int b = index; b < index + 11; b++){
                    tmpStringBuilder.append(message.charAt(b));
                }
                if(tmpStringBuilder.toString().equals("_ENDOFLINE_")){
                    answers.add(new Question.Option(tempOption.toString(), isCorrectAnswer));
                    isCorrectAnswer = false;
                    question.populateDecodedQuestion(tempQuestion.toString(), answers);
                    questions.add(question);
                    break;
                }
            }
            if(isQuestion){
                tempQuestion.append(message.charAt(index));
            }
            else{
                tempOption.append(message.charAt(index));
            }
        }
        return questions;
    }

    /**
     * Decodes answer string that has been sent over bluetooth
     * */
    public Integer decodePreviousAnswer(StringBuilder message){
        String messageString = message.toString();
        if (messageString.length() == 2){
            return Character.getNumericValue(messageString.charAt(1));
        }
        return 4;
    }

    /**
     * Decodes error code that has been sent over bluetooth
     * */
    public Integer decodeError(StringBuilder message){
        String messageString = message.toString();
        if (messageString.length() == 2){
            return Character.getNumericValue(messageString.charAt(1));
        }
        return 100;
    }

    /**
     * Decodes name string that has been sent over bluetooth
     * */
    public String decodeName(StringBuilder message){
        String messageString = message.toString();
        return messageString.substring(1);
    }

}
