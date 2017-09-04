package com.example.tejas.oratory;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    Button progress;
    TextView topText;
    EditText fullText;
    Button load;
    Button delete;
    String speechTitle;
    String fullTextString;
    Context context;
    public static ArrayList<String> stringParagraphs = new ArrayList<>();




    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.roundedlogo);
        int color = getResources().getColor(R.color.white);
        setTaskDescription(new ActivityManager.TaskDescription("Oratory", icon, color));
        fullText = (EditText)findViewById(R.id.fullText);
        topText = (TextView)findViewById(R.id.topText);
        progress = (Button)findViewById(R.id.start);
        load = (Button) findViewById(R.id.load);
        delete = (Button) findViewById(R.id.delete);
        context = this;



        progress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fullTextString = fullText.getText().toString();
                stringParagraphs = makeParagraphs(fullTextString);

                // Prompt user for speech title

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Speech Title");

                // Set up the input
                final EditText input = new EditText(context);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        speechTitle = input.getText().toString();
                        try {
                            FileOutputStream outputStream = openFileOutput("speeches.txt", Context.MODE_APPEND);
                            /*outputStream.write("   \n".getBytes());
                            outputStream.write((speechTitle + "\n").getBytes());
                            for(String str : stringParagraphs) {
                                outputStream.write((str + "\n").getBytes());
                            }*/


                            InputStream inputStream = openFileInput("speeches.txt");

                            boolean speechTitleExists = false;
                            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                            String recieveString;
                            //System.out.println("GARBANZO");
                            while ((recieveString = bufferedReader.readLine()) != null) {
                                //System.out.println("GARBANZO");
                                if (recieveString.equals("   ")) {
                                    if (bufferedReader.readLine().equals(speechTitle)) {
                                        speechTitleExists = true;
                                        break;
                                    }
                                }
                            }
                            bufferedReader.close();

                            if (!speechTitleExists) {
                                //Toast.makeText(getApplicationContext(), "DOES NOT EXIST", Toast.LENGTH_LONG).show();
                                outputStream.write("   \n".getBytes());
                                outputStream.write((speechTitle + "\n").getBytes());
                                for(String str : stringParagraphs) {
                                    outputStream.write((str + "\n").getBytes());
                                }
                                outputStream.close();
                            } else {
                                //Toast.makeText(getApplicationContext(), "EXISTS", Toast.LENGTH_LONG).show();
                                new AlertDialog.Builder(context)
                                        .setTitle("Duplicate Title")
                                        .setMessage("Please enter a different title for the speech.")
                                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        })
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();
                            }

                            outputStream.close();



                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        Intent intent = new Intent(getApplicationContext(), Memorize.class);
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();


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



    public ArrayList<String> makeParagraphs(String str){
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

