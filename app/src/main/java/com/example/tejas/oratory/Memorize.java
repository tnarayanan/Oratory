package com.example.tejas.oratory;

import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import net.sf.classifier4J.summariser.SimpleSummariser;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;

public class Memorize extends AppCompatActivity {

    TextView textSegment;
    TextView wrongText;
    TextView currentStage;

    Button record;
    Button next;
    Button override;

    ListView listView;

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
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.roundedlogo);
        int color = ContextCompat.getColor(getApplicationContext(), R.color.white);
        setTaskDescription(new ActivityManager.TaskDescription("Oratory", icon, color));

        Window window = this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(getApplicationContext() ,R.color.colorPrimaryDark));


        //for color
        // getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#90AFC5")));
        textSegment = (TextView) findViewById(R.id.textSegment);
        textSegment.setMovementMethod(new ScrollingMovementMethod());
        wrongText = (TextView) findViewById(R.id.wrongText);
        currentStage = (TextView) findViewById(R.id.stage);

        listView = (ListView) findViewById(R.id.listView);

        listView.setVisibility(View.INVISIBLE);

        record = (Button) findViewById(R.id.record);
        next = (Button) findViewById(R.id.next);
        override = (Button) findViewById(R.id.override);

        newSection();

        override.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listView.setVisibility(View.INVISIBLE);
                wrongText.setText("");
                if (levelsDown > 0) {
                    upOneLevel();
                } else {
                    if ((COUNT + 1) % NUM_OF_PARAGRAPHS == 0) {
                        nextStageScreen();
                        textSegment.scrollTo(0, 0);
                    } else {
                        COUNT++;
                        newSection();
                    }
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                next.setVisibility(View.INVISIBLE);
                override.setVisibility(View.VISIBLE);
                if (MODE.equals("Read")) {
                    MODE = "Summarize";
                } else if (MODE.equals("Summarize")) {
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
                listView.setVisibility(View.INVISIBLE);
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
                    txtOutput = result.get(0);
                    txtOutput = txtOutput.toLowerCase();
                    String correctWithoutPunc = removePunctuation(textSegment.getText().toString()).toLowerCase();

                    if (txtOutput.equals(correctWithoutPunc)) {
                        wrongText.setText("");
                        Toast.makeText(getApplicationContext(), "Correct!", Toast.LENGTH_LONG).show();
                        wrongText.setText("");
                        if (levelsDown > 0) {
                            System.out.println("BAD------------------------------");
                            upOneLevel();

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
                        ArrayList<String> finalCorrectNoSymbol = new ArrayList<>(Arrays.asList(correctWithoutPunc.split(" ")));
                        ArrayList<String> finalUserNoSymbol = new ArrayList<>(Arrays.asList(txtOutput.split(" ")));

                        Object[] score = getScore(finalCorrectNoSymbol, finalUserNoSymbol);
                        final List<Map<String, String>> adapterData = new ArrayList<>();
                        ArrayList<String> finalCorrect = (ArrayList<String>) ((ArrayList<String>) score[1]).clone();
                        ArrayList<String> finalUser = (ArrayList<String>) ((ArrayList<String>) score[2]).clone();

                        Collections.reverse(finalCorrect);
                        Collections.reverse(finalUser);

                        Collections.reverse(finalCorrectNoSymbol);
                        Collections.reverse(finalUserNoSymbol);

                        for(int i = 0; i < finalCorrect.size();i++){

                            if(finalCorrect.get(i).equals(finalUser.get(i))){
                                continue;
                            }

                            int correctIndexBefore = i - 1;
                            int correctIndexAfter = i + 1;

                            int userIndexBefore = i - 1;
                            int userIndexAfter =  i + 1;

                            while (correctIndexBefore >= 0 && finalCorrect.get(correctIndexBefore).equals("*")) {
                                correctIndexBefore--;
                            }

                            while (correctIndexAfter < finalCorrect.size() && finalCorrect.get(correctIndexAfter).equals("*")) {
                                correctIndexAfter++;
                            }

                            while (userIndexBefore >= 0 && finalUser.get(userIndexBefore).equals("*")) {
                                userIndexBefore--;
                            }

                            while (userIndexAfter < finalUser.size() && finalUser.get(userIndexAfter).equals("*")) {
                                userIndexAfter++;
                            }

                            // Hello hi this is a test
                            // Hello hello * is a test


                            String correctText = "Correct:";
                            String userText = "You said:";

                            /*int indexInCorrectNoSymbol = finalCorrectNoSymbol.indexOf(finalCorrect.get(i));
                            int indexInUserNoSymbol = finalUserNoSymbol.indexOf(finalUser.get(i));

                            Log.e("CorrectNoSymbol", finalCorrectNoSymbol.toString());
                            Log.e("UserNoSymbol", finalUserNoSymbol.toString());*/

                            if (correctIndexBefore > -1) correctText += " " + finalCorrect.get(correctIndexBefore);
                            if (userIndexBefore > -1) userText += " " + finalUser.get(userIndexBefore);

                            correctText += " <b><u>" + finalCorrect.get(i) + "</u></b>";
                            userText += " <b><u>" + finalUser.get(i) + "</u></b>";

                            if (correctIndexAfter < finalCorrect.size()) correctText += " " + finalCorrect.get(correctIndexAfter);
                            if (userIndexAfter < finalUser.size()) userText += " " + finalUser.get(userIndexAfter);

                            if(!finalCorrect.get(i).equals("*") && !finalUser.get(i).equals("*")){
                                Map<String,String> currentData = new HashMap<>(2);

                                currentData.put("correct", correctText);
                                currentData.put("user", userText);

                                adapterData.add(currentData);

                            } else if(finalCorrect.get(i).equals("*")){
                                Map<String,String> currentData = new HashMap<>(2);
                                currentData.put("correct","You added an extra word");
                                currentData.put("user", userText);
                                adapterData.add(currentData);

                            } else {
                                Map<String,String> currentData = new HashMap<>(2);
                                currentData.put("correct",correctText);
                                currentData.put("user", "You missed this word");
                                adapterData.add(currentData);
                            }
                        }

                        SimpleAdapter adapter = new SimpleAdapter(getApplicationContext(), adapterData, android.R.layout.simple_list_item_2,
                                new String[]{"correct", "user"}, new int[]{android.R.id.text1, android.R.id.text2}){
                            @Override
                            public View getView(int position, View convertView, ViewGroup parent){
                                View view = super.getView(position,convertView,parent);
                                TextView textView1 = (TextView) view.findViewById(android.R.id.text1);
                                TextView textView2 = (TextView) view.findViewById(android.R.id.text2);

                                textView1.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.stone));
                                textView2.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.mistDark));

                                textView1.setText(Html.fromHtml(adapterData.get(position).get("correct")));
                                textView2.setText(Html.fromHtml(adapterData.get(position).get("user")));

                                return view;
                            }
                        };

                        listView.setAdapter(adapter);

                        listView.setVisibility(View.VISIBLE);

                        wrongText.setText("You got " + score[0] + "% of words correct / " + finalCorrect.toString() + " / "+finalUser.toString());

                        if (COUNT >= NUM_OF_PARAGRAPHS) {
                            levelsDown++;
                            if (MODE.equals("Memorize")) {
                                MODE = "Summarize";
                            } else if (MODE.equals("Summarize")) {
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

    private void upOneLevel() {
        levelsDown--;
        if (MODE.equals("Read")) {
            MODE = "Summarize";
        } else if (MODE.equals("Summarize")) {
            MODE = "Memorize";
        }
        COUNT += NUM_OF_PARAGRAPHS;
        newSection();
    }

    private void nextStageScreen() {
        System.out.println("IN NEXT STAGE SCREEN");
        if (COUNT + 1 >= 3 * NUM_OF_PARAGRAPHS) {
            textSegment.setText("Congrats! You've completed all three stages! If you wish, click Next to continue memorizing!");
        } else {
            textSegment.setText("Move on to the next section!");
        }

        next.setVisibility(View.VISIBLE);
        override.setVisibility(View.INVISIBLE);
        record.setVisibility(View.INVISIBLE);
    }

    private String removePunctuation(String str) {
        String newString = str;
        String[] punctuation = {"/", ".", ",", "\"", ";", ":", "(", ")", "!", "?", "-"};
        for (String aPunctuation : punctuation) {
            newString = newString.replace(aPunctuation, "");
        }
        return newString;
    }

    /*private double getPercent(ArrayList<String> userText, ArrayList<String> correctText) {
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
    }*/

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

    private ArrayList<String> toWords(String str) {
        ArrayList<String> words = new ArrayList<>();
        int lastWord = 0;
        for(int i = 0; i < str.length(); i++){
            if(Character.toString(str.charAt(i)).equals(" ") || Character.toString(str.charAt(i)).equals("!") || Character.toString(str.charAt(i)).equals("?") || Character.toString(str.charAt(i)).equals(".")){
                words.add(str.substring(lastWord, i));
                if(Character.toString(str.charAt(i)).equals(".") || Character.toString(str.charAt(i)).equals("!") || Character.toString(str.charAt(i)).equals("?")){
                    lastWord = i + 2;
                } else {
                    lastWord = i + 1;
                }
            } else if (i == str.length() - 1) {
                words.add(str.substring(lastWord));
            }
        }
        return words;
    }

    private Object[] getScore(ArrayList<String> correctStr, ArrayList<String> userStr){
        int match = 1;
        int gap = -1;
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
                int used;
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

        while (j > 0) {
            finalUser.add(userStr.get(j-1));
            finalCorrect.add("*");
            j--;
        }

        while (i > 0) {
            finalUser.add("*");
            finalCorrect.add(correctStr.get(i-1));
            i--;
        }
        /*
        while(finalCorrect.size() < finalUser.size()){
            finalCorrect.add("*");
        }

        while(finalCorrect.size() > finalUser.size()){
            finalUser.add("*");
        }
        */
        for (int k = 0;k < finalCorrect.size();k++){
            if(finalUser.get(k).equals(finalCorrect.get(k))){
                numCorrect++;
            }
        }

        return new Object[] {String.format("%.2f", 100.0*((double) numCorrect / finalCorrect.size())), finalCorrect,finalUser};
    }
}
