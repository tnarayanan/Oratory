package com.example.tejas.oratory;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

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



    public static ArrayList<String> makeParagraphs(String str){
        ArrayList<String> paragraphs = new ArrayList<String>();
        int lastParagraph = 0;
        int i = 0;

        while (str.charAt(i) < 32) {
            i++;
        }

        for(int j = i; j < str.length(); j++){
            if(str.charAt(j) < 32) {
                paragraphs.add(str.substring(lastParagraph, j));
                while (str.charAt(j) < 32) {
                    j++;
                }
                lastParagraph = j;
                j--;
            }
        }
        paragraphs.add(str.substring(lastParagraph, str.length()));
        return paragraphs;
    }
}

