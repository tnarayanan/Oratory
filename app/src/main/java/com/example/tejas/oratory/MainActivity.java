package com.example.tejas.oratory;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    Button progress;
    TextView topText;
    EditText fullText;
    String fullTextString;
    public static ArrayList<String> stringParagraphs;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fullText = (EditText)findViewById(R.id.fullText);
        topText = (TextView)findViewById(R.id.topText);
        progress = (Button)findViewById(R.id.start);

        progress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fullTextString = fullText.getText().toString();
                stringParagraphs = makeParagraphs(fullTextString);
                Intent intent = new Intent(getApplicationContext(), Memorize.class);
                startActivity(intent);
            }
        });

    }



    public ArrayList<String> makeParagraphs(String str){
        /*
        String PARAGRAPH_SPLIT_REGEX = "(?m)(?=^\\s{4})";
        ArrayList<String> paragraphs = new ArrayList<String>(Arrays.asList(str.split(PARAGRAPH_SPLIT_REGEX)));

        return paragraphs;
         */

        ArrayList<String> paragraphs = new ArrayList<>();
        int i = 0;

        while (str.charAt(i) <= 32) {
            i++;
        }

        int lastParagraph = i;


        for (int k = 0; k < str.length(); k++) {
            System.out.print(str.charAt(k));
            System.out.print(" ");
            System.out.println((int) str.charAt(k));
        }




        int spaceStreak = 0;
        for(int j = i + 1; j < str.length(); j++){
            int currChar = str.charAt(j);
            if(currChar < 32) {
                paragraphs.add(str.substring(lastParagraph, j));
                while (str.charAt(j) < 32 && j < str.length()) {
                    j++;
                }
                lastParagraph = j;
                j--;
            } else if (currChar == 32 || currChar == 160) {
                spaceStreak++;
                if (spaceStreak > 1) {
                    paragraphs.add(str.substring(lastParagraph, j - 2));
                    while (str.charAt(j) <= 32 && j < str.length()) {
                        j++;
                    }
                    lastParagraph = j;
                    j--;
                }

            } else {
                spaceStreak = 0;
            }
        }
        paragraphs.add(str.substring(lastParagraph, str.length()));
        return paragraphs;


    }
}

