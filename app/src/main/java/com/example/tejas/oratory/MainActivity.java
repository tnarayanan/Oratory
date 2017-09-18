package com.example.tejas.oratory;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Button progress;
    TextView topText;
    Button load;
    Button delete;
    Context context;
    public static ArrayList<String> stringParagraphs = new ArrayList<>();




    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.roundedlogo);
        int color = ContextCompat.getColor(getApplicationContext(), R.color.white);
        setTaskDescription(new ActivityManager.TaskDescription("Oratory", icon, color));

        Window window = this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(getApplicationContext() ,R.color.colorPrimaryDark));

        topText = (TextView)findViewById(R.id.topText);
        progress = (Button)findViewById(R.id.start);
        load = (Button) findViewById(R.id.load);
        delete = (Button) findViewById(R.id.delete);
        context = this;


        progress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EnterTextActivity.class);
                startActivity(intent);
            }
        });


        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoadActivity.class);
                startActivity(intent);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(getFilesDir(), "speeches.txt");
                file.delete();
            }
        });



    }



    public static ArrayList<String> makeParagraphs(String str){
        /*
        String PARAGRAPH_SPLIT_REGEX = "(?m)(?=^\\s{4})";
        ArrayList<String> paragraphs = new ArrayList<String>(Arrays.asList(str.split(PARAGRAPH_SPLIT_REGEX)));

        return paragraphs;
         */

        ArrayList<String> paragraphs = new ArrayList<>();
        int i = 0;

        /*
        while (str.charAt(i) <= 32) {
            i++;
        }*/

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
                paragraphs.add(str.substring(lastParagraph, j - spaceStreak));
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
        paragraphs.add(str.substring(lastParagraph, str.length() - spaceStreak).replace("\t", ""));
        return paragraphs;


    }
}

