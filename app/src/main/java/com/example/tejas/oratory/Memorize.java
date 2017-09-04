package com.example.tejas.oratory;

import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import net.sf.classifier4J.summariser.SimpleSummariser;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;

public class Memorize extends AppCompatActivity {

    TextView textSegment;
    TextView wrongText;
    TextView currentStage;

    Button record;
    Button next;
    Button override;

    final int NUM_OF_PARAGRAPHS = MainActivity.stringParagraphs.size();
    private final int SPEECH_RECOGNITION_CODE = 1;
    int COUNT = 0;

    int levelsDown = 0;

    Boolean nextStageMoved = true;

    String MODE = "Read";
    String txtOutput = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memorize);
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
        int color = getResources().getColor(R.color.white);
        setTaskDescription(new ActivityManager.TaskDescription("Oratory", icon, color));

        //for color
        // getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#90AFC5")));
        textSegment = (TextView) findViewById(R.id.textSegment);
        textSegment.setMovementMethod(new ScrollingMovementMethod());
        wrongText = (TextView) findViewById(R.id.wrongText);
        currentStage = (TextView) findViewById(R.id.stage);

        record = (Button) findViewById(R.id.record);
        next = (Button) findViewById(R.id.next);
        override = (Button) findViewById(R.id.override);

        newSection();

        override.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((COUNT + 1) % NUM_OF_PARAGRAPHS == 0) {
                    nextStageScreen();
                    textSegment.scrollTo(0,0);
                } else {
                    COUNT++;
                    newSection();
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MODE == "Read") {
                    MODE = "Summarize";
                } else if (MODE == "Summarize") {
                    MODE = "Memorize";
                }
                nextStageMoved = true;
                System.out.println(MODE);

                COUNT++;
                newSection();

                textSegment.scrollTo(0,0);
            }
        });

    }

    private void newSection() {

        System.out.println(COUNT);

        textSegment.setText(MainActivity.stringParagraphs.get(COUNT % NUM_OF_PARAGRAPHS));
        next.setVisibility(View.INVISIBLE);
        record.setVisibility(View.VISIBLE);

        currentStage.setText(MODE);

        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSpeechToText();
            }
        });
    }

    private void startSpeechToText() {
        String finalMode = "";
        switch (MODE) {
            case "Read":
                finalMode = textSegment.getText().toString();
                break;
            case "Summarize":
                SimpleSummariser summariser = new SimpleSummariser();
                String summarisedText = summariser.summarise(textSegment.getText().toString(), 2);
                int prevSentenceEnd = 0;

                finalMode = "";
                for (int i = 0; i < summarisedText.length(); i++) {
                    String currChar = Character.toString(summarisedText.charAt(i));
                    if (currChar.equals(".") || currChar.equals("?") || currChar.equals("!")) {
                        finalMode += "- ";
                        finalMode += summarisedText.substring(prevSentenceEnd, i);
                        finalMode += "\n";
                        prevSentenceEnd = i + 2;
                    }
                }
                break;
            case "Memorize":
                finalMode = "";
        }
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, finalMode);
        try {
            startActivityForResult(intent, SPEECH_RECOGNITION_CODE);
        } catch (ActivityNotFoundException a) {
            makeText(getApplicationContext(),
                    "Sorry! Speech recognition is not supported in this device.",
                    LENGTH_SHORT).show();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SPEECH_RECOGNITION_CODE: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String text = result.get(0);
                    txtOutput = text;
                    txtOutput = txtOutput.toLowerCase();
                    String correctWithoutPunc = removePunctuation(textSegment.getText().toString()).toLowerCase();

                    if (txtOutput.equals(correctWithoutPunc)) {
                        wrongText.setText("");
                        Toast.makeText(getApplicationContext(), "Correct!", Toast.LENGTH_LONG).show();
                        wrongText.setText("");
                        if (levelsDown > 0) {
                            System.out.println("BAD------------------------------");
                            levelsDown--;
                            if (MODE == "Read") {
                                MODE = "Summarize";
                            } else if (MODE == "Summarize") {
                                MODE = "Memorize";
                            }
                            COUNT += NUM_OF_PARAGRAPHS;
                            newSection();

                        } else {
                            System.out.println("Here----------------");
                            if ((COUNT + 1) % NUM_OF_PARAGRAPHS == 0) {
                                if (COUNT + 1 > 3 * NUM_OF_PARAGRAPHS) {
                                    COUNT -= NUM_OF_PARAGRAPHS;
                                }
                                nextStageScreen();
                            } else {
                                COUNT++;
                                newSection();
                            }
                        }
                    } else {
                        wrongText.setText(Html.fromHtml("<b>" + txtOutput + "</b> is what I heard, but <b>" + correctWithoutPunc + "</b> is the answer."
                                + " You got <b>" + getScore(new ArrayList<String>(Arrays.asList(correctWithoutPunc.split(" "))), new ArrayList<String>(Arrays.asList(txtOutput.split(" ")))) + "% </b> of words correct"));
                        if (COUNT >= NUM_OF_PARAGRAPHS) {
                            levelsDown++;
                            if (MODE == "Memorize") {
                                MODE = "Summarize";
                            } else if (MODE == "Summarize") {
                                MODE = "Read";
                            }
                            COUNT -= NUM_OF_PARAGRAPHS;
                        }
                        newSection();
                    }

                }
                break;
            }
        }
    }

    private void nextStageScreen() {
        System.out.println("IN NEXT STAGE SCREEN");
        if (COUNT + 1 >= 3 * NUM_OF_PARAGRAPHS) {
            textSegment.setText("Congrats! You've completed all three stages! If you wish, click Next to continue memorizing!");
        } else {
            textSegment.setText("Move on to the next section!");
        }

        next.setVisibility(View.VISIBLE);
        record.setVisibility(View.INVISIBLE);
    }

    private String removePunctuation(String str) {
        String newString = str;
        String[] punctuation = {"/", ".", ",", "\"", ";", ":", "(", ")", "!", "?", "-"};
        for(int i = 0; i < punctuation.length; i++){
            newString = newString.replace(punctuation[i], "");
        }
        return newString;
    }

    private double getPercent(ArrayList<String> userText, ArrayList<String> correctText) {
        int correct1 = 0;
        int correct2 = 0;

        System.out.println(Arrays.toString(toWords(txtOutput).toArray()));

        for(int i = 0; i< correctText.size(); i++){
            while (userText.size() < correctText.size()) {
                userText.add("");
            }
            int index = userText.indexOf(correctText.get(i));
            if(userText.contains(correctText.get(i))){
                if (Math.abs(index - i) < 3) {
                    correct1++;
                }
            }

            System.out.println("index: " + index);
            System.out.println("userContainsCorrect: " + Boolean.toString(userText.contains(correctText.get(i))));
            System.out.println("Math.abs: " + Boolean.toString(Math.abs(index - i) < 3));
            System.out.println("Correct: " + correct1);

        }

        for(int i = 0; i< correctText.size(); i++){
            while (correctText.size() < userText.size()) {
                correctText.add("");
            }
            int index = correctText.indexOf(userText.get(i));
            if(correctText.contains(userText.get(i))){
                if (Math.abs(index - i) < 3) {
                    correct2++;
                }
            }

            System.out.println("index: " + index);
            System.out.println("userContainsCorrect: " + Boolean.toString(correctText.contains(correctText.get(i))));
            System.out.println("Math.abs: " + Boolean.toString(Math.abs(index - i) < 3));
            System.out.println("Correct: " + correct2);

        }

        int finalCorrect = Math.min(correct1, correct2);

        return ((double) finalCorrect)/(double) (correctText.size());
    }

    /*private double getPercent2(ArrayList<String> userText, ArrayList<String> correctText) {
        int originalSize = correctText.size();
        if (userText.size() > correctText.size()) {
            while(correctText.size() < userText.size()) {
                correctText.add("");
            }
        } else if (correctText.size() > userText.size()) {
            while(userText.size() < correctText.size()) {
                userText.add("");
            }
        }

        double percent = 0;

        ArrayList<String> cloneUser = (ArrayList<String>) userText.clone();
        ArrayList<String> cloneCorrect = (ArrayList<String>) correctText.clone();


        for (int i = 0; i < correctText.size(); i++) {
            if (cloneUser.contains(cloneCorrect.get(i))) {
                cloneUser.remove(cloneUser.indexOf(cloneCorrect.get(i)));
                cloneCorrect.remove(i);
            }
        }

        int wrong = cloneCorrect.size() + cloneUser.size();





    } */

    private  ArrayList<String> toWords(String str) {
        ArrayList<String> words = new ArrayList<String>();
        String str1 = str;
        int lastWord = 0;
        for(int i = 0; i < str.length(); i++){
            if(Character.toString(str1.charAt(i)).equals(" ") || Character.toString(str1.charAt(i)).equals("!") || Character.toString(str1.charAt(i)).equals("?") || Character.toString(str1.charAt(i)).equals(".")){
                words.add(str1.substring(lastWord, i));
                if(Character.toString(str1.charAt(i)).equals(".") || Character.toString(str1.charAt(i)).equals("!") || Character.toString(str1.charAt(i)).equals("?")){
                    lastWord = i + 2;
                } else {
                    lastWord = i + 1;
                }
            } else if (i == str1.length() - 1) {
                words.add(str1.substring(lastWord));
            }
        }
        return words;
    }

    private String getScore(ArrayList<String> correctStr, ArrayList<String> userStr){
        int match = 1;
        int gap = -2;
        int mismatch = -1;

        int[][] a = new int[correctStr.size()+1][userStr.size()+1];
        int[][] reference = new int[correctStr.size()+1][userStr.size()+1];

        for(int i =0; i<correctStr.size()+1; i++){
            a[i][0] = gap*i;
        }

        for(int i =0; i<userStr.size()+1; i++){
            a[0][i] = gap*i;
        }

        for(int i=1;i<correctStr.size()+1;i++){
            for(int j=1;j<userStr.size()+1;j++) {
                int currentPenalty = mismatch;
                int used = 0;
                //0 is a[i - 1][j - 1] + currentPenalty, 1 is a[i - 1][j] + gap, 2 is a[i][j - 1] + gap

                if (correctStr.get(i - 1).equals(userStr.get(j - 1))) {
                    currentPenalty = match;
                }

                if(a[i - 1][j - 1] + currentPenalty>a[i - 1][j] + gap){
                    used = 0;
                } else {
                    used = 1;
                }

                if(used==0){
                    if(a[i - 1][j - 1] + currentPenalty<a[i][j-1] + gap){
                        used = 2;
                    } else {
                        used = 0;
                    }
                } else {
                    if(a[i - 1][j] + gap<a[i][j-1] + gap){
                        used = 2;
                    } else {
                        used = 1;
                    }
                }

                a[i][j] = Math.max(a[i - 1][j - 1] + currentPenalty, Math.max(a[i - 1][j] + gap, a[i][j - 1] + gap));
                reference[i][j] = used;
            }

        }

        int i = correctStr.size();
        int j = userStr.size();
        ArrayList<String> finalCorrect = new ArrayList<>();
        ArrayList<String> finalUser = new ArrayList<>();
        while(i>0 && j>0){
            if(reference[i][j]==0){
                finalCorrect.add(correctStr.get(i-1));
                finalUser.add(userStr.get(j-1));
                i--;
                j--;
            } else if(reference[i][j]==1){
                finalCorrect.add(correctStr.get(i-1));
                finalUser.add("*");
                i--;
            } else {
                finalCorrect.add("*");
                finalUser.add(userStr.get(j-1));
                j--;
            }

        }

        int numCorrect = 0;

        while(finalCorrect.size() < finalUser.size()){
            finalCorrect.add("*");
        }

        while(finalCorrect.size() > finalUser.size()){
            finalUser.add("*");
        }

        for (int k = 0;k < finalCorrect.size();k++){
            if(finalUser.get(k).equals(finalCorrect.get(k))){
                numCorrect++;
            }
        }

        return String.format("%.2f", 100.0*((double) numCorrect / finalCorrect.size())) + "/" + finalCorrect + "/" + finalUser;
    }
}
