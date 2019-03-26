package se.ju.stos1605.quizzz;

import android.util.Log;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class DecoderTest {

    @Test
    public void testDecoder(){
        StringBuilder message = new StringBuilder();
        //message.append("1QUESTION0Which of these is NOT a map included in the game Counter-Strike: Global Offensive?OPTION00AssaultOPTION01OilrigCOR_ANSOPTION02MirageOPTION03CacheQUESTION1Which artist released the 2012 single &quot;Harlem Shake&quot;, which was used in numerous YouTube videos in 2013?OPTION10RL GrimeOPTION11NGHTMREOPTION12BaauerCOR_ANSOPTION13FlosstradamusQUESTION2Which of these games includes the phrase &quot;Do not pass Go, do not collect $200&quot;?OPTION20CoppitOPTION21Pay DayOPTION22MonopolyCOR_ANSOPTION23CluedoQUESTION3TF2: What code does Soldier put into the door keypad in &quot;Meet the Spy&quot;?OPTION30No codeOPTION311432OPTION321111COR_ANSOPTION331337QUESTION4The Space Needle is located in which city?OPTION40Los AnglesOPTION41VancouverOPTION42SeattleCOR_ANSOPTION43TorontoQUESTION5What video game engine does the videogame Quake 2 run in?OPTION50Unreal EngineOPTION51iD Tech 2COR_ANSOPTION52iD Tech 3OPTION53iD Tech 1QUESTION6When Donkey Kong died in the &quot;Donkey Kong Country&quot; episode &quot;It&#039;s a Wonderful Life&quot;, who was his guardian angel?OPTION60Eddie the Mean Old YetiCOR_ANSOPTION61King K. RoolOPTION62Kiddy KongOPTION63Diddy KongQUESTION7What mythology did the god &quot;Apollo&quot; come from?OPTION70Roman and SpanishOPTION71Greek, Roman and NorseOPTION72Greek and ChineseOPTION73Greek and RomanCOR_ANSQUESTION8Who led the Communist Revolution of Russia?OPTION80Joseph StalinOPTION81Vladimir LeninCOR_ANSOPTION82Vladimir PutinOPTION83Mikhail Gorbachev_ENDOFLINE_");
        message.append("1QUESTION0In the Super Mario Bros. Series, what is Yoshi&#039;s scientific name?OPTION00YossyCOR_ANSOPTION01T. Yoshisaur MunchakoopasOPTION02T. Yoshisotop MunchakoopasOPTION03YoshiQUESTION1What is the name of one of the Neo-Aramaic languages spoken by the Jewish population from Northwestern Iraq?OPTION10Hulaul&aacute;OPTION11Lishan DidanOPTION12Chaldean Neo-AramaicCOR_ANSOPTION13Lishana DeniQUESTION2What is the scientific name of the red fox?OPTION20Vulpes RedusOPTION21Red FoxOPTION22Vulpes VulpieCOR_ANSOPTION23Vulpes VulpesQUESTION3In &quot;Highschool of the Dead&quot;, where did Komuro and Saeko establish to meet after the bus explosion?COR_ANSOPTION30Eastern Police StationOPTION31The Center MallOPTION32Komuro&#039;s HouseOPTION33On The Main BridgeQUESTION4Which Death Grips album is the only one to feature a band member?OPTION40The Powers That BOPTION41The Money StoreCOR_ANSOPTION42No Love Deep WebOPTION43Bottomless PitQUESTION5Llanfair&shy;pwllgwyngyll&shy;gogery&shy;chwyrn&shy;drobwll&shy;llan&shy;tysilio&shy;gogo&shy;goch is located on which Welsh island?COR_ANSOPTION50AngleseyOPTION51BardseyOPTION52BarryOPTION53CaldeyQUESTION6Which album was released by Kanye West in 2013?OPTION60Watch the ThroneOPTION61My Beautiful Dark Twisted FantasyOPTION62The Life of PabloCOR_ANSOPTION63YeezusQUESTION7America Online (AOL) started out as which of these online service providers?COR_ANSOPTION70Quantum LinkOPTION71CompuServeOPTION72ProdigyOPTION73GEnieQUESTION8According to &quot;Star Wars&quot; lore, which planet does Obi-Wan Kenobi come from?OPTION80NabooCOR_ANSOPTION81StewjonOPTION82AlderaanOPTION83TatooineQUESTION9Which of the following is another name for the &quot;Poecilotheria Metallica Tarantula&quot;?OPTION90Silver StripeOPTION91WoebegoneOPTION92HopperCOR_ANSOPTION93Gooty_ENDOFLINE_");
        Decoder decoder = new Decoder();
        ArrayList<Question> questions = decoder.decodeQuestions(message);
        assertFalse(questions.isEmpty());

        for (int i = 0; i < questions.size(); i++){
            assertFalse(questions.get(i).getQuestion().isEmpty());
            for (int b = 0; b < 4; b++){
                assertFalse(questions.get(i).getOptions().get(b).getOptionText().isEmpty());

            }
        }
    }
}